package org.woahoverflow.chad.framework;

import static org.woahoverflow.chad.core.ChadVar.musicManagers;
import static org.woahoverflow.chad.core.ChadVar.playerManager;
import static org.woahoverflow.chad.core.ChadVar.swearWords;

import com.google.common.net.HttpHeaders;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import org.woahoverflow.chad.framework.ui.UIHandler.LogLevel;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Main framework for Chad
 *
 * @author sho
 * @since 0.6.3 B2
 */
public final class Chad
{
    /**
     * A Thread Consumer
     */
    public static final class ThreadConsumer
    {
        private final boolean discordUser;
        private long userId;

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
         * @param userId The user to create it for
         */
        ThreadConsumer(long userId)
        {
            this.userId = userId;
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
        public long getUserId() {
            return userId;
        }
    }

    /**
     * The amount of user running threads
     */
    public static int runningThreads;

    /**
     * The amount of internal threads running
     */
    public static int internalRunningThreads;

    /**
     * The executor service, where every thread runs
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(30);

    /**
     * The internal thread consumer
     */
    private static final ThreadConsumer internalThreadConsumer = new ThreadConsumer();

    /**
     * User's thread consumer
     */
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
        Chad's UI
         */
        UIHandler.handle = new UIHandler();

        /*
        Swear Words
         */
        runThread(() -> JsonHandler.handle.readArray("https://cdn.woahoverflow.org/chad/data/swears.json").forEach((word) -> swearWords.add((String) word)), getInternalConsumer());

        /*
        Developers
         */
        runThread(() -> JsonHandler.handle.readArray("https://cdn.woahoverflow.org/chad/data/contributors.json").forEach((v) ->
        {
            if (Boolean.parseBoolean(((JSONObject) v).getString("allow")))
            {
                UIHandler.handle
                    .addLog("Added user " + ((JSONObject) v).getString("display_name") + " to group System Administrator", LogLevel.INFO);
                ChadVar.DEVELOPERS.add(((JSONObject) v).getLong("id"));
            }
            else {
                UIHandler.handle.addLog("Avoided adding user " + ((JSONObject) v).getString("display_name"), LogLevel.INFO);
            }
        }), getInternalConsumer());

        /*
        Adds all the presences
         */
        runThread(() -> JsonHandler.handle.readArray("https://cdn.woahoverflow.org/chad/data/presence.json").forEach((v) -> ChadVar.presenceRotation.add((String) v)), getInternalConsumer());

        /*
        Gets all the words from the CDN
         */
        runThread(() ->
        {
            try {
                // Defines the URL and Connection
                URL url = new URL("https://cdn.woahoverflow.org/chad/data/words.txt");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                // Sets the properties of the connection
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", HttpHeaders.USER_AGENT);

                @SuppressWarnings("all")
                // Creates a buffered reader at the word url
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

                // Adds the words to the list
                ChadVar.wordsList = in.lines().collect(Collectors.toList());

                // Closes the reader
                in.close();
            } catch (@SuppressWarnings("all") IOException e1) {
                e1.printStackTrace();
            }
        }, getInternalConsumer());
    }

    /**
     * Gets a guild's audio player
     */
    public static synchronized GuildMusicManager getMusicManager(IGuild guild)
    {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null)
        {
            musicManager = new GuildMusicManager(playerManager, guildId);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }


    /**
     * Gets a user's thread consumer
     *
     * @param userId The user's ID
     * @return The user's thread consumer
     */
    public static ThreadConsumer getConsumer(long userId)
    {
        for (ThreadConsumer cons : threadHash.keySet())
            if (cons.isDiscordUser() && cons.getUserId() == (userId))
                return cons;
        return new ThreadConsumer(userId);
    }

    /**
     * Gets the internal thread consumer
     *
     * @return The local internal consumer
     */
    public static synchronized ThreadConsumer getInternalConsumer()
    {
        return internalThreadConsumer;
    }

    /**
     * Runs a thread
     *
     * @param thread The thread to be run
     * @param consumer The consumer to tie it to
     */
    public static synchronized void runThread(Runnable thread, ThreadConsumer consumer)
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

        ChadBot.getLogger().info("{}", internalRunningThreads);
    }

    /**
     * Makes sure a thread consumer can run it
     *
     * @param consumer The thread consumer
     * @return If it can run it
     */
    public static synchronized boolean consumerRunThread(ThreadConsumer consumer)
    {
        return threadHash.get(consumer) == null || threadHash.get(consumer).size() < 3;
    }
}
