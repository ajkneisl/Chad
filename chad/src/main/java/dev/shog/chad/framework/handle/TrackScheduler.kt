package dev.shog.chad.framework.handle

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.shog.chad.framework.obj.GuildMusicManager
import java.util.*

/**
 * The guild's audio manager
 *
 * @author sho
 */
class TrackScheduler(
        private val player: AudioPlayer,
        val guildId: Long,
        var channelId: Long,
        private val musicManager: GuildMusicManager
) : AudioEventAdapter() {

    /**
     * The Guild's queue
     */
    val queue: MutableList<AudioTrack> = ArrayList()

    /**
     * Gets the next track, but doesn't play
     *
     * @return The next track
     */
    val nextTrack: AudioTrack? = if (queue.isEmpty()) null else queue[0]

    /**
     * Gets the local audio queue
     *
     * @return The queue
     */
    val fullQueue: MutableList<AudioTrack> = queue

    /**
     * Queues a track for the Guild
     *
     * @param track The track to queue
     */
    fun queue(track: AudioTrack) {
        musicManager.setActive(true)

        if (!player.startTrack(track, true)) queue.add(track)
    }

    /**
     * Skips to the next song
     */
    fun nextTrack() {
        if (queue.isEmpty())
            player.stopTrack()
        else {
            val track = queue[0]
            player.startTrack(track, false)
            queue.remove(track)

            musicManager.setActive(true)
        }
    }


    /**
     * The event on track end
     *
     * @param player The audio player
     * @param track The ended audio track
     * @param endReason The reason for the ending
     */
    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (endReason!!.mayStartNext) nextTrack()
    }
}
