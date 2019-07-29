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
    const val VERSION = "v1.0.0-RC1"

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
