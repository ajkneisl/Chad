package com.jhobot.core;

import com.jhobot.commands.admin.*;
import com.jhobot.commands.fun.*;
import com.jhobot.commands.function.Logging;
import com.jhobot.commands.function.Message;
import com.jhobot.commands.function.Prefix;
import com.jhobot.commands.function.Purge;
import com.jhobot.commands.info.*;
import com.jhobot.commands.punishments.Ban;
import com.jhobot.commands.punishments.Kick;
import com.jhobot.handle.DatabaseHandler;
import com.jhobot.handle.DebugHandler;
import com.jhobot.handle.JSONHandler;
import com.jhobot.handle.commands.PermissionLevels;
import com.jhobot.handle.commands.PermissionsHandler;
import com.jhobot.handle.commands.ThreadCountHandler;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import org.json.JSONObject;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChadBot {
    public static final JSONHandler JSON_HANDLER = new JSONHandler().forceCheck();
    public static final DatabaseHandler DATABASE_HANDLER = new DatabaseHandler(JSON_HANDLER.get("uri_link"));
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(30);
    public static final PermissionsHandler PERMISSIONS_HANDLER = new PermissionsHandler();
    public static final DebugHandler DEBUG_HANDLER = new DebugHandler();
    
    // Checks if there's a token in the bot.json (if not it exits the program)
    static {
        if (ChadBot.JSON_HANDLER.get("token").equals(""))
        {
            System.out.println("No Token!");
            System.exit(1);
        }
    }
    
    public static final IDiscordClient cli = new ClientBuilder().withToken(ChadBot.JSON_HANDLER.get("token")).withRecommendedShardCount().build();
    
    public static void main(String[] args)
    {
        // register commands
        registerCommands();

        // add developer ids to the permissions handler
        PermissionHandler.GLOBAL_PERMISSIONS.put("173495550467899402", PermissionLevels.SYSTEM_ADMINISTRATOR);
        PermissionHandler.GLOBAL_PERMISSIONS.put("416399667094618124", PermissionLevels.SYSTEM_ADMINISTRATOR);

        // logs in and registers the listener
        cli.login();
        cli.getDispatcher().registerListener(new Listener());
    }

    public static void registerCommands() {
        Listener.hash.put("userinfo", new UserInfo());
        Listener.hash.put("kick", new Kick());
        Listener.hash.put("ban", new Ban());
        Listener.hash.put("updatelog", new UpdateLog());
        Listener.hash.put("steam", new Steam());
        Listener.hash.put("chad", new Chad());
        Listener.hash.put("guildinfo", new GuildInfo());
        Listener.hash.put("prefix", new Prefix());
        Listener.hash.put("logging", new Logging());
        Listener.hash.put("random", new com.jhobot.commands.fun.Random());
        Listener.hash.put("pe", new PhotoEditor());
        Listener.hash.put("8ball", new EightBall());
        Listener.hash.put("catgallery", new CatGallery());
        Listener.hash.put("catfact", new CatFact());
        Listener.hash.put("help", new Help());
        Listener.hash.put("rrl", new RussianRoulette());
        Listener.hash.put("purge", new Purge());
        Listener.hash.put("im", new Message());
        Listener.hash.put("threads", new CurrentThreads()); // admin only/debug
        Listener.hash.put("rtop", new RedditTop());
        Listener.hash.put("rnew", new RedditNew());
        Listener.hash.put("systeminfo", new SystemInfo());
        Listener.hash.put("modpresence", new ModifyPresence()); // admin only
        Listener.hash.put("perms", new com.jhobot.commands.function.Permissions());
        Listener.hash.put("debugger", new Debugger());
    }
}
