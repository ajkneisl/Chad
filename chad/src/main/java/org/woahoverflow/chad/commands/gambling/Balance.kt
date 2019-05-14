package org.woahoverflow.chad.commands.gambling

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player
import org.woahoverflow.chad.framework.obj.Player.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap

/**
 * Gets a user's balance
 *
 * @author sho
 */
class Balance : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        if (args.isEmpty()) {
            val player = PlayerHandler.getPlayer(e.author.longID)
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Your balance is `" + player.getObject(DataType.BALANCE) + "`!"))
            return
        }

        if (e.message.mentions.size == 1) {
            val targetIUser = e.message.mentions[0]
            val player = PlayerHandler.getPlayer(targetIUser.longID)
            messageHandler.sendEmbed(EmbedBuilder().withDesc('`'.toString() + targetIUser.name + "`'s balance is `" + player.getObject(DataType.BALANCE) + "`!"))
            return
        }

        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX).toString() + "balance")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["balance"] = "See your balance."
        st["balance [@user]"] = "See another user's balance."
        Command.helpCommand(st, "Balance", e)
    }
}
