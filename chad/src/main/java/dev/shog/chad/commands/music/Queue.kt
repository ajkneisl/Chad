package dev.shog.chad.commands.music

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.coroutine.asIVoiceChannel
import dev.shog.chad.framework.handle.coroutine.request
import dev.shog.chad.framework.handle.getMusicManager
import dev.shog.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
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
        }.asIVoiceChannel()

        val manager = getMusicManager(e.guild, chadChannel)
        val queue = manager.scheduler.fullQueue

        // If there's nothing playing
        if (manager.player.playingTrack == null && queue.isEmpty()) {
            messageHandler.sendEmbed(EmbedBuilder().withDesc("There's no things currently playing!"))
            return
        }

        if (args.size == 2) {
            when (args[0].toLowerCase()) {
                "remove" -> {
                    val int = args[1].toIntOrNull()?.minus(1) ?: run {
                        messageHandler.sendError("Invalid queue index!")
                        return
                    }

                    if (int >= queue.size || -1 >= int) {
                        messageHandler.sendError("Invalid queue index!")
                        return
                    }

                    val obj = queue[int]
                    manager.scheduler.fullQueue.removeAt(int)

                    messageHandler.sendMessage("Removed `${obj.info.title}` from the queue.")
                    return
                }

                "to" -> {
                    val int = args[1].toIntOrNull()?.minus(1) ?: run {
                        messageHandler.sendError("Invalid queue index!")
                        return
                    }

                    if (int >= queue.size || -1 >= int) {
                        messageHandler.sendError("Invalid queue index!")
                        return
                    }

                    val obj = queue[int]

                    for (i in 0 until int + 1)
                        manager.scheduler.fullQueue.removeAt(0)

                    manager.player.playTrack(obj)

                    messageHandler.sendMessage("Skipped to `${obj.info.title}`")
                    return
                }
            }
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
