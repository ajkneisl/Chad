package dev.shog.chad.core

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import dev.shog.chad.commands.Hentai
import dev.shog.chad.commands.Meme
import dev.shog.chad.commands.Porn
import dev.shog.chad.commands.RedditNew
import dev.shog.chad.commands.RedditTop
import dev.shog.chad.commands.`fun`.*
import dev.shog.chad.commands.admin.*
import dev.shog.chad.commands.community.*
import dev.shog.chad.commands.developer.*
import dev.shog.chad.commands.gambling.Balance
import dev.shog.chad.commands.gambling.CoinFlip
import dev.shog.chad.commands.gambling.DailyReward
import dev.shog.chad.commands.info.*
import dev.shog.chad.commands.music.*
import dev.shog.chad.commands.punishments.Ban
import dev.shog.chad.commands.punishments.Kick
import dev.shog.chad.commands.punishments.Mute
import dev.shog.chad.framework.handle.JsonHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Command.Data
import dev.shog.chad.framework.obj.GuildMusicManager
import sx.blah.discord.handle.obj.ActivityType
import sx.blah.discord.handle.obj.StatusType
import java.util.concurrent.ConcurrentHashMap

/**
 * Storing of Static Variables that don't have a home
 *
 * @author sho
 */
object ChadVar {
    const val VERSION = "v1.0.0-B2"

    /**
     * All the swear words for the swear filter
     */
    @JvmStatic
    val swearWords: ArrayList<String> = arrayListOf()

    /**
     * All results available for the eight ball command
     */
    @JvmStatic
    val eightBallResults: ArrayList<String> = object : ArrayList<String>() {
        init {
            addAll(arrayListOf("It is certain", "As I see it, yes", "Reply hazy, try again", "Don't count on it", "It is decidedly so", "Most likely",
                    "Ask again later", "My reply is no", "Without a doubt", "Outlook good", "Better not tell you now", "My sources say no",
                    "Yes - definitely", "Yes", "Cannot predict now", "Outlook not so good", "You may rely on it", "Signs point to yes", "Concentrate and ask again", "Very doubtful"
            ))
        }
    }

    /**
     * The universal player manager for music playing
     */
    @JvmStatic
    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()

    /**
     * The music manager for guilds
     */
    @JvmStatic
    val musicManagers = ConcurrentHashMap<Long, GuildMusicManager>()

    /**
     * The Youtube API Key in the bot.json file
     */
    @JvmStatic
    var YOUTUBE_API_KEY = JsonHandler["youtube_api_key"]

    /**
     * The Steam API key in the bot.json file
     */
    @JvmStatic
    var STEAM_API_KEY = JsonHandler["steam_api_key"]

    /**
     * The status type (idle, online, offline, dnd)
     */
    @JvmStatic
    var statusType = StatusType.ONLINE

    /**
     * The activity type
     */
    @JvmStatic
    var activityType = ActivityType.PLAYING

    /**
     * The current status string
     */
    @JvmStatic
    var currentStatus = ""

    /**
     * If it should rotate presence at all
     */
    @JvmStatic
    var rotatePresence = true

    /**
     * The full list of Commands
     */
    @JvmStatic
    val COMMANDS = object : ConcurrentHashMap<String, Data>() {
        init {
            // FUN!
            this["random"] = Data(Command.Category.FUN, Random())
            this["photoeditor"] = Data(Command.Category.FUN, PhotoEditor(), "pe")
            this["eightball"] = Data(Command.Category.FUN, EightBall(), "8ball")
            this["catgallery"] = Data(Command.Category.FUN, CatGallery(), "catgal")
            this["catfact"] = Data(Command.Category.FUN, CatFact())
            this["russianroulette"] = Data(Command.Category.FUN, RussianRoulette(), "rrl")
            this["wordreverse"] = Data(Command.Category.FUN, WordReverse(), "wr")
            this["rockpaperscissors"] = Data(Command.Category.FUN, RockPaperScissors(), "rps")
            this["meme"] = Data(Command.Category.FUN, Meme())
            this["doggallery"] = Data(Command.Category.FUN, DogGallery())
            this["dogfact"] = Data(Command.Category.FUN, DogFact())

            // COMMUNITY!
            this["divorce"] = Data(Command.Category.COMMUNITY, DivorcePlayer())
            this["marry"] = Data(Command.Category.COMMUNITY, MarryPlayer())
            this["cuddle"] = Data(Command.Category.COMMUNITY, Cuddle())
            this["profile"] = Data(Command.Category.COMMUNITY, Profile())
            this["upvote"] = Data(Command.Category.COMMUNITY, UpVote())
            this["downvote"] = Data(Command.Category.COMMUNITY, DownVote())

            // INFO!
            this["help"] = Data(Command.Category.INFO, Help())
            this["userinfo"] = Data(Command.Category.INFO, UserInfo(), "uinfo")
            this["steam"] = Data(Command.Category.INFO, Steam(), "st")
            this["chad"] = Data(Command.Category.INFO, Chad())
            this["guildinfo"] = Data(Command.Category.INFO, GuildInfo(), "ginfo")
            this["reddittop"] = Data(Command.Category.INFO, RedditTop(), "rtop")
            this["redditnew"] = Data(Command.Category.INFO, RedditNew(), "rnew")
            this["subscribercount"] = Data(Command.Category.INFO, SubscriberCount(), "subcount", "subc")
            this["steamstatus"] = Data(Command.Category.INFO, SteamStatus(), "steamst")
            this["aliases"] = Data(Command.Category.INFO, Aliases(), "alias")
            this["leaderboard"] = Data(Command.Category.INFO, Leaderboard(), "lb")

            // PUNISHMENTS!
            this["kick"] = Data(Command.Category.PUNISHMENTS, Kick())
            this["ban"] = Data(Command.Category.PUNISHMENTS, Ban())
            this["mute"] = Data(Command.Category.PUNISHMENTS, Mute())

            // ADMINISTRATOR!
            this["prefix"] = Data(Command.Category.ADMINISTRATOR, Prefix())
            this["logging"] = Data(Command.Category.ADMINISTRATOR, Logging())
            this["purge"] = Data(Command.Category.ADMINISTRATOR, Purge())
            this["instantmessage"] = Data(Command.Category.ADMINISTRATOR, Message(), "im")
            this["autorole"] = Data(Command.Category.ADMINISTRATOR, AutoRole(), "ar")
            this["permissions"] = Data(Command.Category.ADMINISTRATOR, Permissions(), "perms")
            this["guildsettings"] = Data(Command.Category.ADMINISTRATOR, GuildSettings(), "gset")
            this["nsfw"] = Data(Command.Category.ADMINISTRATOR, Nsfw())
            this["swearfilter"] = Data(Command.Category.ADMINISTRATOR, Swearing(), "sf")

            // NSFW !
            this["porn"] = Data(Command.Category.NSFW, Porn(), "pn")
            this["hentai"] = Data(Command.Category.NSFW, Hentai(), "hentie")

            // DEVELOPER!
            this["threads"] = Data(Command.Category.DEVELOPER, CurrentThreads(), "cth")
            this["modpresence"] = Data(Command.Category.DEVELOPER, ModifyPresence(), "modp")
            this["systeminfo"] = Data(Command.Category.DEVELOPER, SystemInfo(), "sinf")
            this["shutdown"] = Data(Command.Category.DEVELOPER, Shutdown())
            this["modifybalance"] = Data(Command.Category.DEVELOPER, ModifyBalance(), "modbal")
            this["modifycache"] = Data(Command.Category.DEVELOPER, ModifyCache(), "modcache")
            this["modifydatabase"] = Data(Command.Category.DEVELOPER, ModifyDatabase(), "moddb")
            this["sync"] = Data(Command.Category.DEVELOPER, Sync())
            this["statistics"] = Data(Command.Category.DEVELOPER, Statistics())

            // GAMBLING!
            this["coinflip"] = Data(Command.Category.GAMBLING, CoinFlip(), "cf")
            this["balance"] = Data(Command.Category.GAMBLING, Balance(), "bal")
            this["dailyreward"] = Data(Command.Category.GAMBLING, DailyReward(), "drw")

            // MUSIC
            this["play"] = Data(Command.Category.MUSIC, Play(), "p")
            this["pause"] = Data(Command.Category.MUSIC, Pause(), "pp")
            this["leave"] = Data(Command.Category.MUSIC, Leave(), "ll")
            this["skip"] = Data(Command.Category.MUSIC, Skip(), "ss")
            this["queue"] = Data(Command.Category.MUSIC, Queue(), "qq")
            this["volume"] = Data(Command.Category.MUSIC, Volume(), "vol")
            this["resume"] = Data(Command.Category.MUSIC, Resume(), "res")
        }
    }

    /**
     * List of Verified Developers (just sho LULW)
     */
    @JvmStatic
    val DEVELOPERS: ArrayList<Long> = object : ArrayList<Long>() {
        init { add(274712215024697345) }
    }

    /*
      Registers sources for the player manager
     */
    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
        playerManager.registerSourceManager(YoutubeAudioSourceManager())
        playerManager.registerSourceManager(SoundCloudAudioSourceManager())
    }
}
