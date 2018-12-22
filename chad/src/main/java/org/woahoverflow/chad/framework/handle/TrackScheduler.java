package org.woahoverflow.chad.framework.handle;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.ArrayList;
import java.util.List;
import org.woahoverflow.chad.core.ChadBot;
import sx.blah.discord.handle.obj.IGuild;

/**
 * @author sho
 * @since 0.7.0
 */
public class TrackScheduler extends AudioEventAdapter
{

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
    private final long guildId;

    /**
     * Creates a new Track Scheduler
     *
     * @param player The Audio Player
     * @param guildId The Guild's ID
     */
    public TrackScheduler(AudioPlayer player, long guildId)
    {
        this.guildId = guildId;
        this.player = player;
        queue = new ArrayList<>();
    }

    /**
     * Queues a track for the Guild
     *
     * @param track The track to queue
     */
    public void queue(AudioTrack track)
    {
        if (!player.startTrack(track, true))
        {
            queue.add(track);
        }
    }

    /**
     * Skips to the next song
     */
    public boolean nextTrack()
    {
        if (queue.isEmpty())
            return false;

        AudioTrack track = queue.get(0);
        player.startTrack(track, false);
        queue.remove(track);
        return true;
    }

    /**
     * Gets the next track, but doesn't play
     *
     * @return The next track
     */
    public AudioTrack getNextTrack()
    {
        if (queue.isEmpty())
            return null;

        return queue.get(0);
    }

    /**
     * Gets the local audio queue
     *
     * @return The queue
     */
    public List<AudioTrack> getFullQueue()
    {
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
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        IGuild guild = ChadBot.cli.getGuildByID(guildId);

        if (guild.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel() != null && guild.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel().getConnectedUsers().size() == 1 || queue.isEmpty())
        {
            guild.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel().leave();
            return;
        }

        if (endReason.mayStartNext)
        {
            nextTrack();
        }
    }
}
