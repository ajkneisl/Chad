package dev.shog.chad.framework.obj

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import sx.blah.discord.handle.audio.AudioEncodingType
import sx.blah.discord.handle.audio.IAudioProvider

/**
 * The audio provider for music playing
 *
 * @author sho
 */
class AudioProvider internal constructor(
        private val audioPlayer: AudioPlayer
) : IAudioProvider {

    /**
     * The last Audio Frame
     */
    private var lastFrame: AudioFrame? = null

    /**
     * @return If the player is ready
     */
    override fun isReady(): Boolean {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide()
        }

        return lastFrame != null
    }

    /**
     * @return Provides :)
     */
    override fun provide(): ByteArray? {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide()
        }

        val data = if (lastFrame != null) lastFrame!!.data else null

        lastFrame = null

        return data
    }

    /**
     * @return returns 2, I seriously don't know what this does
     */
    override fun getChannels(): Int {
        return 2
    }

    /**
     * @return Encoding type Opus
     */
    override fun getAudioEncodingType(): AudioEncodingType {
        return AudioEncodingType.OPUS
    }
}
