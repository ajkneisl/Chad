package org.woahoverflow.chad.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.woahoverflow.chad.core.listener.*;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.RequestBuffer;

/**
 * Main class within Chad
 *
 * @author sho, codebasepw
 */
public final class ChadInstance {

    /**
     * The local logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ChadInstance.class);

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
        // No UI due to servers and stuff
        if (JsonHandler.handle.get("token").isEmpty() || JsonHandler.handle.get("uri_link").isEmpty()) {
            getLogger().error("Bot.json is empty!");
            // Exits
            System.exit(1);
        }
    }

    /**
     * Main Client Instance
     */
    public static final IDiscordClient cli = new ClientBuilder().withToken(JsonHandler.handle.get("token")).withRecommendedShardCount().build();

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

        getLogger().debug("woahoverflow: chad (v0.8.0)");

        // Calculates the launch options
        if (args.length >= 1) {
            for (int i = 0; args.length > i; i++) {
                final int i2 = i;
                ChadVar.launchOptions.forEach((st, bol) -> {
                    // If the launch option is valid, enter it
                    if (args[i2].equalsIgnoreCase(st)) {
                        ChadVar.launchOptions.put(st, true);
                        getLogger().debug("{} has been enabled", st);
                    }
                });
            }
        }

        // Logs in and registers the listeners
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageReceived(), new OnReady(), new UserLeaveJoin(), new MessageEditEvent());

        // Initializes the framework & a lot of stuff
        Chad.init();

        // Logs out of the client on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Saves all of the guild's data
            RequestBuffer.request(cli::getGuilds).get().forEach(guild -> GuildHandler.handle.getGuild(guild.getLongID()).updateStatistics());

            // Logout
            cli.logout();
        }));
    }

}
