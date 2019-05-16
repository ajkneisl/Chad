package org.woahoverflow.chad.commands.admin

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.coroutine.request
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer

import java.util.HashMap

/**
 * Toggle NSFW status within a channel
 *
 * @author sho
 */
class Nsfw : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        val hasPermission = request { e.channel.getModifiedPermissions(e.client.ourUser).contains(Permissions.MANAGE_CHANNEL) }.also {
            if (it.result !is Boolean) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
                return
            }
        }.result as Boolean

        // Makes sure they've got permissions
        if (!hasPermission) {
            messageHandler.sendPresetError(MessageHandler.Messages.BOT_NO_PERMISSION)
            return
        }

        val nsfw = request { e.channel.isNSFW }.also {
            if (it.result !is Boolean) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
                return
            }
        }.result as Boolean

        // If the channel is NSFW, revoke, if not, add
        if (nsfw) {
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Removed NSFW status from this channel!"))
            RequestBuffer.request { e.channel.changeNSFW(false) }
        } else {
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Added NSFW status from this channel!"))
            RequestBuffer.request { e.channel.changeNSFW(true) }
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["nsfw"] = "Toggles NSFW status for the channel."
        Command.helpCommand(st, "NSFW", e)
    }
}
