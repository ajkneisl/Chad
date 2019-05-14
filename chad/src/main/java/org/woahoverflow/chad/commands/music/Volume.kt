package org.woahoverflow.chad.commands.music

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PermissionHandler
import org.woahoverflow.chad.framework.handle.getMusicManager
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*

/**
 * Changes the volume of the guild's player
 *
 * @author sho
 */
class Volume : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        // Makes sure the value is a valid integer
        val volume: Int
        try {
            volume = Integer.parseInt(args[0])
        } catch (throwaway: NumberFormatException) {
            MessageHandler(e.channel, e.author).sendError("Invalid Volume!")
            return
        }

        val messageHandler = MessageHandler(e.channel, e.author)

        // If the guild isn't in a channel
        if (e.client.ourUser.getVoiceStateForGuild(e.guild).channel == null) {
            messageHandler.sendError("I'm not connected!")
            return
        }

        // If the author isn't in the same channel as the bot
        if (e.client.ourUser.getVoiceStateForGuild(e.guild).channel !== e.author.getVoiceStateForGuild(e.guild).channel) {
            messageHandler.sendError("You aren't in my channel!")
            return
        }

        // Make sure the value isn't negative
        if (0 > volume) {
            messageHandler.sendError("Please don't use negative numbers!")
            return
        }

        // Makes sure the value isn't over 100, but also allows developers to get whatever they want
        if (100 < volume && !PermissionHandler.isDeveloper(e.author)) {
            messageHandler.sendError("That's too high!")
            return
        }

        // Sets the volume
        getMusicManager(e.guild, e.client.ourUser.getVoiceStateForGuild(e.guild).channel).player.volume = volume

        messageHandler.sendMessage("Set the volume to `$volume`!")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["volume [number]"] = "Sets the volume of the music."
        Command.helpCommand(st, "Volume", e)
    }
}
