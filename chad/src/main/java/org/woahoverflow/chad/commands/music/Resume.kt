package org.woahoverflow.chad.commands.music

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.getMusicManager
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

            if (channel == null) {
                messageHandler.sendError("Music isn't paused!")
                return@Runnable
            }

            if (e.author.getVoiceStateForGuild(e.guild).channel != channel) {
                messageHandler.sendError("You're not in the same channel as Chad!")
                return@Runnable
            }

            val musicManager = getMusicManager(e.guild, channel)

            if (!musicManager.player.isPaused) {
                messageHandler.sendError("Music isn't paused!")
                return@Runnable
            }

            musicManager.player.isPaused = false

            messageHandler.sendMessage("Music is now un-paused!")
        }
    }
}