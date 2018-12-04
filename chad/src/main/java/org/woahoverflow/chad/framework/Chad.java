package org.woahoverflow.chad.framework;

import static org.woahoverflow.chad.core.ChadVar.swearWords;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.bson.Document;
import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.JSONHandler;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import org.woahoverflow.chad.framework.ui.UIHandler.LogLevel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

/**
 * Main framework for Chad
 *
 * @author sho
 * @since 0.6.3 B2
 */
public final class Chad
{

    /**
     * A Cached Guild
     */
    public static final class CachedGuild
    {
        private String lastCached;
        private final IGuild guild;
        private Document document;

        /**
         * Constructor
         *
         * @param guild The guild to be cached
         */
        public CachedGuild(IGuild guild)
        {
            this.guild = guild;
            lastCached = Util.getTimeStamp();
            cache();
        }

        /**
         * ReCaches the guild
         */
        public void cache()
        {
            Document get = DatabaseHandler.handle.getCollection().find(new Document("guildid", guild.getStringID())).first();

            if (get == null)
                return;

            document = get;
            lastCached = Util.getTimeStamp();
        }

        /**
         * @return The guild
         */
        public IGuild getGuild() {
            return guild;
        }

        /**
         * @return The cached document
         */
        public Document getDocument() {
            return document;
        }

        /**
         * @return The date it's last cached
         */
        public String getLastCached() {
            return lastCached;
        }
    }

    /**
     * A Thread Consumer
     */
    public static final class ThreadConsumer
    {
        private final boolean discordUser;
        private IUser user;

        /**
         * Local Constructor for non IUsers
         */
        ThreadConsumer()
        {
            discordUser = false;
        }

        /**
         * Local Constructor for IUsers
         *
         * @param user The user to create it for
         */
        ThreadConsumer(IUser user)
        {
            this.user = user;
            discordUser = true;
        }

        /**
         * @return If the user is an IUser
         */
        public boolean isDiscordUser() {
            return discordUser;
        }

        /**
         * @return The user
         */
        public IUser getUser() {
            return user;
        }
    }

    public static int runningThreads;
    public static int internalRunningThreads;
    public static final ConcurrentHashMap<IGuild, CachedGuild> cachedGuilds = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(30);
    private static final ThreadConsumer internalThreadConsumer = new ThreadConsumer();
    public static final ConcurrentHashMap<ThreadConsumer, ArrayList<Future<?>>> threadHash = new ConcurrentHashMap<>();

    /**
     * Global Init Event
     */
    public static void init()
    {
        // To account for the main thread :)
        internalRunningThreads++;
        
        /*
        Chad's Thread Counting
         */
        runThread(
            () -> new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!threadHash.isEmpty())
                    {
                        threadHash.forEach((key, val) -> {
                            if (!val.isEmpty())
                            {
                                for (int i = 0; val.size() > i; i++)
                                {
                                    if (val.get(i).isDone())
                                    {
                                        val.remove(val.get(i));
                                        if (key.isDiscordUser())
                                        {
                                            runningThreads--;
                                        }
                                        else {
                                            internalRunningThreads--;
                                        }
                                    }
                                }
                            }
                            else {
                                threadHash.remove(key);
                            }
                        });
                    }
                }
            }, 0, 1000), getInternalConsumer() // gets internal consumer
        );

        /*
        Chad's Caching
         */
        runThread(() -> {
            List<IGuild> guilds = RequestBuffer.request(ChadBot.cli::getGuilds).get();
            guilds.forEach((guild) -> cachedGuilds.put(guild, new CachedGuild(guild)));
        }, getInternalConsumer());

        /*
        Chad's UI
         */
        UIHandler.handle = new UIHandler(ChadBot.cli);

        /*
        Swear Words
         */
        runThread(() -> JSONHandler.handle.readArray("https://cdn.woahoverflow.org/chad/data/swears.json").forEach((word) -> swearWords.add((String) word)), getInternalConsumer());

        /*
        Developers
         */
        runThread(() -> JSONHandler.handle.readArray("https://cdn.woahoverflow.org/chad/data/contributors.json").forEach((v) ->
        {
            if (Boolean.parseBoolean(((JSONObject) v).getString("allow")))
            {
                UIHandler.handle.addLog("Added user " + ((JSONObject) v).getString("display_name") + " to group System Administrator", LogLevel.INFO);
                ChadVar.GLOBAL_PERMISSIONS.put(((JSONObject) v).getString("id"), PermissionHandler.Levels.SYSTEM_ADMINISTRATOR);
            }
            else {
                UIHandler.handle.addLog("Avoided adding user " + ((JSONObject) v).getString("display_name"), LogLevel.INFO);
            }
        }), getInternalConsumer());

        /*
        Adds all the presences
         */
        runThread(() -> JSONHandler.handle.readArray("https://cdn.woahoverflow.org/chad/data/presence.json").forEach((v) -> ChadVar.presenceRotation.add((String) v)), getInternalConsumer());
    }

    /**
     * Gets a cached guild
     *
     * @param guild The guild to get a cached version of
     * @return The cached guild
     */
    public static CachedGuild getGuild(IGuild guild)
    {
        // If it contains the guild, which it should, return it
        if (cachedGuilds.keySet().contains(guild))
            return cachedGuilds.get(guild);

        // if the guild wasn't cached, cache it
        cachedGuilds.put(guild, new CachedGuild(guild));
        return cachedGuilds.get(guild);
    }

    /**
     * UnCaches a guild
     *
     * @param guild The guild to be uncached
     */
    public static void unCacheGuild(IGuild guild)
    {
        cachedGuilds.remove(guild);
    }

    /**
     * Gets a user's thread consumer
     *
     * @param user The user
     * @return The user's thread consumer
     */
    public static ThreadConsumer getConsumer(IUser user)
    {
        for (ThreadConsumer cons : threadHash.keySet())
            if (cons.isDiscordUser() && cons.getUser().equals(user))
                return cons;
        return new ThreadConsumer(user);
    }

    /**
     * Gets the internal thread consumer
     *
     * @return The local internal consumer
     */
    public static ThreadConsumer getInternalConsumer()
    {
        return internalThreadConsumer;
    }

    /**
     * Runs a thread
     *
     * @param thread The thread to be run
     * @param consumer The consumer to tie it to
     */
    public static void runThread(Runnable thread, ThreadConsumer consumer)
    {
        // If they're a discord user, add a running thread to the default
        if (consumer.isDiscordUser())
            runningThreads++;
        else
            internalRunningThreads++; // If not, it's an internal thread

        // Run the thread
        Future<?> ranThread = executorService.submit(thread);

        // Add it to the hashmap
        if (threadHash.containsKey(consumer))
        {
            ArrayList<Future<?>> th = threadHash.get(consumer);
            th.add(ranThread);
            threadHash.put(consumer, th);
        }
        else {
            ArrayList<Future<?>> th = new ArrayList<>();
            th.add(ranThread);
            threadHash.put(consumer, th);
        }
    }

    /**
     * Makes sure a thread consumer can run it
     *
     * @param consumer The thread consumer
     * @return If it can run it
     */
    public static boolean consumerRunThread(ThreadConsumer consumer)
    {
        return threadHash.get(consumer) == null || threadHash.get(consumer).size() < 3;
    }
}
