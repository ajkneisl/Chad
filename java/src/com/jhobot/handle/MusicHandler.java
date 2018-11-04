package com.jhobot.handle;

import com.jhobot.handle.ui.ChadException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IGuild;

import java.util.LinkedList;
import java.util.Queue;

public class MusicHandler {
    public IGuild guild;
    public Queue<AudioTrack> queue = new LinkedList<>();
    public AudioPlayerManager playerManager;
    public AudioPlayer player;
    public TrackScheduler scheduler;
    public boolean currentlyPlaying = false;

    public MusicHandler(IGuild guild)
    {
        ChadException.error("new MusicHandler");

        this.guild = guild;
        this.guild.getAudioManager().setAudioProvider(getAudioProvider());
        this.playerManager = new DefaultAudioPlayerManager();

        if (this.playerManager == null)
            ChadException.error("playerManager is null");

        this.player = playerManager.createPlayer();
        this.scheduler = new TrackScheduler(this, player);

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        player.addListener(scheduler);

        if (this.player == null)
            ChadException.error("player is null");
    }

    /*
     * Lookup a track, and enqueue it.
     */
    public void play(String identifier)
    {
        try {
            playerManager.loadItemOrdered(this, identifier, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    scheduler.queue(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        scheduler.queue(track);
                    }
                }

                @Override
                public void noMatches() {
                    // Notify the user that we've got nothing
                }

                @Override
                public void loadFailed(FriendlyException throwable) {
                    // Notify the user that everything exploded
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Pause the track.
     */
    public void pause()
    {
        player.setPaused(true);
    }

    /*
     * Resume the track.
     */
    public void resume()
    {
        player.setPaused(false);
    }

    /*
     * Returns an AudioProvider wrapper for the AudioPlayer.
     */
    public AudioProvider getAudioProvider()
    {
        return new AudioProvider(this.player);
    }
}
