package org.woahoverflow.chad.framework.obj;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.TrackScheduler;
import sx.blah.discord.util.RequestBuffer;

import java.util.Timer;
import java.util.TimerTask;

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
     * Timer :)
     */
    private Timer timer = new Timer();

    /**
     * Creates a Guild Music Manager
     *
     * @param manager The manager
     * @param guildId The guild's ID
     */
    public GuildMusicManager(AudioPlayerManager manager, long guildId, long channelId) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guildId, channelId);

        player.addListener(scheduler);

        amount = 0;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ((player.isPaused() || player.getPlayingTrack() == null) || RequestBuffer.request(() -> cli.getOurUser().getVoiceStateForGuild(cli.getGuildByID(guildId)).getChannel() == null).get()) {
                    amount++;
                } else {
                    amount = 0;
                }

                // Bot doesn't send a message due to it being very iffy, and finding a channel with permissions is difficult
                if (amount >= 60) {
                    RequestBuffer.request(() -> {
                        RequestBuffer.request(() -> cli.getGuildByID(scheduler.guildId).getClient().getOurUser().getVoiceStateForGuild(cli.getGuildByID(scheduler.guildId)).getChannel().leave());
                    });

                    bye();
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
        scheduler.queue.clear();
        player.stopTrack();
        amount = 0;
    }

    /**
     * Updates the voice channel
     * @param channelId The new channel
     */
    public void updateChannel(long channelId) {
        scheduler.channelId = channelId;
    }

    /**
     * This gets rid of the music manager, until the next song starts to play.
     */
    public void bye() {
        ChadVar.musicManagers.remove(scheduler.guildId);

        // Makes sure the concurrent check stops
        timer = null;
    }
}
