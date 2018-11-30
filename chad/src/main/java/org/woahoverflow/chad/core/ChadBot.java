package org.woahoverflow.chad.core;

import org.json.JSONObject;
import org.woahoverflow.chad.core.listener.GuildJoinLeave;
import org.woahoverflow.chad.core.listener.MessageEditEvent;
import org.woahoverflow.chad.core.listener.MessageRecieved;
import org.woahoverflow.chad.core.listener.OnReady;
import org.woahoverflow.chad.core.listener.UserLeaveJoin;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.JSONHandler;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import org.woahoverflow.chad.framework.ui.UIHandler.LogLevel;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public final class ChadBot {
    // Makes sure the token and URI_LINK values in bot.json are filled in.
    static
    {
        JSONHandler h = new JSONHandler().forceCheck();
        if (h.get("token").isEmpty() || h.get("uri_link").isEmpty())
        {
            UIHandler handle = new UIHandler(null);
            handle.addLog("bot.json is empty!", LogLevel.SEVERE);
            // Exits
            System.exit(1);
        }
    }

    public static final IDiscordClient cli = new ClientBuilder().withToken(new JSONHandler().forceCheck().get("token")).withRecommendedShardCount().build();

    public static void main(String[] args)
    {
        // Logs in and registers the listeners
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageRecieved(), new OnReady(), new UserLeaveJoin(), new MessageEditEvent());

        // Initializes the framework & a lot of stuff
        Chad.init();
    }

}
