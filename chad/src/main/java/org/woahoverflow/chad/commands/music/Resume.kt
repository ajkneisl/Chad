package org.woahoverflow.chad.commands.music

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.getMusicManager
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * To resume music
 *
 * @author sho
 */
class Resume : Command.Class {
    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["resume"] = "Resumes the music."
        return Command.helpCommand(st, "Resume", e)
    }

    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)
            val channel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel
            var musicManager = getMusicManager(e.guild)

            if (musicManager == null) {
                messageHandler.sendError("Music isn't paused!")
                return@Runnable
            }

            if (channel == null && musicManager.player.isPaused) {
                val userChannel = e.author.getVoiceStateForGuild(e.guild).channel

                if (userChannel == null) {
                    messageHandler.sendError("You aren't in a channel!")
                    return@Runnable
                }

                musicManager = getMusicManager(e.guild, userChannel)

                RequestBuffer.request {
                    userChannel.join()
                }

                musicManager.player.isPaused = false
                messageHandler.sendError("Music has been resumed!")
                return@Runnable
            }

            messageHandler.sendError("Music isn't paused!")
        }
    }
}