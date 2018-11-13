package org.woahoverflow.chad.core;

import org.woahoverflow.chad.handle.ui.UIHandler;
import org.woahoverflow.chad.commands.admin.Cache;
import org.woahoverflow.chad.commands.admin.CurrentThreads;
import org.woahoverflow.chad.commands.admin.ModifyPresence;
import org.woahoverflow.chad.commands.admin.Shutdown;
import org.woahoverflow.chad.commands.admin.SystemInfo;
import org.woahoverflow.chad.commands.fun.*;
import org.woahoverflow.chad.commands.function.*;
import org.woahoverflow.chad.commands.info.*;
import org.woahoverflow.chad.commands.nsfw.*;
import org.woahoverflow.chad.commands.punishments.Ban;
import org.woahoverflow.chad.commands.punishments.Kick;
import org.woahoverflow.chad.handle.*;
import org.woahoverflow.chad.handle.commands.Category;
import org.woahoverflow.chad.handle.commands.CommandData;
import org.woahoverflow.chad.handle.commands.permissions.PermissionHandler;
import org.woahoverflow.chad.handle.commands.permissions.PermissionLevels;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChadVar
{
    // used in with the auto updater
    public static final String VERSION = "v0.5.3-SNAPCHAT";
    public static boolean ALLOW_UNSTABLE = false;

    // ui stuff
    public static UIHandler UI_HANDLER;

    // caching
    public static CachingHandler CACHE_DEVICE;
    public static String LAST_CACHE_ALL = "Loading...";

    // main
    public static JSONHandler JSON_HANDLER;
    public static DatabaseHandler DATABASE_HANDLER;
    public static final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(30);

    // else
    static boolean ALLOW_UI = true;
    public static final HashMap<String, CommandData> COMMANDS = new HashMap<>();
    // presence rotation stuff
    public static int ROTATION_TIME = 60000*5; // 5 minutes
    public static boolean ROTATE_PRESENCE = true;
    public static final List<String> PRESENCE_ROTATION = new ArrayList<>();

    // perms
    public static final ConcurrentHashMap<String, PermissionLevels> GLOBAL_PERMISSIONS = new ConcurrentHashMap<>();
    public static final PermissionHandler PERMISSION_HANDLER = new PermissionHandler();

    // count handler
    public static final ThreadCountHandler THREAD_HANDLER = new ThreadCountHandler();

    // caching
    public static final ConcurrentHashMap<IGuild, CachingHandler.CachedGuild> GUILD_CACHE = new ConcurrentHashMap<>();

    // strings
    private static final ConcurrentHashMap<String, String> STRINGS = new ConcurrentHashMap<>();

    // music handlers
    //public static Map<IGuild, MusicHandler> musicHandlers = new HashMap<>();

    // add strings
    static {
        STRINGS.put("error.generic", "An unknown error has occurred.");
        STRINGS.put("error.internal", "An internal error has occurred.");
        STRINGS.put("arguments.invalid", "Invalid arguments.");
        STRINGS.put("arguments.more", "Not enough arguments.");
        STRINGS.put("arguments.less", "Too many arguments.");
        STRINGS.put("denied.generic", "No access. Contact a system administrator for assistance.");
        STRINGS.put("denied.permission.generic", "You don't have permission for this!");
        STRINGS.put("denied.permission.command", "You don't have permission to access this command!");
        STRINGS.put("denied.permission.developer", "Oh noes! Looks like you're not a developer, too bad.");
        STRINGS.put("chad.function.permissions.none", "There's no permissions there!");
        STRINGS.put("chad.function.permissions.role.invalid", "Invalid role!");
    }

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
        PRESENCE_ROTATION.add("yeet");
        PRESENCE_ROTATION.add("yeet yote yeet yote");
        PRESENCE_ROTATION.add("chad is a kike");
        PRESENCE_ROTATION.add("my mom beats me");
        PRESENCE_ROTATION.add("chad till your dead");
        PRESENCE_ROTATION.add("chad for life");
        PRESENCE_ROTATION.add("suck my chad");
        PRESENCE_ROTATION.add("git push you off the swing");
        PRESENCE_ROTATION.add("git pull my dick");
        PRESENCE_ROTATION.add("git reword essay");
        PRESENCE_ROTATION.add("r/rule34");
        PRESENCE_ROTATION.add("sneeki breeki");
        PRESENCE_ROTATION.add("someone stole my sweet role");
        PRESENCE_ROTATION.add("j!lewdneko... FBI open up!");
    }

    static {
        // FUN!
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
        COMMANDS.put("org/woahoverflow/chad", new CommandData(Category.INFO, false, new Chad()));
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
        COMMANDS.put("cache", new CommandData(Category.ADMIN, true, new Cache()));
        COMMANDS.put("shutdown", new CommandData(Category.ADMIN,true, new Shutdown()));

        // MUSIC!
        // disabled :/
    }

    // just because they use the client
    static void setCacheDevice()
    {
        CACHE_DEVICE = new CachingHandler(ChadBot.cli);
    }

    static void setUiHandler()
    {
        UI_HANDLER = new UIHandler(ChadBot.cli);
    }

    static void setDatabaseHandler()
    {
        DATABASE_HANDLER = new DatabaseHandler(JSON_HANDLER.get("uri_link"));
    }

    static void setJsonHandler()
    {
        JSON_HANDLER = new JSONHandler().forceCheck();
    }

    // get a string from STRINGS
    public static String getString(String key) {
        return ChadVar.STRINGS.get(key);
    }
}
