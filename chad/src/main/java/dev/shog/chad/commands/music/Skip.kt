package dev.shog.chad.commands.music

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.getMusicManager
import dev.shog.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*

/**
 * Skips songs within the guild's music player
 *
 * @author sho
 */
class Skip : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        // Chad's voice channel
        val channel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel

        // Makes sure that Chad is playing music
        if (channel == null) {
            messageHandler.sendMessage("Chad isn't playing music!")
            return
        }

        // Makes sure the author is in the same channel as the bot
        if (channel !== e.author.getVoiceStateForGuild(e.guild).channel) {
            messageHandler.sendError("You aren't in the same channel as Chad!")
            return
        }

        // The guild's music manager
        val manager = getMusicManager(e.guild, channel)

        // Skips all of the songs in the queue
        if (args.size == 1 && args[0].equals("all", ignoreCase = true)) {
            manager.clear()
            messageHandler.sendMessage("Skipped all songs in queue!")
            return
        }

        manager.scheduler.nextTrack()
        messageHandler.sendMessage("Skipped current song!")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["skip"] = "Skips the current song."
        st["skip all"] = "Skips all the current songs."
        Command.helpCommand(st, "Skip", e)
    }
}
