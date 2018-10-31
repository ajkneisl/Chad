package com.jhobot.core;

import com.jhobot.core.listener.GuildJoinLeave;
import com.jhobot.core.listener.MessageRecieved;
import com.jhobot.core.listener.OnReady;
import com.jhobot.core.listener.UserLeaveJoin;
import com.jhobot.handle.JSONHandler;
import com.jhobot.handle.commands.permissions.PermissionLevels;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ChadBot {
    static {
        if (new JSONHandler().forceCheck().get("token").equals(""))
        {
            System.out.println("No Token!");
            System.exit(1);
        }
    }
    public static final IDiscordClient cli = new ClientBuilder().withToken(new JSONHandler().forceCheck().get("token")).withRecommendedShardCount().build();

    public static void main(String[] args)
    {
        if (args.length == 1 && args[0].equalsIgnoreCase("denyui"))
        {
            ChadVar.ALLOW_UI = false;
        }

        // add developer ids to the permissions handler
        ChadVar.GLOBAL_PERMISSIONS.put("173495550467899402", PermissionLevels.SYSTEM_ADMINISTRATOR); //CodeBase
        ChadVar.GLOBAL_PERMISSIONS.put("416399667094618124", PermissionLevels.SYSTEM_ADMINISTRATOR); // sho

        // logs in and registers the listener
        cli.login();
        cli.getDispatcher().registerListeners(new GuildJoinLeave(), new MessageRecieved(), new OnReady(), new UserLeaveJoin());
    }

}
