package com.jhobot.handle;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
    public MusicHandler handler;
    public AudioPlayer player;

    public TrackScheduler(MusicHandler handler, AudioPlayer player)
    {
        this.handler = handler;
        this.player = player;
    }

    /*
     * Enqueue a track.
     * If there is already a track playing, add the track to the queue.
     */
    public void queue(AudioTrack track)
    {
        if (!player.startTrack(track, true))
        {
            handler.queue.offer(track);
            return;
        }
    }

    /*
     * Starts the next track, regardless of whether or not there is already a song playing.
     */
    public void nextTrack()
    {
        player.startTrack(handler.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        if (endReason.mayStartNext && !handler.queue.isEmpty())
        {
            nextTrack();
        }
    }
}
