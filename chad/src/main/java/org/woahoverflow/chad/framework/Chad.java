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

// This class is the main framework for running commands
public final class Chad
{
    public static final class CachedGuild
    {
        private String lastCached;
        private final IGuild guild;
        private Document document;

        public CachedGuild(IGuild guild)
        {
            this.guild = guild;
            lastCached = Util.getTimeStamp();
            cache();
        }

        public void cache()
        {
            Document get = DatabaseHandler.handle.getCollection().find(new Document("guildid", guild.getStringID())).first();

            if (get == null)
                return;

            document = get;
            lastCached = Util.getTimeStamp();
        }

        public IGuild getGuild() {
            return guild;
        }

        public Document getDocument() {
            return document;
        }

        public String getLastCached() {
            return lastCached;
        }
    }

    public static final class ThreadConsumer
    {
        private final boolean discordUser;
        private IUser user;

        ThreadConsumer()
        {
            discordUser = false;
        }

        ThreadConsumer(IUser user)
        {
            this.user = user;
            discordUser = true;
        }

        public boolean isDiscordUser() {
            return discordUser;
        }

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

    // Global Initialization Event
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
    }

    public static CachedGuild getGuild(IGuild guild)
    {
        // If it contains the guild, which it should, return it
        if (cachedGuilds.keySet().contains(guild))
            return cachedGuilds.get(guild);

        // if the guild wasn't cached, cache it
        cachedGuilds.put(guild, new CachedGuild(guild));
        return cachedGuilds.get(guild);
    }

    public static void unCacheGuild(IGuild guild)
    {
        cachedGuilds.remove(guild);
    }

    // returns a user's threadconsumer
    public static ThreadConsumer getConsumer(IUser user)
    {
        for (ThreadConsumer cons : threadHash.keySet())
            if (cons.isDiscordUser() && cons.getUser().equals(user))
                return cons;
        return new ThreadConsumer(user);
    }

    // returns the internal thread consumer
    public static ThreadConsumer getInternalConsumer()
    {
        return internalThreadConsumer;
    }

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

    public static boolean consumerRunThread(ThreadConsumer consumer)
    {
        return threadHash.get(consumer) == null || threadHash.get(consumer).size() < 3;
    }
}
