package org.woahoverflow.chad.framework.obj;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import sx.blah.discord.handle.audio.AudioEncodingType;
import sx.blah.discord.handle.audio.IAudioProvider;

/**
 * @author sho
 * @since 0.7.0
 */
public class AudioProvider implements IAudioProvider
{

    /**
     * The Audio Player
     */
    private final AudioPlayer audioPlayer;

    /**
     * The last Audio Frame
     */
    private AudioFrame lastFrame;

    /**
     * Creates a new Audio Provider
     *
     * @param audioPlayer The audio player
     */
    AudioProvider(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    /**
     * @return If the player is ready
     */
    @Override
    public boolean isReady() {
        if (lastFrame == null)
        {
            lastFrame = audioPlayer.provide();
        }

        return lastFrame != null;
    }

    /**
     * @return Provides :)
     */
    @Override
    public byte[] provide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.getData() : null;

        lastFrame = null;

        return data;
    }

    /**
     * @return returns 2, I seriously don't know what this does
     */
    @Override
    public int getChannels(){
        return 2;
    }

    /**
     * @return Encoding type Opus
     */
    @Override
    public AudioEncodingType getAudioEncodingType()
    {
        return AudioEncodingType.OPUS;
    }
}
