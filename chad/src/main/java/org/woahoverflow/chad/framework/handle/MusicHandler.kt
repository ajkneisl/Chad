package org.woahoverflow.chad.framework.handle

import org.woahoverflow.chad.core.ChadVar.musicManagers
import org.woahoverflow.chad.core.ChadVar.playerManager
import org.woahoverflow.chad.framework.obj.GuildMusicManager
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IVoiceChannel

/**
 * Gets a guild's audio player
 */
fun getMusicManager(guild: IGuild, voiceChannel: IVoiceChannel): GuildMusicManager {
    val guildId = guild.longID
    val channelId = voiceChannel.longID
    var musicManager: GuildMusicManager? = musicManagers[guildId]

    if (musicManager == null) {
        musicManager = GuildMusicManager(playerManager, guildId, channelId)

        musicManagers[guildId] = musicManager
    }

    musicManager.scheduler.channelId = voiceChannel.longID
    guild.audioManager.audioProvider = musicManager.audioProvider
    return musicManager
}