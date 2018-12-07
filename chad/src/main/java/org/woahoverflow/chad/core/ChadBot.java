package org.woahoverflow.chad.core;

import org.woahoverflow.chad.core.listener.GuildJoinLeave;
import org.woahoverflow.chad.core.listener.MessageEditEvent;
import org.woahoverflow.chad.core.listener.MessageRecieved;
import org.woahoverflow.chad.core.listener.OnReady;
import org.woahoverflow.chad.core.listener.UserLeaveJoin;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import org.woahoverflow.chad.framework.ui.UIHandler.LogLevel;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

/**
 * Main class within Chad
 *
 * @author sho, codebasepw
 * @since forever
 */
public final class ChadBot {

    /*
    Makes sure bot.json is fulled
     */
    static
    {
        JsonHandler h = new JsonHandler().forceCheck();
        if (h.get("token").isEmpty() || h.get("uri_link").isEmpty())
        {
            UIHandler handle = new UIHandler();
            handle.addLog("bot.json is empty!", LogLevel.SEVERE);
            // Exits
            System.exit(1);
        }
    }

    /**
     * Main Client Instance
     */
    public static final IDiscordClient cli = new ClientBuilder().withToken(new JsonHandler().forceCheck().get("token")).withRecommendedShardCount().build();

    /**
     * Main Method
     *
     * @param args Java Arguments
     */
    public static void main(String[] args)
    {
        // Logs in and registers the listeners
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageRecieved(), new OnReady(), new UserLeaveJoin(), new MessageEditEvent());

        // Initializes the framework & a lot of stuff
        Chad.init();
    }

}
