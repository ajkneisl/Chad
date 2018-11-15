package org.woahoverflow.chad.core;

import org.woahoverflow.chad.core.listener.GuildJoinLeave;
import org.woahoverflow.chad.core.listener.MessageRecieved;
import org.woahoverflow.chad.core.listener.OnReady;
import org.woahoverflow.chad.core.listener.UserLeaveJoin;
import org.woahoverflow.chad.handle.JSONHandler;
import org.woahoverflow.chad.handle.logging.LogLevel;
import org.woahoverflow.chad.handle.ui.ChadException;
import org.json.JSONObject;
import org.woahoverflow.chad.handle.commands.permissions.PermissionLevels;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.util.Arrays;

public class ChadBot {
    static {
        JSONHandler h = new JSONHandler().forceCheck();
        if (h.get("token").equals("") || h.get("uri_link").equals(""))
        {
            ChadVar.setUiHandler();
            ChadException.error("bot.json is missing values!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.exit(1);
        }
    }
    public static final IDiscordClient cli = new ClientBuilder().withToken(new JSONHandler().forceCheck().get("token")).withRecommendedShardCount().build();

    public static void main(String[] args)
    {
        if (args.length >= 1 && Arrays.asList(args).contains("denyui"))
        {
            ChadVar.ALLOW_UI = false;
        }
        if (args.length >= 1 && Arrays.asList(args).contains("unstable"))
        {
            ChadVar.ALLOW_UNSTABLE = true;
        }


        ChadVar.setJsonHandler();
        ChadVar.setDatabaseHandler();
        ChadVar.setCacheDevice();

        // logs in and registers the listener
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageRecieved(), new OnReady(), new UserLeaveJoin());

        if (ChadVar.ALLOW_UI)
            ChadVar.setUiHandler();

        // add developer ids to the permissions handler
        ChadVar.JSON_HANDLER.readArray("https://raw.githubusercontent.com/woahoverflow/Chad-Repo/master/data/contributors.json").forEach((v) ->
        {
            ChadVar.UI_HANDLER.addLog("Added user " + ((JSONObject) v).getString("display_name") + " to group System Administrator", LogLevel.INFO);
            ChadVar.GLOBAL_PERMISSIONS.put(((JSONObject) v).getString("id"), PermissionLevels.SYSTEM_ADMINISTRATOR);
        });
    }

}