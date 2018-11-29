package org.woahoverflow.chad.core;

import org.json.JSONObject;
import org.woahoverflow.chad.core.listener.GuildJoinLeave;
import org.woahoverflow.chad.core.listener.MessageEditEvent;
import org.woahoverflow.chad.core.listener.MessageRecieved;
import org.woahoverflow.chad.core.listener.OnReady;
import org.woahoverflow.chad.core.listener.UserLeaveJoin;
import org.woahoverflow.chad.handle.JSONHandler;
import org.woahoverflow.chad.handle.commands.PermissionHandler;
import org.woahoverflow.chad.handle.ui.ChadError;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public final class ChadBot {
    // Makes sure the token and URI_LINK values in bot.json are filled in.
    static
    {
        JSONHandler h = new JSONHandler().forceCheck();
        if (h.get("token").isEmpty() || h.get("uri_link").isEmpty())
        {
            ChadVar.uiDevice = new UIHandler(null);
            ChadError.throwError("bot.json is missing values!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Exits
            System.exit(1);
        }
    }

    public static final IDiscordClient cli = new ClientBuilder().withToken(new JSONHandler().forceCheck().get("token")).withRecommendedShardCount().build();

    public static void main(String[] args)
    {
        // Initializes ChadVar variables.
        ChadVar.init();

        // Logs in and registers the listeners
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageRecieved(), new OnReady(), new UserLeaveJoin(), new MessageEditEvent());

        // Adds developers into the permissions.
        ChadVar.jsonDevice.readArray("https://cdn.woahoverflow.org/chad/data/contributors.json").forEach((v) ->
        {
            if (Boolean.parseBoolean(((JSONObject) v).getString("allow")))
            {
                ChadVar.uiDevice
                    .addLog("Added user " + ((JSONObject) v).getString("display_name") + " to group System Administrator", UIHandler.LogLevel.INFO);
                ChadVar.GLOBAL_PERMISSIONS.put(((JSONObject) v).getString("id"), PermissionHandler.Levels.SYSTEM_ADMINISTRATOR);
            }
            else {
                ChadVar.uiDevice.addLog("Avoided adding user " + ((JSONObject) v).getString("display_name"), UIHandler.LogLevel.INFO);
            }
        });
    }

}
