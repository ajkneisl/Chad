package com.jhobot.handle;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.ui.ChadException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import sx.blah.discord.handle.audio.AudioEncodingType;
import sx.blah.discord.handle.audio.IAudioProvider;

public class AudioProvider implements IAudioProvider {
    private AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    /**
     * @param audioPlayer Audio player to wrap.
     */
    public AudioProvider(AudioPlayer audioPlayer)
    {
        if (audioPlayer == null)
            ChadVar.UI_HANDLER.addLog("constructor audioPlayer is null");
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean isReady() {
        if (audioPlayer == null)
            System.out.println("audioPlayer is null");

        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        return lastFrame != null;
    }

    @Override
    public byte[] provide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;

        return data;
    }

    @Override
    public int getChannels() {
        return 2;
    }

    @Override
    public AudioEncodingType getAudioEncodingType() {
        return AudioEncodingType.OPUS;
    }
}
