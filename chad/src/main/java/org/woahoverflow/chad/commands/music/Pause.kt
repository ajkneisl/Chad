package org.woahoverflow.chad.commands.music

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.getMusicManager
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*

/**
 * Pauses the guild's player
 *
 * @author sho
 */
class Pause : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        // The channel the bot is in
        val channel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel

        // If it's connected
        if (channel == null) {
            messageHandler.sendError("I'm not connected!")
            return
        }

        // Makes sure the user is in the same channel as the bot
        if (e.author.getVoiceStateForGuild(e.guild).channel !== channel) {
            messageHandler.sendError("You aren't in Chads channel!")
            return
        }

        getMusicManager(e.guild, channel).player.isPaused = true
        messageHandler.sendMessage("Music is now paused!")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["pause"] = "Pauses the currently playing music."
        Command.helpCommand(st, "Pause", e)
    }
}
