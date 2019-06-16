package dev.shog.chad.framework.obj;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dev.shog.chad.core.ChadInstance;
import dev.shog.chad.framework.handle.TrackScheduler;
import sx.blah.discord.util.RequestBuffer;

import java.util.Timer;
import java.util.TimerTask;

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
    private long amount;

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

        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (active) {
                            if ((player.isPaused() || player.getPlayingTrack() == null) || RequestBuffer.request(() -> ChadInstance.getClient().getOurUser().getVoiceStateForGuild(ChadInstance.getClient().getGuildByID(guildId)).getChannel() == null).get()) {
                                amount++;
                            } else amount = 0;

                            if (amount >= 60) {
                                RequestBuffer.request(() -> {
                                    RequestBuffer.request(() -> ChadInstance.getClient().getGuildByID(scheduler.getGuildId()).getClient().getOurUser().getVoiceStateForGuild(ChadInstance.getClient().getGuildByID(scheduler.getGuildId())).getChannel().leave());
                                });

                                setActive(false);
                            }
                        }
                    }
                }, 0, 1000);
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
        scheduler.getQueue().clear();
        player.stopTrack();
        amount = 0;
    }

    /**
     * If the player is active
     *
     * @param active If the player is active
     */
    public void setActive(boolean active) {
        if (!active) amount = 0;

        this.active = active;
    }
}
