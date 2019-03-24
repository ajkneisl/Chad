package org.woahoverflow.chad.framework.obj

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * The command utility for Chad Bot
 *
 * @author sho, codebasepw
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
        // When the command is run
        fun run(e: MessageEvent, args: MutableList<String>): Runnable

        // When the command is run with help with the only argument
        fun help(e: MessageEvent): Runnable
    }

    /**
     * The command's data
     */
    class Data {
        /**
         * @return The command's category
         */
        val commandCategory: Category

        /**
         * The command's class
         */
        /**
         * @return The command's class
         */
        val commandClass: Command.Class

        /**
         * The command's aliases
         */
        var cmdAliases: Array<out String>? = null

        /**
         * If the command uses aliases
         */
        private val usesAliases: Boolean

        /**
         * The constructor for data with aliases
         *
         * @param category The command's category
         * @param commandClass The command's class
         * @param commandAliases The command's aliases
         */
        constructor(category: Category, commandClass: Command.Class, vararg commandAliases: String) {
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
        constructor(category: Category, commandClass: Command.Class) {
            commandCategory = category
            this.commandClass = commandClass
            usesAliases = false
        }

        /**
         * @return The command's aliases
         */
        fun getCommandAliases(): Array<out String>? = if (!usesAliases()) null else cmdAliases

        /**
         * @return If it uses aliases
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
    fun helpCommand(commands: HashMap<String, String>, commandName: String, messageReceivedEvent: MessageEvent): Runnable {
        return Runnable {
            if (!helpCommands.containsKey(commandName)) {
                // The guild's prefix
                val prefix = GuildHandler.getGuild(messageReceivedEvent.guild.longID).getObject(
                        DataType.PREFIX) as String

                // The embed builder
                val embedBuilder = EmbedBuilder()
                embedBuilder.withTitle("Help : `$commandName`")
                commands.forEach { key, `val` ->
                    run {
                        if (key.startsWith("!TEXT!")) {
                            embedBuilder.appendField(String.format("%s", key.removePrefix("!TEXT!")), `val`, false)
                        } else {
                            embedBuilder.appendField(String.format("`%s%s`", prefix, key), `val`, false)
                        }
                    }
                }

                helpCommands[commandName] = embedBuilder
            }

            // Sends it
            MessageHandler(messageReceivedEvent.channel, messageReceivedEvent.author).sendEmbed(helpCommands[commandName]!!)
        }
    }

    /**
     * The stored help commands
     */
    @JvmStatic
    private val helpCommands = ConcurrentHashMap<String, EmbedBuilder>()
}
