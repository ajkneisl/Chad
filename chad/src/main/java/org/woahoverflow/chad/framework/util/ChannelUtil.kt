package org.woahoverflow.chad.framework.util

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