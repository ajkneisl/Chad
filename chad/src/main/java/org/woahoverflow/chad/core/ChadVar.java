package org.woahoverflow.chad.core;

import org.woahoverflow.chad.commands.admin.Cache;
import org.woahoverflow.chad.commands.admin.CurrentThreads;
import org.woahoverflow.chad.commands.admin.ModifyPresence;
import org.woahoverflow.chad.commands.admin.SetBalance;
import org.woahoverflow.chad.commands.admin.Shutdown;
import org.woahoverflow.chad.commands.admin.SystemInfo;
import org.woahoverflow.chad.commands.fun.CatFact;
import org.woahoverflow.chad.commands.fun.CatGallery;
import org.woahoverflow.chad.commands.fun.EightBall;
import org.woahoverflow.chad.commands.fun.PhotoEditor;
import org.woahoverflow.chad.commands.fun.Random;
import org.woahoverflow.chad.commands.fun.RockPaperScissors;
import org.woahoverflow.chad.commands.fun.RussianRoulette;
import org.woahoverflow.chad.commands.fun.WordReverse;
import org.woahoverflow.chad.commands.function.AutoRole;
import org.woahoverflow.chad.commands.function.Logging;
import org.woahoverflow.chad.commands.function.Message;
import org.woahoverflow.chad.commands.function.Nsfw;
import org.woahoverflow.chad.commands.function.Permissions;
import org.woahoverflow.chad.commands.function.Prefix;
import org.woahoverflow.chad.commands.function.Purge;
import org.woahoverflow.chad.commands.function.Swearing;
import org.woahoverflow.chad.commands.gambling.Balance;
import org.woahoverflow.chad.commands.gambling.CoinFlip;
import org.woahoverflow.chad.commands.gambling.Register;
import org.woahoverflow.chad.commands.info.Chad;
import org.woahoverflow.chad.commands.info.Contributors;
import org.woahoverflow.chad.commands.info.GuildInfo;
import org.woahoverflow.chad.commands.info.Help;
import org.woahoverflow.chad.commands.info.RedditNew;
import org.woahoverflow.chad.commands.info.RedditTop;
import org.woahoverflow.chad.commands.info.Steam;
import org.woahoverflow.chad.commands.info.UserInfo;
import org.woahoverflow.chad.commands.nsfw.NB4K;
import org.woahoverflow.chad.commands.nsfw.NBLewdNeko;
import org.woahoverflow.chad.commands.punishments.Ban;
import org.woahoverflow.chad.commands.punishments.Kick;
import org.woahoverflow.chad.handle.CachingHandler;
import org.woahoverflow.chad.handle.DatabaseHandler;
import org.woahoverflow.chad.handle.JSONHandler;
import org.woahoverflow.chad.handle.ThreadCountHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.Command.Category;
import org.woahoverflow.chad.handle.commands.Command.Data;
import org.woahoverflow.chad.handle.commands.PermissionHandler;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ChadVar
{
    // Used in ModPresence
    public static StatusType statusType = StatusType.ONLINE;
    public static String currentStatus = "";
    public static int rotationInteger = 60000*5; // 5 minutes
    public static boolean rotatePresence = true;
    public static final List<String> presenceRotation = new ArrayList<>();

    // Devices
    static void init() // for devices that need to use the client
    {
        jsonDevice = new JSONHandler().forceCheck();
        databaseDevice = new DatabaseHandler(jsonDevice.get("uri_link"));
        cacheDevice = new CachingHandler(ChadBot.cli);
        permissionDevice = new PermissionHandler();
        threadDevice = new ThreadCountHandler();
        uiDevice = new UIHandler(ChadBot.cli);

        // adds all the words to the array
        jsonDevice.readArray("https://cdn.woahoverflow.org/chad/data/swears.json").forEach((word) -> swearWords.add((String) word));
    }

    public static UIHandler uiDevice;
    public static CachingHandler cacheDevice;
    public static JSONHandler jsonDevice;
    public static DatabaseHandler databaseDevice;
    public static PermissionHandler permissionDevice;
    public static ThreadCountHandler threadDevice;

    // HashMaps
    public static final ConcurrentHashMap<String, Command.Data> COMMANDS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, PermissionHandler.Levels> GLOBAL_PERMISSIONS = new ConcurrentHashMap<>();

    // Important Stuff
    public static final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(30);

    // UI Stuff
    public static String lastCacheAll = "NCA"; // NCA = Not Cached All



    // static blocks
    static {
        presenceRotation.add("hello!");
        presenceRotation.add("gamers");
        presenceRotation.add("epic gamers");
        presenceRotation.add("a bad game");
        presenceRotation.add("j!help");
        presenceRotation.add("j!prefix set *");
        presenceRotation.add("what's going on gamers");
        presenceRotation.add("invite me please");
        presenceRotation.add("yeet");
        presenceRotation.add("yeet yote yeet yote");
        presenceRotation.add("chad is a kike");
        presenceRotation.add("my mom beats me");
        presenceRotation.add("chad till your dead");
        presenceRotation.add("chad for life");
        presenceRotation.add("suck my chad");
        presenceRotation.add("git push you off the swing");
        presenceRotation.add("git pull my dick");
        presenceRotation.add("git reword essay");
        presenceRotation.add("r/rule34");
        presenceRotation.add("sneeki breeki");
        presenceRotation.add("someone stole my sweet role");
        presenceRotation.add("j!lewdneko... FBI open up!");
    }
    static {
        // FUN!
        COMMANDS.put("random", new Command.Data(Command.Category.FUN, false, new Random()));
        COMMANDS.put("pe", new Command.Data(Command.Category.FUN, false, new PhotoEditor()));
        COMMANDS.put("8ball", new Command.Data(Command.Category.FUN, false, new EightBall()));
        COMMANDS.put("catgallery", new Command.Data(Command.Category.FUN, false, new CatGallery()));
        COMMANDS.put("catfact", new Command.Data(Command.Category.FUN, false, new CatFact()));
        COMMANDS.put("rrl", new Command.Data(Command.Category.FUN, false, new RussianRoulette()));
        COMMANDS.put("wr", new Command.Data(Command.Category.FUN, false, new WordReverse()));
        COMMANDS.put("rps", new Command.Data(Command.Category.FUN, false, new RockPaperScissors()));

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
        COMMANDS.put("nsfw", new Command.Data(Command.Category.FUNCTION, false, new Nsfw()));
        COMMANDS.put("swearfilter", new Data(Category.FUNCTION, false, new Swearing()));

        // Nsfw !
        COMMANDS.put("4k", new Command.Data(Command.Category.NSFW, false, new NB4K()));
        COMMANDS.put("lewdneko", new Command.Data(Command.Category.NSFW, false, new NBLewdNeko()));

        // ADMIN!
        COMMANDS.put("threads", new Command.Data(Command.Category.ADMIN, true, new CurrentThreads()));
        COMMANDS.put("modpresence", new Command.Data(Command.Category.ADMIN, true, new ModifyPresence()));
        COMMANDS.put("systeminfo", new Command.Data(Command.Category.ADMIN, true, new SystemInfo()));
        COMMANDS.put("cache", new Command.Data(Command.Category.ADMIN, true, new Cache()));
        COMMANDS.put("shutdown", new Command.Data(Command.Category.ADMIN,true, new Shutdown()));
        COMMANDS.put("setbal", new Command.Data(Command.Category.ADMIN, true, new SetBalance()));

        // MONEY!
        COMMANDS.put("register", new Command.Data(Command.Category.MONEY, false, new Register()));
        COMMANDS.put("coinflip", new Command.Data(Command.Category.MONEY, false, new CoinFlip()));
        COMMANDS.put("balance", new Command.Data(Command.Category.MONEY, false, new Balance()));
    }

    // Utilized in MessageHandler (thanks mr zacanager)
    public static List<String> swearWords = new ArrayList<>();
}
