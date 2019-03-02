package org.woahoverflow.chad.framework.obj;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.woahoverflow.chad.framework.handle.TrackScheduler;
import sx.blah.discord.util.RequestBuffer;

import java.util.concurrent.TimeUnit;

import static org.woahoverflow.chad.core.ChadInstance.cli;

/**
 * @author sho
 */
public class GuildMusicManager {
    /**
     * The Guild's Audio Player
     */
    public final AudioPlayer player;

    /**
     * The Guild's TrackScheduler
     */
    public final TrackScheduler scheduler;

    /**
     * The amount of seconds that the player's been not playing
     */
    public long amount;

    /**
     * If the manager is active
     */
    private boolean active;

    /**
     * Creates a Guild Music Manager
     *
     * @param manager The manager
     * @param guildId The guild's ID
     */
    public GuildMusicManager(AudioPlayerManager manager, long guildId, long channelId) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guildId, channelId, this);

        active = true;

        player.addListener(scheduler);

        amount = 0;

        new Thread(() -> {
            while (active) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if ((player.isPaused() || player.getPlayingTrack() == null) || RequestBuffer.request(() -> cli.getOurUser().getVoiceStateForGuild(cli.getGuildByID(guildId)).getChannel() == null).get()) {
                    amount++;
                } else amount = 0;

                if (amount >= 60) {
                    RequestBuffer.request(() -> {
                        RequestBuffer.request(() -> cli.getGuildByID(scheduler.guildId).getClient().getOurUser().getVoiceStateForGuild(cli.getGuildByID(scheduler.guildId)).getChannel().leave());
                    });

                    active = false;
                }
            }
        }).start();
    }

    /**
     * @return The guild's audio provider
     */
    public AudioProvider getAudioProvider() {
        return new AudioProvider(player);
    }

    /**
     * Clear the current queue and stop the current track
     */
    public void clear() {
        scheduler.queue.clear();
        player.stopTrack();
        amount = 0;
    }

    /**
     * If the player is active
     *
     * @param active If the player is active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
