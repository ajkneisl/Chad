package org.woahoverflow.chad.commands.admin

import kotlinx.coroutines.delay
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * Removes a large amount of messages from a channel
 *
 * @author sho, codebasepw
 */
class Purge : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Makes sure the bot has permission to manage messages
        if (!ChadInstance.cli.ourUser.getPermissionsForGuild(e.guild).contains(Permissions.MANAGE_MESSAGES)) {
            messageHandler.sendPresetError(MessageHandler.Messages.BOT_NO_PERMISSION)
            return
        }

        // Makes sure they've got the amount of messages they want to delete
        if (args.size != 1) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "purge [amount of messages]")
            return
        }

        // Gets the requested amount from the arguments and makes sure it's an actual integer
        val requestedAmount: Int
        try {
            requestedAmount = Integer.parseInt(args[0])
        } catch (throwaway: NumberFormatException) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "purge [amount of messages]")
            return
        }


        // Makes sure the amount isn't over 100
        if (requestedAmount > 100) {
            messageHandler.sendError("You can only delete 100 messages or less.")
            return
        }

        // Deletes the user's message
        RequestBuffer.request { e.message.delete() }

        // Deletes the messages from the channel
        RequestBuffer.request<List<IMessage>> { e.channel.getMessageHistory(Integer.parseInt(args[0])).bulkDelete() }

        // Sends message confirming
        val botConfirm = RequestBuffer.request<IMessage> { e.channel.sendMessage("Cleared `" + args[0] + "` messages from `" + e.channel.name + '`'.toString()) }.get()

        // Waits 2 seconds, then deletes the bot's message
        delay(2000L)

        RequestBuffer.request { botConfirm.delete() }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["purge [amount of messages]"] = "Removes a specific amount of messages from the current channel."
        Command.helpCommand(st, "Purge", e)
    }
}
