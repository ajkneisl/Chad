package com.jhobot.core;

import com.jhobot.commands.admin.CurrentThreads;
import com.jhobot.commands.admin.ModifyPresence;
import com.jhobot.commands.fun.*;
import com.jhobot.commands.function.*;
import com.jhobot.commands.info.*;
import com.jhobot.commands.info.SystemInfo;
import com.jhobot.commands.nsfw.*;
import com.jhobot.commands.punishments.Ban;
import com.jhobot.commands.punishments.Kick;
import com.jhobot.handle.DatabaseHandler;
import com.jhobot.handle.DebugHandler;
import com.jhobot.handle.JSONHandler;
import com.jhobot.handle.ThreadCountHandler;
import com.jhobot.handle.commands.Category;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.CommandData;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import com.jhobot.handle.commands.permissions.PermissionLevels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChadVar
{
    // main
    public static final JSONHandler JSON_HANDLER = new JSONHandler().forceCheck();
    public static final DatabaseHandler DATABASE_HANDLER = new DatabaseHandler(JSON_HANDLER.get("uri_link"));
    public static final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(30);
    public static final DebugHandler DEBUG_HANDLER = new DebugHandler();

    // else
    public static boolean ALLOW_UI = true;
    public static HashMap<String, CommandData> COMMANDS = new HashMap<>();
    // presence rotation stuff
    public static int ROTATION_TIME = 60000*5; // 5 minutes
    public static boolean ROTATE_PRESENCE = true;
    public static List<String> PRESENCE_ROTATION = new ArrayList<>();

    // perms
    public static Map<String, PermissionLevels> GLOBAL_PERMISSIONS = new HashMap<>();
    public static PermissionHandler PERMISSION_HANDLER = new PermissionHandler();

    // count handler
    public static ThreadCountHandler THREAD_HANDLER = new ThreadCountHandler();


    // add in rotation
    static {
        PRESENCE_ROTATION.add("hello!");
        PRESENCE_ROTATION.add("gamers");
        PRESENCE_ROTATION.add("epic gamers");
        PRESENCE_ROTATION.add("a bad game");
        PRESENCE_ROTATION.add("j!help");
        PRESENCE_ROTATION.add("j!prefix set *");
        PRESENCE_ROTATION.add("what's going on gamers");
        PRESENCE_ROTATION.add("invite me please");
    }

    static {
        COMMANDS.put("random", new CommandData(Category.FUN, false, new Random()));
        COMMANDS.put("pe", new CommandData(Category.FUN, false, new PhotoEditor()));
        COMMANDS.put("8ball", new CommandData(Category.FUN, false, new EightBall()));
        COMMANDS.put("catgallery", new CommandData(Category.FUN, false, new CatGallery()));
        COMMANDS.put("catfact", new CommandData(Category.FUN, false, new CatFact()));
        COMMANDS.put("rrl", new CommandData(Category.FUN, false, new RussianRoulette()));

        // INFO!
        COMMANDS.put("help", new CommandData(Category.INFO, false, new Help()));
        COMMANDS.put("userinfo", new CommandData(Category.INFO, false, new UserInfo()));
        COMMANDS.put("steam", new CommandData(Category.INFO, false, new Steam()));
        COMMANDS.put("chad", new CommandData(Category.INFO, false, new Chad()));
        COMMANDS.put("guildinfo", new CommandData(Category.INFO, false, new GuildInfo()));
        COMMANDS.put("rtop", new CommandData(Category.INFO, false, new RedditTop()));
        COMMANDS.put("rnew", new CommandData(Category.INFO, false, new RedditNew()));
        COMMANDS.put("contributors", new CommandData(Category.INFO, false, new Contributors()));

        // PUNISHMENTS!
        COMMANDS.put("kick", new CommandData(Category.PUNISHMENTS, false, new Kick()));
        COMMANDS.put("ban", new CommandData(Category.PUNISHMENTS, false, new Ban()));

        // FUNCTION!
        COMMANDS.put("prefix", new CommandData(Category.FUNCTION, false, new Prefix()));
        COMMANDS.put("logging", new CommandData(Category.FUNCTION, false, new Logging()));
        COMMANDS.put("purge", new CommandData(Category.FUNCTION, false, new Purge()));
        COMMANDS.put("im", new CommandData(Category.FUNCTION, false, new Message()));
        COMMANDS.put("autorole", new CommandData(Category.FUNCTION, false, new AutoRole()));
        COMMANDS.put("perms", new CommandData(Category.FUNCTION, false, new Permissions()));

        // NSFW !
        COMMANDS.put("nsfw", new CommandData(Category.NSFW, false, new NSFW()));
        COMMANDS.put("4k", new CommandData(Category.NSFW, false, new NB4K()));
        COMMANDS.put("hentai", new CommandData(Category.NSFW, false, new NBHentai()));
        COMMANDS.put("holo", new CommandData(Category.NSFW, false, new NBHolo()));
        COMMANDS.put("lewdneko", new CommandData(Category.NSFW, false, new NBLewdNeko()));
        COMMANDS.put("lewdkitsune", new CommandData(Category.NSFW, false, new NBLewdKitsune()));
        COMMANDS.put("kemonomimi", new CommandData(Category.NSFW, false, new NBKemonomimi()));
        COMMANDS.put("anal", new CommandData(Category.NSFW, false, new NBAnal()));
        COMMANDS.put("hentaianal", new CommandData(Category.NSFW, false, new NBHentaiAnal()));
        COMMANDS.put("gonewild", new CommandData(Category.NSFW, false, new NBGoneWild()));
        COMMANDS.put("kanna", new CommandData(Category.NSFW, false, new NBKanna()));
        COMMANDS.put("ass", new CommandData(Category.NSFW, false, new NBAss()));
        COMMANDS.put("pussy", new CommandData(Category.NSFW, false, new NBPussy()));
        COMMANDS.put("thigh", new CommandData(Category.NSFW, false, new NBThigh()));
        COMMANDS.put("neko", new CommandData(Category.NSFW, false, new NBNeko()));

        // ADMIN!
        COMMANDS.put("threads", new CommandData(Category.ADMIN, true, new CurrentThreads()));
        COMMANDS.put("modpresence", new CommandData(Category.ADMIN, true, new ModifyPresence()));
        COMMANDS.put("systeminfo", new CommandData(Category.ADMIN, true, new SystemInfo()));
    }
}
