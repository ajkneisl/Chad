package dev.shog.chad.commands.admin

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Add logging for specific triggers
 *
 * @author sho
 */
class Logging : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        val guild = GuildHandler.getGuild(e.guild.longID)
        val prefix = guild.getObject(DataType.PREFIX) as String

        // Checks if there are any arguments
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "logging [set/setchannel]")
            return
        }

        // Disables or Enables logging in the guild
        if (args.size == 2 && args[0].equals("set", ignoreCase = true)) {
            if (args[1].equals("off", ignoreCase = true) || args[1].equals("on", ignoreCase = true)) {
                // Sets the on or off
                val bool = if (args[1].equals("on", ignoreCase = true)) "off" else "on"
                val actualBoolean = bool.equals("off", ignoreCase = true)

                // Sets in the database
                GuildHandler.getGuild(e.guild.longID).setObject(DataType.LOGGING, actualBoolean)

                // Sends a log
                MessageHandler.sendConfigLog("Logging", bool, java.lang.Boolean.toString(guild.getObject(DataType.LOGGING) as Boolean), e.author, e.guild)

                // Sends the message
                messageHandler.sendEmbed(EmbedBuilder().withDesc("Logging has been turned `$bool`."))

                // recaches
                GuildHandler.refreshGuild(e.guild.longID)
                return
            }
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "logging set [on/off]")
            return
        }

        if (args.size >= 2 && args[0].equals("setchannel", ignoreCase = true)) {
            // Isolates the channel name
            args.removeAt(0)

            // Builds the channel name
            var formattedString = ""
            for (s in args) {
                formattedString += "$s "
            }

            // Makes sure the channel exists
            if (e.guild.getChannelsByName(formattedString.trim { it <= ' ' }).isEmpty()) {
                messageHandler.sendError("Invalid Channel")
                return
            }

            val channel = e.guild.getChannelsByName(formattedString.trim { it <= ' ' })[0]

            // Makes sure it's not null
            if (channel == null) {
                messageHandler.sendError("Invalid Channel")
                return
            }

            // Gets the current logging channel and makes sure it isn't null
            val loggingChannel = guild.getObject(DataType.LOGGING_CHANNEL) as String

            // Sends the log
            if (loggingChannel.equals("none", ignoreCase = true))
                MessageHandler.sendConfigLog("Logging Channel", formattedString.trim { it <= ' ' }, "none", e.author, e.guild)
            else
                MessageHandler.sendConfigLog("Logging Channel", formattedString.trim { it <= ' ' }, e.guild.getChannelByID(java.lang.Long.parseLong(loggingChannel)).name, e.author, e.guild)

            // Send Message
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Logging channel has been changed to `" + channel.name + "`."))

            // Sets in the database
            GuildHandler.getGuild(e.guild.longID).setObject(DataType.LOGGING, loggingChannel)
            return
        }

        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "logging [set/setchannel]")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["logging set <on/off>"] = "Toggles the logging functionality."
        st["logging setchannel <channel name>"] = "Sets the logging channel."
        Command.helpCommand(st, "Logging", e)
    }
}
