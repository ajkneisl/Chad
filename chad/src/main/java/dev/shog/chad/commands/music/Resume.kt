package dev.shog.chad.commands.music

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.getMusicManager
import dev.shog.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * To resume music
 *
 * @author sho
 */
class Resume : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["resume"] = "Resumes the music."
        Command.helpCommand(st, "Resume", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val channel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel
        var musicManager = getMusicManager(e.guild)

        if (musicManager == null) {
            messageHandler.sendError("Music isn't paused!")
            return
        }

        if (channel == null && musicManager.player.isPaused) {
            val userChannel = e.author.getVoiceStateForGuild(e.guild).channel

            if (userChannel == null) {
                messageHandler.sendError("You aren't in a channel!")
                return
            }

            musicManager = getMusicManager(e.guild, userChannel)

            RequestBuffer.request {
                userChannel.join()
            }

            musicManager.player.isPaused = false
            messageHandler.sendError("Music has been resumed!")
            return
        }

        messageHandler.sendError("Music isn't paused!")
    }
}