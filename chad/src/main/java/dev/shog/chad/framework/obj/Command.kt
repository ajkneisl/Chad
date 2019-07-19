package dev.shog.chad.framework.obj

import dev.shog.chad.commands.*
import dev.shog.chad.commands.`fun`.*
import dev.shog.chad.commands.`fun`.Random
import dev.shog.chad.commands.admin.*
import dev.shog.chad.commands.community.*
import dev.shog.chad.commands.developer.*
import dev.shog.chad.commands.gambling.Balance
import dev.shog.chad.commands.gambling.CoinFlip
import dev.shog.chad.commands.gambling.DailyReward
import dev.shog.chad.commands.info.*
import dev.shog.chad.commands.music.*
import dev.shog.chad.commands.music.Queue
import dev.shog.chad.commands.punishments.Ban
import dev.shog.chad.commands.punishments.Kick
import dev.shog.chad.commands.punishments.Mute
import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * The command utility for Chad Bot
 *
 * @author sho
 */
object Command {
    /**
     * Categories for Commands
     */
    enum class Category {
        DEVELOPER, GAMBLING, PUNISHMENTS, INFO, ADMINISTRATOR, FUN, NSFW, MUSIC, COMMUNITY
    }

    /**
     * The class the commands implement from.
     */
    interface Class {
        /**
         * When the command is activated. This is through the prefix then the command name.
         *
         * @param e The message event created by Discord4J
         * @param args The arguments in the command string, excluding the command.
         */
        suspend fun run(e: MessageEvent, args: MutableList<String>)

        /**
         * When the command is activated through the prefix and the command, but the only argument is `help`.
         *
         * @param e The message event created by Discord4J
         */
        suspend fun help(e: MessageEvent)
    }

    /**
     * Combines the [Class] and [Data] creating a full command data set.
     */
    data class Command(val commandClass: Class, val data: Data)

    /**
     * The command's data
     */
    class Data {
        /**
         * The command's [Category]
         */
        val commandCategory: Category

        /**
         * The command's aliases
         */
        var cmdAliases: Array<out String>? = null

        /**
         * If the command uses aliases
         */
        private val usesAliases: Boolean

        /**
         * The command's class
         */
        val commandClass: Class

        /**
         * The constructor for data with aliases
         *
         * @param category The command's category
         * @param commandClass The command's class
         * @param commandAliases The command's aliases
         */
        constructor(category: Category, commandClass: Class, vararg commandAliases: String) {
            this.cmdAliases = commandAliases
            commandCategory = category
            this.commandClass = commandClass
            usesAliases = true
        }

        /**
         * The constructor for data without aliases
         *
         * @param category The command's category
         * @param commandClass The command's class
         */
        constructor(category: Category, commandClass: Class) {
            commandCategory = category
            usesAliases = false
            this.commandClass = commandClass
        }

        /**
         * The command's aliases
         */
        fun getCommandAliases(): Array<out String>? = if (!usesAliases()) null else cmdAliases

        /**
         * If it uses aliases through [usesAliases]
         */
        fun usesAliases(): Boolean = usesAliases
    }

    /**
     * Generates a help command
     *
     * @param commands The command's hashmap with their description
     * @param commandName The command's name
     * @param messageReceivedEvent The messagerecievedevent
     * @return The help runnable
     */
    @JvmStatic
    fun helpCommand(commands: HashMap<String, String>, commandName: String, messageReceivedEvent: MessageEvent) {
        if (!helpCommands.containsKey(commandName)) {
            // The guild's prefix
            val prefix = GuildHandler.getGuild(messageReceivedEvent.guild.longID).getObject(
                    DataType.PREFIX) as String

            // The embed builder
            val embedBuilder = EmbedBuilder()
            embedBuilder.withTitle("Help : `$commandName`")
            commands.forEach { (key, `var`) ->
                run {
                    var `val` = `var`
                    when {
                        `val`.contains("!PREFIX!") -> `val` = `val`.replace("!PREFIX!", prefix)
                    }

                    when {
                        key.startsWith("!TEXT!") -> embedBuilder.appendField(String.format("%s", key.removePrefix("!TEXT!")), `val`, false)
                        else -> embedBuilder.appendField(String.format("`%s%s`", prefix, key), `val`, false)
                    }
                }
            }

            helpCommands[commandName] = embedBuilder
        }

        // Sends it
        MessageHandler(messageReceivedEvent.channel, messageReceivedEvent.author).sendEmbed(helpCommands[commandName]!!)
    }

    /**
     * The stored help commands
     */
    @JvmStatic
    private val helpCommands = ConcurrentHashMap<String, EmbedBuilder>()
    
    /**
     * The full list of Commands
     */
    @JvmStatic
    val COMMANDS = object : ConcurrentHashMap<String, Data>() {
        init {
            // FUN!
            this["random"] = Data(Category.FUN, Random())
            this["photoeditor"] = Data(Category.FUN, PhotoEditor(), "pe")
            this["eightball"] = Data(Category.FUN, EightBall(), "8ball")
            this["catgallery"] = Data(Category.FUN, CatGallery(), "catgal")
            this["catfact"] = Data(Category.FUN, CatFact())
            this["russianroulette"] = Data(Category.FUN, RussianRoulette(), "rrl")
            this["wordreverse"] = Data(Category.FUN, WordReverse(), "wr")
            this["rockpaperscissors"] = Data(Category.FUN, RockPaperScissors(), "rps")
            this["meme"] = Data(Category.FUN, Meme())
            this["doggallery"] = Data(Category.FUN, DogGallery())
            this["dogfact"] = Data(Category.FUN, DogFact())
            this["uno"] = Data(Category.FUN, Uno())

            // COMMUNITY!
            this["divorce"] = Data(Category.COMMUNITY, DivorcePlayer())
            this["marry"] = Data(Category.COMMUNITY, MarryPlayer())
            this["cuddle"] = Data(Category.COMMUNITY, Cuddle())
            this["profile"] = Data(Category.COMMUNITY, Profile())
            this["upvote"] = Data(Category.COMMUNITY, UpVote())
            this["downvote"] = Data(Category.COMMUNITY, DownVote())

            // INFO!
            this["help"] = Data(Category.INFO, Help())
            this["userinfo"] = Data(Category.INFO, UserInfo(), "uinfo")
            this["steam"] = Data(Category.INFO, Steam(), "st")
            this["chad"] = Data(Category.INFO, Chad())
            this["guildinfo"] = Data(Category.INFO, GuildInfo(), "ginfo")
            this["reddittop"] = Data(Category.INFO, RedditTop(), "rtop")
            this["redditnew"] = Data(Category.INFO, RedditNew(), "rnew")
            this["subscribercount"] = Data(Category.INFO, SubscriberCount(), "subcount", "subc")
            this["steamstatus"] = Data(Category.INFO, SteamStatus(), "steamst")
            this["aliases"] = Data(Category.INFO, Aliases(), "alias")
            this["leaderboard"] = Data(Category.INFO, Leaderboard(), "lb")

            // PUNISHMENTS!
            this["kick"] = Data(Category.PUNISHMENTS, Kick())
            this["ban"] = Data(Category.PUNISHMENTS, Ban())
            this["mute"] = Data(Category.PUNISHMENTS, Mute())

            // ADMINISTRATOR!
            this["prefix"] = Data(Category.ADMINISTRATOR, Prefix())
            this["logging"] = Data(Category.ADMINISTRATOR, Logging())
            this["purge"] = Data(Category.ADMINISTRATOR, Purge())
            this["instantmessage"] = Data(Category.ADMINISTRATOR, Message(), "im")
            this["autorole"] = Data(Category.ADMINISTRATOR, AutoRole(), "ar")
            this["permissions"] = Data(Category.ADMINISTRATOR, Permissions(), "perms")
            this["guildsettings"] = Data(Category.ADMINISTRATOR, GuildSettings(), "gset")
            this["nsfw"] = Data(Category.ADMINISTRATOR, Nsfw())
            this["swearfilter"] = Data(Category.ADMINISTRATOR, Swearing(), "sf")

            // NSFW !
            this["porn"] = Data(Category.NSFW, Porn(), "pn")
            this["hentai"] = Data(Category.NSFW, Hentai(), "hentie")

            // DEVELOPER!
            this["threads"] = Data(Category.DEVELOPER, CurrentThreads(), "cth")
            this["modpresence"] = Data(Category.DEVELOPER, ModifyPresence(), "modp")
            this["systeminfo"] = Data(Category.DEVELOPER, SystemInfo(), "sinf")
            this["shutdown"] = Data(Category.DEVELOPER, Shutdown())
            this["modifybalance"] = Data(Category.DEVELOPER, ModifyBalance(), "modbal")
            this["modifycache"] = Data(Category.DEVELOPER, ModifyCache(), "modcache")
            this["modifydatabase"] = Data(Category.DEVELOPER, ModifyDatabase(), "moddb")
            this["sync"] = Data(Category.DEVELOPER, Sync())
            this["statistics"] = Data(Category.DEVELOPER, Statistics())

            // GAMBLING!
            this["coinflip"] = Data(Category.GAMBLING, CoinFlip(), "cf")
            this["balance"] = Data(Category.GAMBLING, Balance(), "bal")
            this["dailyreward"] = Data(Category.GAMBLING, DailyReward(), "drw")

            // MUSIC
            this["play"] = Data(Category.MUSIC, Play(), "p")
            this["pause"] = Data(Category.MUSIC, Pause(), "pp")
            this["leave"] = Data(Category.MUSIC, Leave(), "ll")
            this["skip"] = Data(Category.MUSIC, Skip(), "ss")
            this["queue"] = Data(Category.MUSIC, Queue(), "qq")
            this["volume"] = Data(Category.MUSIC, Volume(), "vol")
            this["resume"] = Data(Category.MUSIC, Resume(), "res")
        }
    }
}
