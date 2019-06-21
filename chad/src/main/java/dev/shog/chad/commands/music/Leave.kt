package dev.shog.chad.commands.music

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.getMusicManager
import dev.shog.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*

/**
 * Leaves and resets queue
 *
 * @author sho
 */
class Leave : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        // The channel that
        val channel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel

        // If Chad's not playing music
        if (channel == null) {
            MessageHandler(e.channel, e.author).sendError("Chad's not playing music")
            return
        }

        // If the author isn't in the same channel as Chad
        if (channel !== e.author.getVoiceStateForGuild(e.guild).channel) {
            MessageHandler(e.channel, e.author).sendError("You aren't in the same channel as Chad!")
            return
        }

        // If Chad's in a channel, leave
        channel.leave()
        MessageHandler(e.channel, e.author).sendMessage("Left the voice channel `" + channel.name + "`!")
        val manager = getMusicManager(e.guild, channel)
        manager.clear()
        manager.setActive(false)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["leave"] = "Leaves the voice channel and clears the queue."
        Command.helpCommand(st, "Leave", e)
    }
}
