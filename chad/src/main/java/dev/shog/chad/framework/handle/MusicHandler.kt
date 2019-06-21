@file:JvmName("MusicHandler")

package dev.shog.chad.framework.handle

import dev.shog.chad.core.ChadVar.musicManagers
import dev.shog.chad.core.ChadVar.playerManager
import dev.shog.chad.framework.obj.GuildMusicManager
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IVoiceChannel

/**
 * Gets a guild's audio player
 */
fun getMusicManager(guild: IGuild, voiceChannel: IVoiceChannel): dev.shog.chad.framework.obj.GuildMusicManager {
    val guildId = guild.longID
    val channelId = voiceChannel.longID
    var musicManager: dev.shog.chad.framework.obj.GuildMusicManager? = musicManagers[guildId]

    if (musicManager == null) {
        musicManager = dev.shog.chad.framework.obj.GuildMusicManager(playerManager, guildId, channelId)

        musicManagers[guildId] = musicManager
    }

    musicManager.scheduler.channelId = voiceChannel.longID
    guild.audioManager.audioProvider = musicManager.audioProvider
    return musicManager
}

/**
 * This doesn't reset the music manager for the new channel, and just grabs the guild's instance.
 */
fun getMusicManager(guild: IGuild): dev.shog.chad.framework.obj.GuildMusicManager? {
    val guildId = guild.longID

    val musicManager = musicManagers[guildId] ?: return null
    guild.audioManager.audioProvider = musicManager.audioProvider
    return musicManager
}