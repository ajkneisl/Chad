package org.woahoverflow.chad.framework.audio.obj;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.woahoverflow.chad.framework.audio.TrackScheduler;

/**
 * @author sho
 * @since 0.7.0
 */
public class GuildMusicManager
{

    /**
     * The Guild's Audio Player
     */
    public final AudioPlayer player;

    /**
     * The Guild's TrackScheduler
     */
    public final TrackScheduler scheduler;

    /**
     * The Guild's ID
     */
    private final long guildId;

    /**
     * Creates a Guild Music Manager
     *
     * @param manager The manager
     * @param guildId The guild's ID
     */
    public GuildMusicManager(AudioPlayerManager manager, long guildId)
    {
        this.guildId = guildId;

        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guildId);

        player.addListener(scheduler);
    }

    /**
     * @return The guild's audio provider
     */
    public AudioProvider getAudioProvider()
    {
        return new AudioProvider(player);
    }
}
