package org.woahoverflow.chad.commands.admin

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap

/**
 * Sets the prefix for the guild
 *
 * @author sho
 */
class Prefix : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        // The guild's database instance
        val guild = GuildHandler.getGuild(e.guild.longID)

        // If there's no arguments, show the prefix
        if (args.isEmpty()) {
            // Sends
            messageHandler.sendEmbed(EmbedBuilder()
                    .withDesc(guild.getObject(DataType.PREFIX) as String)
            )
            return
        }

        // If the arguments are 2, set the prefix
        if (args.size == 2 && args[0].equals("set", ignoreCase = true)) {
            // Gets the current prefix
            val prefix = guild.getObject(Guild.DataType.PREFIX) as String

            // The new prefix
            val newPrefix = args[1]

            // Makes sure the prefix isn't over 6 characters long
            if (newPrefix.length > 6) {
                messageHandler.sendError("Prefix can't be over 6 characters long!")
                return
            }

            // Sends the log
            MessageHandler.sendConfigLog("Prefix", newPrefix, prefix, e.author, e.guild)

            // Sets the prefix in the database
            guild.setObject(DataType.PREFIX, newPrefix)

            // Sends a the message
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Your prefix is now `$newPrefix`."))
            return
        }

        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, guild.getObject(DataType.PREFIX).toString() + "prefix set [new prefix]")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["prefix"] = "Your prefix."
        st["prefix set [string]"] = "Sets the prefix."
        Command.helpCommand(st, "Prefix", e)
    }
}
