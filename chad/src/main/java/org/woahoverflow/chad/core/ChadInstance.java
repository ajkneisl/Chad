package org.woahoverflow.chad.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.google.common.net.HttpHeaders;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.woahoverflow.chad.core.listener.*;
import org.woahoverflow.chad.framework.handle.ArgumentHandler;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.ThreadHandler;
import org.woahoverflow.chad.framework.ui.UI;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.RequestBuffer;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.woahoverflow.chad.core.ChadVar.eightBallResults;
import static org.woahoverflow.chad.core.ChadVar.swearWords;

/**
 * Main class within Chad
 *
 * @author sho, codebasepw
 */
public final class ChadInstance {

    /**
     * The local logger
     */
    private static final Logger logger = LoggerFactory.getLogger("Chad");

    /**
     * Access the logger
     *
     * @return The local logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /*
    Makes sure bot.json is filled
     */
    static {
        JsonHandler.INSTANCE.forceCheck();

        // No UI due to servers and stuff
        if (JsonHandler.INSTANCE.get("token").isEmpty() || JsonHandler.INSTANCE.get("uri_link").isEmpty()) {
            getLogger().error("Bot.json is not filled!");

            // Exits
            System.exit(1);
        }
    }

    /**
     * Main Client Instance
     */
    public static final IDiscordClient cli = new ClientBuilder().withToken(JsonHandler.INSTANCE.get("token")).withRecommendedShardCount().build();

    /**
     * Main Method
     *
     * @param args Java Arguments
     */
    public static void main(String[] args) {
        // Disables MongoDB's logging, as it's just clutter and not really needed
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);

        getLogger().debug("woahoverflow: Chad ("+ChadVar.VERSION+")");

        ArgumentHandler.load(args);

        // Logs in and registers the listeners
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageReceived(), new OnReady(), new UserLeaveJoin(), new MessageEditEvent());

        // Initializes the framework & a lot of stuff
        long start = System.currentTimeMillis();
        ChadInstance.getLogger().debug("Starting bot...");

        ThreadHandler.initThreads();

        UI.handle = new UI();

        Thread ex = new Thread(() -> {
            Objects.requireNonNull(JsonHandler.INSTANCE.readArray("https://cdn.woahoverflow.org/data/chad/swears.json")).forEach((word) -> swearWords.add((String) word));
            Objects.requireNonNull(JsonHandler.INSTANCE.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json")).forEach((word) -> eightBallResults.add((String) word));
            Objects.requireNonNull(JsonHandler.INSTANCE.readArray("https://cdn.woahoverflow.org/data/chad/presence.json")).forEach((v) -> ChadVar.presenceRotation.add((String) v));
            Objects.requireNonNull(JsonHandler.INSTANCE.readArray("https://cdn.woahoverflow.org/data/contributors.json")).forEach((v) -> {
                if (Boolean.parseBoolean(((JSONObject) v).getString("allow"))) {
                    UI.handle.addLog("Added user " + ((JSONObject) v).getString("display_name") + " to group System Administrator", UI.LogLevel.INFO);
                    ChadVar.DEVELOPERS.add(((JSONObject) v).getLong("id"));
                } else {
                    UI.handle.addLog("Avoided adding user " + ((JSONObject) v).getString("display_name"), UI.LogLevel.INFO);
                }
            });

            try {
                URL url = new URL("https://cdn.woahoverflow.org/data/chad/words.txt");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", HttpHeaders.USER_AGENT);
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                ChadVar.wordsList = in.lines().collect(Collectors.toList());
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        ex.start();

        // Sets up timers & other stuff
        Thread timerTh = new Thread(() -> {
            // Updates all guild stats
            RequestBuffer.request(cli::getGuilds).get().forEach(guild -> GuildHandler.getGuild(guild.getLongID()).updateStatistics());

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    RequestBuffer.request(ChadInstance.cli::getGuilds).get().forEach(guild -> GuildHandler.getGuild(guild.getLongID()).updateStatistics());
                }
            }, 0, 1000*60*60); // Every hour
        });

        timerTh.start();

        while (timerTh.isAlive() || ex.isAlive()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ChadInstance.getLogger().debug("Completed startup! Took " + (System.currentTimeMillis() - start) + "ms");
    }

}