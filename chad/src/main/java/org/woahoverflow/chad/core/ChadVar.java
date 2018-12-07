package org.woahoverflow.chad.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.woahoverflow.chad.commands.admin.*;
import org.woahoverflow.chad.commands.fight.Attack;
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
import org.woahoverflow.chad.commands.gambling.DailyReward;
import org.woahoverflow.chad.commands.gambling.Register;
import org.woahoverflow.chad.commands.info.Chad;
import org.woahoverflow.chad.commands.info.ChangeLog;
import org.woahoverflow.chad.commands.info.Contributors;
import org.woahoverflow.chad.commands.info.GuildInfo;
import org.woahoverflow.chad.commands.info.Help;
import org.woahoverflow.chad.commands.info.RedditNew;
import org.woahoverflow.chad.commands.info.RedditTop;
import org.woahoverflow.chad.commands.info.Steam;
import org.woahoverflow.chad.commands.info.UserInfo;
import org.woahoverflow.chad.commands.music.Leave;
import org.woahoverflow.chad.commands.music.Pause;
import org.woahoverflow.chad.commands.music.Play;
import org.woahoverflow.chad.commands.music.Skip;
import org.woahoverflow.chad.commands.music.Volume;
import org.woahoverflow.chad.commands.nsfw.NB4K;
import org.woahoverflow.chad.commands.nsfw.NBLewdNeko;
import org.woahoverflow.chad.commands.punishments.Ban;
import org.woahoverflow.chad.commands.punishments.Kick;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Command.Category;
import org.woahoverflow.chad.framework.Command.Data;
import org.woahoverflow.chad.framework.audio.obj.GuildMusicManager;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Storing of Static Variables that don't have a home
 *
 * @author sho
 * @since 0.6.3 B2
 */
public final class ChadVar
{

    // Utilized in MessageHandler (thanks mr zacanager)
    public static final List<String> swearWords = new ArrayList<>();

    // Music stuff
    public static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public static ConcurrentHashMap<Long, GuildMusicManager> musicManagers = new ConcurrentHashMap<>();
    static
    {
        AudioSourceManagers.registerRemoteSources(playerManager);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
    }

    // Used in ModPresence
    public static StatusType statusType = StatusType.ONLINE;
    public static String currentStatus = "";
    public static int rotationInteger = 60; // 5 minutes
    public static boolean rotatePresence = true;
    public static final List<String> presenceRotation = new ArrayList<>();

    // HashMaps
    public static final ConcurrentHashMap<String, Command.Data> COMMANDS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, PermissionHandler.Levels> GLOBAL_PERMISSIONS = new ConcurrentHashMap<>();

    static {
        // FUN!
        COMMANDS.put("random", new Command.Data(Command.Category.FUN, new Random()));
        COMMANDS.put("pe", new Command.Data(Command.Category.FUN, new PhotoEditor(), "photoeditor"));
        COMMANDS.put("8ball", new Command.Data(Command.Category.FUN, new EightBall(), "eightball"));
        COMMANDS.put("catgallery", new Command.Data(Command.Category.FUN, new CatGallery(), "catgal"));
        COMMANDS.put("catfact", new Command.Data(Command.Category.FUN, new CatFact()));
        COMMANDS.put("rrl", new Command.Data(Command.Category.FUN, new RussianRoulette(), "russianroulette"));
        COMMANDS.put("wr", new Command.Data(Command.Category.FUN, new WordReverse(), "wordreverse"));
        COMMANDS.put("rps", new Command.Data(Command.Category.FUN, new RockPaperScissors(), "rockpaperscissors"));

        // INFO!
        COMMANDS.put("help", new Command.Data(Command.Category.INFO, new Help()));
        COMMANDS.put("userinfo", new Command.Data(Command.Category.INFO, new UserInfo(), "uinfo"));
        COMMANDS.put("steam", new Command.Data(Command.Category.INFO, new Steam()));
        COMMANDS.put("chad", new Command.Data(Command.Category.INFO, new Chad()));
        COMMANDS.put("guildinfo", new Command.Data(Command.Category.INFO, new GuildInfo(), "ginfo"));
        COMMANDS.put("rtop", new Command.Data(Command.Category.INFO, new RedditTop(), "reddittop"));
        COMMANDS.put("rnew", new Command.Data(Command.Category.INFO, new RedditNew(), "redditnew"));
        COMMANDS.put("contributors", new Command.Data(Command.Category.INFO, new Contributors()));
        COMMANDS.put("changelog", new Data(Category.INFO, new ChangeLog()));

        // PUNISHMENTS!
        COMMANDS.put("kick", new Command.Data(Command.Category.PUNISHMENTS, new Kick()));
        COMMANDS.put("ban", new Command.Data(Command.Category.PUNISHMENTS, new Ban()));

        // FUNCTION!
        COMMANDS.put("prefix", new Command.Data(Command.Category.FUNCTION, new Prefix()));
        COMMANDS.put("logging", new Command.Data(Command.Category.FUNCTION, new Logging()));
        COMMANDS.put("purge", new Command.Data(Command.Category.FUNCTION, new Purge()));
        COMMANDS.put("im", new Command.Data(Command.Category.FUNCTION, new Message()));
        COMMANDS.put("autorole", new Command.Data(Command.Category.FUNCTION, new AutoRole()));
        COMMANDS.put("perms", new Command.Data(Command.Category.FUNCTION, new Permissions()));
        COMMANDS.put("nsfw", new Command.Data(Command.Category.FUNCTION, new Nsfw()));
        COMMANDS.put("swearfilter", new Command.Data(Category.FUNCTION, new Swearing()));

        // Nsfw !
        COMMANDS.put("4k", new Command.Data(Command.Category.NSFW, new NB4K(), "porn"));
        COMMANDS.put("lewdneko", new Command.Data(Command.Category.NSFW, new NBLewdNeko(), "neko"));

        // ADMIN!
        COMMANDS.put("threads", new Command.Data(Command.Category.ADMIN, new CurrentThreads()));
        COMMANDS.put("modpresence", new Command.Data(Command.Category.ADMIN, new ModifyPresence()));
        COMMANDS.put("systeminfo", new Command.Data(Command.Category.ADMIN, new SystemInfo()));
        COMMANDS.put("cache", new Command.Data(Command.Category.ADMIN, new Cache()));
        COMMANDS.put("shutdown", new Command.Data(Command.Category.ADMIN,new Shutdown()));
        COMMANDS.put("setbal", new Command.Data(Command.Category.ADMIN, new SetBalance()));
        COMMANDS.put("createplayer", new Data(Category.ADMIN, new CreatePlayer()));

        // MONEY!
        COMMANDS.put("register", new Command.Data(Command.Category.MONEY, new Register()));
        COMMANDS.put("coinflip", new Command.Data(Command.Category.MONEY, new CoinFlip()));
        COMMANDS.put("balance", new Command.Data(Command.Category.MONEY, new Balance()));
        COMMANDS.put("dailyreward", new Data(Category.MONEY, new DailyReward(), "drw"));

        // MUSIC
        COMMANDS.put("play", new Data(Category.MUSIC, new Play()));
        COMMANDS.put("pause", new Data(Category.MUSIC, new Pause()));
        COMMANDS.put("leave", new Data(Category.MUSIC, new Leave()));
        COMMANDS.put("skip", new Data(Category.MUSIC, new Skip()));
        COMMANDS.put("volume", new Data(Category.MUSIC, new Volume()));

        // FIGHT
        COMMANDS.put("attack", new Data(Category.FIGHT, new Attack()));
    }
}
