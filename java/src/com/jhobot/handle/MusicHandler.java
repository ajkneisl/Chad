package com.jhobot.handle;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import sx.blah.discord.handle.obj.IGuild;

import java.util.LinkedList;
import java.util.Queue;

public class MusicHandler extends AudioEventAdapter {
    public IGuild guild;
    public Queue<AudioTrack> queue = new LinkedList<>();
    public AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public AudioPlayer player = playerManager.createPlayer();
    public boolean currentlyPlaying = false;

    public MusicHandler(IGuild guild)
    {
        try {
            this.guild = guild;
            this.guild.getAudioManager().setAudioProvider(getAudioProvider());

            AudioSourceManagers.registerRemoteSources(playerManager);
            AudioSourceManagers.registerLocalSource(playerManager);
            player.addListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if (endReason.mayStartNext && !queue.isEmpty())
        {
            nextTrack();
            System.out.println("Track ended, starting next one");
        }
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
                    enqueue(track);
                    System.out.println("Enqueued track: " + identifier);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        enqueue(track);
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
     * Enqueue a track.
     * If there is already a track playing, add the track to the queue.
     */
    public void enqueue(AudioTrack track)
    {
        if (!player.startTrack(track, true))
        {
            queue.offer(track);
            return;
        }

        System.out.println("Started a track");
    }

    /*
     * Starts the next track, regardless of whether or not there is already a song playing.
     */
    public void nextTrack()
    {
        player.startTrack(queue.poll(), false);

        System.out.println("Started next track");
    }

    /*
     * Returns an AudioProvider wrapper for the AudioPlayer.
     */
    public AudioProvider getAudioProvider()
    {
        return new AudioProvider(player);
    }
}
