package org.woahoverflow.chad.commands.music

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.coroutine.request
import org.woahoverflow.chad.framework.handle.getMusicManager
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * Gets the music player's queue
 *
 * @author sho
 */
class Queue : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val chadChannel = request {
            e.client.ourUser.getVoiceStateForGuild(e.guild).channel
        }.also {
            if (it.result !is IVoiceChannel) {
                messageHandler.sendEmbed(EmbedBuilder().withDesc("There's no things currently playing!"))
                return
            }
        }.result as IVoiceChannel

        val manager = getMusicManager(e.guild, chadChannel)
        val queue = manager.scheduler.fullQueue

        // If there's nothing playing
        if (manager.player.playingTrack == null && queue.isEmpty()) {
            messageHandler.sendEmbed(EmbedBuilder().withDesc("There's no things currently playing!"))
            return
        }

        // The currently playing song
        var string = if (manager.player.isPaused)
            "Currently paused. `" + manager.player.playingTrack.info.title + "` by `" + manager.player.playingTrack.info.author + "` was playing.\n\n"
        else
            "Currently playing `" + manager.player.playingTrack.info.title + "` by `" + manager.player.playingTrack.info.author + "`\n\n"

        // Builds the queue into the string
        if (queue.size <= 10) {
            for (i in 0 until queue.size) {
                string += "${i+1}. `${queue[i].info.title}` by `${queue[i].info.author}`\n${queue[i].info.uri}\n\n"
            }
        } else {
            for (i in 0 until 10) {
                string += "${i+1}. `${queue[i].info.title}` by `${queue[i].info.author}`\n${queue[i].info.uri}\n\n"
            }

            string += "... and `${queue.size - 10}` more!\n\n"
        }

        // Builds the queue size into it
        string += "There's currently `" + queue.size + "` songs in the queue."

        messageHandler.sendEmbed(EmbedBuilder().withDesc(string))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["queue"] = "Gets the current song(s) in the queue."
        Command.helpCommand(st, "Queue", e)
    }
}
