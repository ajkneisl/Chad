package com.jhobot.core;

import com.jhobot.commands.admin.*;
import com.jhobot.commands.fun.*;
import com.jhobot.commands.function.*;
import com.jhobot.commands.info.*;
import com.jhobot.commands.punishments.Ban;
import com.jhobot.commands.punishments.Kick;
import com.jhobot.handle.DatabaseHandler;
import com.jhobot.handle.DebugHandler;
import com.jhobot.handle.JSONHandler;
import com.jhobot.handle.MetaData;
import com.jhobot.handle.commands.Category;
import com.jhobot.handle.commands.PermissionLevels;
import com.jhobot.handle.commands.PermissionsHandler;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"WeakerAccess", "unused"})
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

        // register presence rotation
        registerPresenceRotation();

        // add developer ids to the permissions handler
        PermissionHandler.GLOBAL_PERMISSIONS.put("173495550467899402", PermissionLevels.SYSTEM_ADMINISTRATOR); //CodeBase
        PermissionHandler.GLOBAL_PERMISSIONS.put("416399667094618124", PermissionLevels.SYSTEM_ADMINISTRATOR); // sho

        // logs in and registers the listener
        cli.login();
        cli.getDispatcher().registerListener(new Listener());
    }

    public static void registerCommands() {
        // fun
        Listener.hash.put("random", new com.jhobot.commands.fun.Random());
        Listener.metaData.put("random", new MetaData(Category.FUN, false)); // Random
        Listener.hash.put("pe", new PhotoEditor());
        Listener.metaData.put("pe", new MetaData(Category.FUN, false)); // PhotoEditor
        Listener.hash.put("8ball", new EightBall());
        Listener.metaData.put("8ball", new MetaData(Category.FUN, false)); // EightBall
        Listener.hash.put("catgallery", new CatGallery());
        Listener.metaData.put("catgallery", new MetaData(Category.FUN, false)); // CatGallery
        Listener.hash.put("catfact", new CatFact());
        Listener.metaData.put("catfact", new MetaData(Category.FUN, false)); // CatFact
        Listener.hash.put("rrl", new RussianRoulette());
        Listener.metaData.put("rrl", new MetaData(Category.FUN, false)); // RussianRoulette
        // info
        Listener.hash.put("help", new Help());
        Listener.metaData.put("help", new MetaData(Category.INFO, false)); // Help
        Listener.hash.put("userinfo", new UserInfo());
        Listener.metaData.put("userinfo", new MetaData(Category.INFO, false)); // UserInfo
        Listener.hash.put("steam", new Steam());
        Listener.metaData.put("steam", new MetaData(Category.INFO, false)); // Steam
        Listener.hash.put("chad", new Chad());
        Listener.metaData.put("chad", new MetaData(Category.INFO, false)); // Chad
        Listener.hash.put("guildinfo", new GuildInfo());
        Listener.metaData.put("guildinfo", new MetaData(Category.INFO, false)); // GuildInfo
        Listener.hash.put("rtop", new RedditTop());
        Listener.metaData.put("rtop", new MetaData(Category.INFO, false)); // RedditTop
        Listener.hash.put("rnew", new RedditNew());
        Listener.metaData.put("rnew", new MetaData(Category.INFO, false)); // ReditNew
        Listener.hash.put("contributions", new Contributors());
        Listener.metaData.put("contributions", new MetaData(Category.INFO, false)); // Contributors
        Listener.hash.put("updatelog", new UpdateLog());
        Listener.metaData.put("updatelog", new MetaData(Category.INFO, false)); // UpdateLog
        // punishments
        Listener.hash.put("kick", new Kick());
        Listener.metaData.put("kick", new MetaData(Category.PUNISHMENTS, false)); // Kick
        Listener.hash.put("ban", new Ban());
        Listener.metaData.put("ban", new MetaData(Category.PUNISHMENTS, false)); // Ban
        // function
        Listener.hash.put("prefix", new Prefix());
        Listener.metaData.put("prefix", new MetaData(Category.FUNCTION, false)); // Prefix
        Listener.hash.put("logging", new Logging());
        Listener.metaData.put("logging", new MetaData(Category.FUNCTION, false)); // Logging
        Listener.hash.put("purge", new Purge());
        Listener.metaData.put("purge", new MetaData(Category.FUNCTION, false)); // Purge
        Listener.hash.put("im", new Message());
        Listener.metaData.put("im", new MetaData(Category.FUNCTION, false)); // Message
        Listener.hash.put("autorole", new AutoRole());
        Listener.metaData.put("autorole", new MetaData(Category.FUNCTION, false)); // AutoRole
        Listener.hash.put("perms", new com.jhobot.commands.function.Permissions());
        Listener.metaData.put("perms", new MetaData(Category.FUNCTION, false)); // Permissions
        // admin
        Listener.hash.put("threads", new CurrentThreads());
        Listener.metaData.put("threads", new MetaData(Category.ADMIN, true)); // Threads
        Listener.hash.put("modpresence", new ModifyPresence());
        Listener.metaData.put("modpresence", new MetaData(Category.ADMIN, true)); // ModifyPresence
        Listener.hash.put("systeminfo", new SystemInfo());
        Listener.metaData.put("systeminfo", new MetaData(Category.INFO, true)); // SystemInfo
    }

    public static void registerPresenceRotation() {
        Listener.PRESENCE_ROTATION.add("hello!");
        Listener.PRESENCE_ROTATION.add("gamers");
        Listener.PRESENCE_ROTATION.add("epic gamers");
        Listener.PRESENCE_ROTATION.add("a bad game");
        Listener.PRESENCE_ROTATION.add("j!help");
        Listener.PRESENCE_ROTATION.add("j!prefix set *");
        Listener.PRESENCE_ROTATION.add("what's going on gamers");
        Listener.PRESENCE_ROTATION.add("invite me please");
    }
}
