package dev.shog.chad.framework.util

import dev.shog.chad.framework.handle.MessageHandler
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IGuild

/**
 * Gets a channel's name through what's suppose to be a long, using a guild name and a input.
 */
fun getChannelName(any: Any, guild: IGuild): String {
    if (any is String) return "Deleted Channel"

    if (any is Long) {
        val channel = guild.getChannelByID(any.toLong())
        return if (channel == null || channel.isDeleted) channel.name else "Deleted Channel"
    }

    return "Deleted Channel"
}

fun MessageEvent.createMessageHandler(): MessageHandler = MessageHandler(channel, author)