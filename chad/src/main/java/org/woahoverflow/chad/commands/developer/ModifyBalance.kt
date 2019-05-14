package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player
import org.woahoverflow.chad.framework.obj.Player.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap

/**
 * Sets the balance of a user
 *
 * @author sho
 */
class ModifyBalance : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Checks if the arguments is empty
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "setbalance [new balance]")
            return
        }

        // If the arguments size is 0, set the value for the author
        if (args.size == 1) {
            // Makes sure the argument is actually a long
            try {
                java.lang.Long.parseLong(args[0])
            } catch (e1: NumberFormatException) {
                messageHandler.sendError("Invalid Value!")
                return
            }

            // The author's player instance
            val player = PlayerHandler.getPlayer(e.author.longID)

            // Sets the balance
            player.setObject(DataType.BALANCE, java.lang.Long.parseLong(args[0]))

            // Sends the message
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Set your balance to `" + args[0] + "`."))
            return
        }

        // If the arguments size is 2, set the value for another user
        if (args.size == 2) {
            // Checks if anyone is mentioned
            if (e.message.mentions.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.NO_MENTIONS)
                return
            }

            // Makes sure the argument is actually a long
            try {
                java.lang.Long.parseLong(args[0])
            } catch (e1: NumberFormatException) {
                MessageHandler(e.channel, e.author).sendError("Invalid Integer!")
                return
            }

            // The mentioned user's player instance
            val player = PlayerHandler.getPlayer(e.message.mentions[0].longID)

            // Sets the balance of the mentioned user
            player.setObject(DataType.BALANCE, java.lang.Long.parseLong(args[0]))

            // Sends the message
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Set `" + e.message.mentions[0].name + "`'s balance to `" + args[0] + "`."))
            return
        }
        messageHandler.sendError("Invalid Arguments!")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["modbal [amount] {@user}"] = "Sets a user's balance."
        Command.helpCommand(st, "Modify Balance", e)
    }
}
