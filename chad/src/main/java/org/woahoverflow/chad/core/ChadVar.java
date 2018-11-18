package org.woahoverflow.chad.core;

import org.woahoverflow.chad.commands.admin.Shutdown;
import org.woahoverflow.chad.commands.admin.*;
import org.woahoverflow.chad.commands.fun.*;
import org.woahoverflow.chad.commands.function.*;
import org.woahoverflow.chad.commands.info.*;
import org.woahoverflow.chad.commands.nsfw.NB4K;
import org.woahoverflow.chad.commands.nsfw.NBLewdNeko;
import org.woahoverflow.chad.commands.punishments.Ban;
import org.woahoverflow.chad.commands.punishments.Kick;
import org.woahoverflow.chad.handle.CachingHandler;
import org.woahoverflow.chad.handle.DatabaseHandler;
import org.woahoverflow.chad.handle.JSONHandler;
import org.woahoverflow.chad.handle.ThreadCountHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.PermissionHandler;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChadVar
{
    public static StatusType STATUS_TYPE = StatusType.ONLINE;
    public static String CURRENT_STATUS = "";
    public static UIHandler UI_HANDLER;
    public static CachingHandler CACHE_DEVICE;
    public static final HashMap<String, Command.Data> COMMANDS = new HashMap<>();
    public static JSONHandler JSON_HANDLER;
    public static DatabaseHandler DATABASE_HANDLER;
    public static final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(30);
    static boolean ALLOW_UI = true;
    public static final ConcurrentHashMap<String, PermissionHandler.Levels> GLOBAL_PERMISSIONS = new ConcurrentHashMap<>();
    public static int ROTATION_TIME = 60000*5; // 5 minutes
    public static boolean ROTATE_PRESENCE = true;
    public static final List<String> PRESENCE_ROTATION = new ArrayList<>();
    public static String LAST_CACHE_ALL = "NCA"; // NCA = Not Cached All
    public static final PermissionHandler PERMISSION_HANDLER = new PermissionHandler();
    public static final ThreadCountHandler THREAD_HANDLER = new ThreadCountHandler();
    public static final ConcurrentHashMap<IGuild, CachingHandler.CachedGuild> GUILD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> STRINGS = new ConcurrentHashMap<>();

    // static blocks
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
        COMMANDS.put("random", new Command.Data(Command.Category.FUN, false, new Random()));
        COMMANDS.put("pe", new Command.Data(Command.Category.FUN, false, new PhotoEditor()));
        COMMANDS.put("8ball", new Command.Data(Command.Category.FUN, false, new EightBall()));
        COMMANDS.put("catgallery", new Command.Data(Command.Category.FUN, false, new CatGallery()));
        COMMANDS.put("catfact", new Command.Data(Command.Category.FUN, false, new CatFact()));
        COMMANDS.put("rrl", new Command.Data(Command.Category.FUN, false, new RussianRoulette()));

        // INFO!
        COMMANDS.put("help", new Command.Data(Command.Category.INFO, false, new Help()));
        COMMANDS.put("userinfo", new Command.Data(Command.Category.INFO, false, new UserInfo()));
        COMMANDS.put("steam", new Command.Data(Command.Category.INFO, false, new Steam()));
        COMMANDS.put("chad", new Command.Data(Command.Category.INFO, false, new Chad()));
        COMMANDS.put("guildinfo", new Command.Data(Command.Category.INFO, false, new GuildInfo()));
        COMMANDS.put("rtop", new Command.Data(Command.Category.INFO, false, new RedditTop()));
        COMMANDS.put("rnew", new Command.Data(Command.Category.INFO, false, new RedditNew()));
        COMMANDS.put("contributors", new Command.Data(Command.Category.INFO, false, new Contributors()));

        // PUNISHMENTS!
        COMMANDS.put("kick", new Command.Data(Command.Category.PUNISHMENTS, false, new Kick()));
        COMMANDS.put("ban", new Command.Data(Command.Category.PUNISHMENTS, false, new Ban()));

        // FUNCTION!
        COMMANDS.put("prefix", new Command.Data(Command.Category.FUNCTION, false, new Prefix()));
        COMMANDS.put("logging", new Command.Data(Command.Category.FUNCTION, false, new Logging()));
        COMMANDS.put("purge", new Command.Data(Command.Category.FUNCTION, false, new Purge()));
        COMMANDS.put("im", new Command.Data(Command.Category.FUNCTION, false, new Message()));
        COMMANDS.put("autorole", new Command.Data(Command.Category.FUNCTION, false, new AutoRole()));
        COMMANDS.put("perms", new Command.Data(Command.Category.FUNCTION, false, new Permissions()));
        COMMANDS.put("nsfw", new Command.Data(Command.Category.FUNCTION, false, new NSFW()));

        // NSFW !
        COMMANDS.put("4k", new Command.Data(Command.Category.NSFW, false, new NB4K()));
        COMMANDS.put("lewdneko", new Command.Data(Command.Category.NSFW, false, new NBLewdNeko()));

        // ADMIN!
        COMMANDS.put("threads", new Command.Data(Command.Category.ADMIN, true, new CurrentThreads()));
        COMMANDS.put("modpresence", new Command.Data(Command.Category.ADMIN, true, new ModifyPresence()));
        COMMANDS.put("systeminfo", new Command.Data(Command.Category.ADMIN, true, new SystemInfo()));
        COMMANDS.put("cache", new Command.Data(Command.Category.ADMIN, true, new Cache()));
        COMMANDS.put("shutdown", new Command.Data(Command.Category.ADMIN,true, new Shutdown()));
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
