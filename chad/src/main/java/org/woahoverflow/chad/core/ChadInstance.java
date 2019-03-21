package org.woahoverflow.chad.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.woahoverflow.chad.core.listener.*;
import org.woahoverflow.chad.framework.handle.ArgumentHandler;
import org.woahoverflow.chad.framework.handle.InitKt;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.util.Timer;

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
     * Timer
     */
    private static final Timer timer = new Timer();

    /**
     * Access the logger
     *
     * @return The local logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Access the timer
     *
     * @return The local timer
     */
    public static Timer getTimer() {
        return timer;
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

        getLogger().info("woahoverflow: Chad ("+ ChadVar.VERSION +")");

        ArgumentHandler.load(args);

        // Logs in and registers the listeners
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageReceived(), new OnReady(), new UserLeaveJoin(), new MessageEditEvent());

        // Initializes the framework & a lot of stuff
        InitKt.init();
    }

}