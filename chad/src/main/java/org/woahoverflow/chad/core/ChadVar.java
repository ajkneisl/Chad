package org.woahoverflow.chad.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.woahoverflow.chad.commands.admin.*;
import org.woahoverflow.chad.commands.community.*;
import org.woahoverflow.chad.commands.developer.Shutdown;
import org.woahoverflow.chad.commands.developer.*;
import org.woahoverflow.chad.commands.fun.*;
import org.woahoverflow.chad.commands.gambling.Balance;
import org.woahoverflow.chad.commands.gambling.CoinFlip;
import org.woahoverflow.chad.commands.gambling.DailyReward;
import org.woahoverflow.chad.commands.info.*;
import org.woahoverflow.chad.commands.music.*;
import org.woahoverflow.chad.commands.nsfw.Hentai;
import org.woahoverflow.chad.commands.nsfw.Porn;
import org.woahoverflow.chad.commands.punishments.Ban;
import org.woahoverflow.chad.commands.punishments.Kick;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Category;
import org.woahoverflow.chad.framework.obj.Command.Data;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Storing of Static Variables that don't have a home
 *
 * @author sho
 */
public final class ChadVar {
    public static final String VERSION = "v0.9.4";

    /**
     * Gigantic Words List
     */
    public static List<String> wordsList = new ArrayList<>();

    /**
     * All the swear words for the swear filter
     */
    public static final List<String> swearWords = new ArrayList<>();

    /**
     * All results available for the eight ball command
     */
    public static final List<String> eightBallResults = new ArrayList<>();

    /**
     * The universal player manager for music playing
     */
    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    /**
     * The music manager for guilds
     */
    public static final ConcurrentHashMap<Long, GuildMusicManager> musicManagers = new ConcurrentHashMap<>();

    /**
     * The Youtube API Key in the bot.json file
     */
    public static String YOUTUBE_API_KEY = JsonHandler.handle.get("youtube_api_key");

    /**
     * The Steam API key in the bot.json file
     */
    public static String STEAM_API_KEY = JsonHandler.handle.get("steam_api_key");

    /*
      Registers sources for the player manager
     */
    static {
        AudioSourceManagers.registerRemoteSources(playerManager);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
    }

    /**
     * The status type (idle, online, offline, dnd)
     */
    public static StatusType statusType = StatusType.ONLINE;

    /**
     * The current status string
     */
    public static String currentStatus = "";

    /**
     * The amount of time to take to rotate between presences
     */
    public static final int rotationInteger = 60*5; // 5 minutes

    /**
     * If it should rotate presence at all
     */
    public static boolean rotatePresence = true;

    /**
     * The presences to rotate between
     */
    public static final List<String> presenceRotation = new ArrayList<>();

    /**
     * The full list of Commands
     */
    public static final ConcurrentHashMap<String, Command.Data> COMMANDS = new ConcurrentHashMap<>();

    /**
     * List of Verified Developers
     */
    public static final List<Long> DEVELOPERS = new ArrayList<>();

    /*
      Define all of the Commands
     */
    static {
        // FUN!
        COMMANDS.put("random", new Command.Data(Command.Category.FUN, new Random()));
        COMMANDS.put("photoeditor", new Command.Data(Command.Category.FUN, new PhotoEditor(), "pe"));
        COMMANDS.put("eightball", new Command.Data(Command.Category.FUN, new EightBall(), "8ball"));
        COMMANDS.put("catgallery", new Command.Data(Command.Category.FUN, new CatGallery(), "catgal"));
        COMMANDS.put("catfact", new Command.Data(Command.Category.FUN, new CatFact()));
        COMMANDS.put("russianroulette", new Command.Data(Command.Category.FUN, new RussianRoulette(), "rrl"));
        COMMANDS.put("wordreverse", new Command.Data(Command.Category.FUN, new WordReverse(), "wr"));
        COMMANDS.put("rockpaperscissors", new Command.Data(Command.Category.FUN, new RockPaperScissors(), "rps"));
        COMMANDS.put("meme", new Data(Category.FUN, new Meme()));
        COMMANDS.put("doggallery", new Data(Category.FUN, new DogGallery()));
        COMMANDS.put("dogfact", new Data(Category.FUN, new DogFact()));

        // COMMUNITY!
        COMMANDS.put("divorce", new Data(Category.COMMUNITY, new DivorcePlayer()));
        COMMANDS.put("marry", new Data(Category.COMMUNITY, new MarryPlayer()));
        COMMANDS.put("cuddle", new Data(Category.COMMUNITY, new Cuddle()));
        COMMANDS.put("profile", new Data(Category.COMMUNITY, new Profile()));
        COMMANDS.put("upvote", new Data(Category.COMMUNITY, new UpVote()));
        COMMANDS.put("downvote", new Data(Category.COMMUNITY, new DownVote()));

        // INFO!
        COMMANDS.put("help", new Command.Data(Command.Category.INFO, new Help()));
        COMMANDS.put("userinfo", new Command.Data(Command.Category.INFO, new UserInfo(), "uinfo"));
        COMMANDS.put("steam", new Command.Data(Command.Category.INFO, new Steam(), "st"));
        COMMANDS.put("chad", new Command.Data(Command.Category.INFO, new Chad()));
        COMMANDS.put("guildinfo", new Command.Data(Command.Category.INFO, new GuildInfo(), "ginfo"));
        COMMANDS.put("reddittop", new Command.Data(Command.Category.INFO, new RedditTop(), "rtop"));
        COMMANDS.put("redditnew", new Command.Data(Command.Category.INFO, new RedditNew(), "rnew"));
        COMMANDS.put("subscribercount", new Data(Category.INFO, new SubscriberCount(), "subcount", "subc"));
        COMMANDS.put("steamstatus", new Data(Category.INFO, new SteamStatus(), "steamst"));
        COMMANDS.put("aliases", new Data(Category.INFO, new Aliases(), "alias"));

        // PUNISHMENTS!
        COMMANDS.put("kick", new Command.Data(Command.Category.PUNISHMENTS, new Kick()));
        COMMANDS.put("ban", new Command.Data(Command.Category.PUNISHMENTS, new Ban()));

        // ADMINISTRATOR!
        COMMANDS.put("prefix", new Command.Data(Command.Category.ADMINISTRATOR, new Prefix()));
        COMMANDS.put("logging", new Command.Data(Command.Category.ADMINISTRATOR, new Logging()));
        COMMANDS.put("purge", new Command.Data(Command.Category.ADMINISTRATOR, new Purge()));
        COMMANDS.put("instantmessage", new Command.Data(Command.Category.ADMINISTRATOR, new Message(), "im"));
        COMMANDS.put("autorole", new Command.Data(Command.Category.ADMINISTRATOR, new AutoRole(), "ar"));
        COMMANDS.put("permissions", new Command.Data(Command.Category.ADMINISTRATOR, new Permissions(), "perms"));
        COMMANDS.put("guildsettings", new Data(Category.ADMINISTRATOR, new GuildSettings(), "gset"));
        COMMANDS.put("nsfw", new Command.Data(Command.Category.ADMINISTRATOR, new Nsfw()));
        COMMANDS.put("swearfilter", new Command.Data(Category.ADMINISTRATOR, new Swearing(), "sf"));

        // NSFW !
        COMMANDS.put("porn", new Command.Data(Command.Category.NSFW, new Porn(), "pn"));
        COMMANDS.put("hentai", new Command.Data(Command.Category.NSFW, new Hentai(), "hentie"));

        // DEVELOPER!
        COMMANDS.put("threads", new Command.Data(Command.Category.DEVELOPER, new CurrentThreads(), "cth"));
        COMMANDS.put("modpresence", new Command.Data(Command.Category.DEVELOPER, new ModifyPresence(), "modp"));
        COMMANDS.put("systeminfo", new Command.Data(Command.Category.DEVELOPER, new SystemInfo(), "sinf"));
        COMMANDS.put("shutdown", new Command.Data(Command.Category.DEVELOPER,new Shutdown()));
        COMMANDS.put("modifybalance", new Command.Data(Command.Category.DEVELOPER, new ModifyBalance(), "modbal"));
        COMMANDS.put("modifycache", new Data(Category.DEVELOPER, new ModifyCache(), "modcache"));
        COMMANDS.put("modifydatabase", new Data(Category.DEVELOPER, new ModifyDatabase(), "moddb"));
        COMMANDS.put("modifydevelopers", new Data(Category.DEVELOPER, new ModifyDevelopers(), "moddev"));
        COMMANDS.put("sync", new Data(Category.DEVELOPER, new Sync()));

        // GAMBLING!
        COMMANDS.put("coinflip", new Command.Data(Command.Category.GAMBLING, new CoinFlip(), "cf"));
        COMMANDS.put("balance", new Command.Data(Command.Category.GAMBLING, new Balance(), "bal"));
        COMMANDS.put("dailyreward", new Data(Category.GAMBLING, new DailyReward(), "drw"));

        // MUSIC
        COMMANDS.put("play", new Data(Category.MUSIC, new Play(), "p"));
        COMMANDS.put("pause", new Data(Category.MUSIC, new Pause(), "pp"));
        COMMANDS.put("leave", new Data(Category.MUSIC, new Leave(), "ll"));
        COMMANDS.put("skip", new Data(Category.MUSIC, new Skip(), "ss"));
        COMMANDS.put("queue", new Data(Category.MUSIC, new Queue(), "qq"));
        COMMANDS.put("volume", new Data(Category.MUSIC, new Volume(), "vol"));
        COMMANDS.put("resume", new Data(Category.MUSIC, new Resume(), "res", "rr"));
    }
}
