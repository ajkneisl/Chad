package com.jhobot.core;

import com.jhobot.core.listener.GuildJoinLeave;
import com.jhobot.core.listener.MessageRecieved;
import com.jhobot.core.listener.OnReady;
import com.jhobot.core.listener.UserLeaveJoin;
import com.jhobot.handle.JSONHandler;
import com.jhobot.handle.commands.permissions.PermissionLevels;
import com.jhobot.handle.ui.ChadException;
import com.jhobot.handle.ui.PopUpPanel;
import com.jhobot.handle.ui.UIHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings({"WeakerAccess", "unused"})
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
        if (args.length == 1 && args[0].equalsIgnoreCase("denyui")) {
            ChadVar.ALLOW_UI = false;
        }

        ChadVar.setJsonHandler();
        ChadVar.setDatabaseHandler();
        ChadVar.setCacheDevice();

        // add developer ids to the permissions handler
        ChadVar.GLOBAL_PERMISSIONS.put("173495550467899402", PermissionLevels.SYSTEM_ADMINISTRATOR); //CodeBase
        ChadVar.GLOBAL_PERMISSIONS.put("416399667094618124", PermissionLevels.SYSTEM_ADMINISTRATOR); // sho
        ChadVar.GLOBAL_PERMISSIONS.put("163777083418345473", PermissionLevels.SYSTEM_ADMINISTRATOR); //Styx

        // logs in and registers the listener
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageRecieved(), new OnReady(), new UserLeaveJoin());

        if (ChadVar.ALLOW_UI)
            ChadVar.setUiHandler();
    }

}