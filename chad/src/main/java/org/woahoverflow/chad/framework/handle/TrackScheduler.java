package org.woahoverflow.chad.framework.handle;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.List;

/**
 * The guild's audio manager
 *
 * @author sho
 */
public class TrackScheduler extends AudioEventAdapter {
    /**
     * The Audio Player for the Guild
     */
    private final AudioPlayer player;

    /**
     * The Guild's queue
     */
    public final List<AudioTrack> queue;

    /**
     * The Guild's ID
     */
    public final long guildId;

    /**
     * The channel's ID
     */
    public long channelId;

    /**
     * Creates a new Track Scheduler
     *
     * @param player The Audio Player
     * @param guildId The Guild's ID
     */
    public TrackScheduler(AudioPlayer player, long guildId, long channelId) {
        this.guildId = guildId;
        this.player = player;
        this.channelId = channelId;
        queue = new ArrayList<>();
    }

    /**
     * Queues a track for the Guild
     *
     * @param track The track to queue
     */
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.add(track);
        }
    }

    /**
     * Skips to the next song
     */
    public void nextTrack() {
        if (queue.isEmpty()) {
            player.stopTrack();
        } else {
            AudioTrack track = queue.get(0);
            player.startTrack(track, false);
            queue.remove(track);
        }
    }

    /**
     * Gets the next track, but doesn't play
     *
     * @return The next track
     */
    public AudioTrack getNextTrack() {
        if (queue.isEmpty())
            return null;

        return queue.get(0);
    }

    /**
     * Gets the local audio queue
     *
     * @return The queue
     */
    public List<AudioTrack> getFullQueue() {
        return queue;
    }


    /**
     * The event on track end
     *
     * @param player The audio player
     * @param track The ended audio track
     * @param endReason The reason for the ending
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
