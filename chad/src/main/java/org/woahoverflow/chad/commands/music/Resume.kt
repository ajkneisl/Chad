package org.woahoverflow.chad.commands.music

import org.woahoverflow.chad.framework.Chad
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

/**
 * To resume music
 *
 * @author sho
 */
class Resume : Command.Class {
    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["resume"] = "Resumes the music."
        return Command.helpCommand(st, "Resume", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)

            // The channel the bot is in
            val channel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel

            // The channel that the author is in
            val authorChannel = e.author.getVoiceStateForGuild(e.guild).channel

            if (channel != null) {
                messageHandler.sendError("Music isn't paused!")
                return@Runnable
            }

            authorChannel.join()

            val musicManager = Chad.getMusicManager(e.guild)

            // unpauses
            musicManager.player.isPaused = false

            messageHandler.sendMessage("Music is now un-paused!")
        }
    }
}