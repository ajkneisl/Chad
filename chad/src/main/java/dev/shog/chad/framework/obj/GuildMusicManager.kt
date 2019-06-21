package dev.shog.chad.framework.obj

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import dev.shog.chad.core.*
import dev.shog.chad.core.ChadVar.musicManagers
import dev.shog.chad.framework.handle.TrackScheduler
import sx.blah.discord.util.RequestBuffer

import java.util.Timer
import java.util.TimerTask

/**
 * @author sho
 */
class GuildMusicManager(manager: AudioPlayerManager, private val guildId: Long, channelId: Long) {
    /**
     * The Guild's Audio Player
     */
    val player: AudioPlayer = manager.createPlayer()

    /**
     * The Guild's TrackScheduler
     */
    val scheduler: TrackScheduler = TrackScheduler(player, guildId, channelId, this)

    /**
     * The amount of seconds that the player's been not playing
     */
    private var amount: Long = 0

    /**
     * If the manager is active
     */
    private var active: Boolean = true

    /**
     * @return The guild's audio provider
     */
    val audioProvider: AudioProvider = AudioProvider(player)

    init {
        player.addListener(scheduler)

        Timer().schedule(
                object : TimerTask() {
                    override fun run() {
                        if (active) {
                            val invalidChannel = RequestBuffer.request<Boolean> {
                                val ch = getClient().ourUser.getVoiceStateForGuild(getClient().getGuildByID(guildId)).channel

                                return@request ch == null || ch.connectedUsers.size <= 1
                            }.get()

                            if (player.isPaused || player.playingTrack == null || invalidChannel) {
                                amount++
                            } else amount = 0

                            if (amount >= 60) {
                                RequestBuffer.request {
                                    getClient().getGuildByID(scheduler.guildId).client.ourUser.getVoiceStateForGuild(getClient().getGuildByID(scheduler.guildId)).channel.leave()
                                }

                                setActive(false)
                            }
                        }
                    }
                }, 0, 1000)
    }

    /**
     * Clear the current queue and stop the current track
     */
    fun clear() {
        scheduler.queue.clear()
        player.stopTrack()
        amount = 0
    }

    /**
     * If the player is active
     *
     * @param active If the player is active
     */
    fun setActive(active: Boolean) {
        if (!active) amount = 0

        this.active = active
    }

    /**
     * Removes the player from existence D:
     *
     * Deletes mostly everything, and when accessed agane a new player will be made.
     */
    fun delete() {
        musicManagers.remove(guildId)
        setActive(false)
        player.destroy()
        scheduler.fullQueue.clear()
    }
}
