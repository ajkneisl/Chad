package org.woahoverflow.chad.core;

import org.json.JSONObject;
import org.woahoverflow.chad.core.listener.*;
import org.woahoverflow.chad.handle.JSONHandler;
import org.woahoverflow.chad.handle.commands.PermissionHandler;
import org.woahoverflow.chad.handle.ui.ChadException;
import org.woahoverflow.chad.handle.ui.UIHandler;
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


        ChadVar.setJsonHandler();
        ChadVar.setDatabaseHandler();
        ChadVar.setCacheDevice();

        // logs in and registers the listener
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageRecieved(), new OnReady(), new UserLeaveJoin(), new Punishments());

        if (ChadVar.ALLOW_UI)
            ChadVar.setUiHandler();

        // add developer ids to the permissions handler
        ChadVar.JSON_HANDLER.readArray("https://cdn.woahoverflow.org/chad/data/contributors.json").forEach((v) ->
        {
            if (Boolean.parseBoolean(((JSONObject) v).getString("allow")))
            {
                ChadVar.UI_HANDLER.addLog("Added user " + ((JSONObject) v).getString("display_name") + " to group System Administrator", UIHandler.LogLevel.INFO);
                ChadVar.GLOBAL_PERMISSIONS.put(((JSONObject) v).getString("id"), PermissionHandler.Levels.SYSTEM_ADMINISTRATOR);
            }
            else {
                ChadVar.UI_HANDLER.addLog("Avoided adding user " + ((JSONObject) v).getString("display_name"), UIHandler.LogLevel.INFO);
            }
        });
    }

}
