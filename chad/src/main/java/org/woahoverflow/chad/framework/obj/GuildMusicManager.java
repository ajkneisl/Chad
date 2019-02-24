package org.woahoverflow.chad.framework.obj;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.TrackScheduler;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;
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

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if ((player.isPaused() || player.getPlayingTrack() == null) || RequestBuffer.request(() -> cli.getOurUser().getVoiceStateForGuild(cli.getGuildByID(guildId)).getChannel() == null).get()) {
                    amount++;
                } else {
                    amount = 0;
                }

                if (amount >= 60) {
                    IGuild guild = RequestBuffer.request(() -> cli.getGuildByID(scheduler.guildId)).get();

                    IChannel defaultChannel = RequestBuffer.request(guild::getDefaultChannel).get();
                    IChannel usingChannel = null;

                    // If Chad doesn't have permissions to send messages in the default channel, go until one's found
                    if (RequestBuffer.request(() -> defaultChannel.getModifiedPermissions(cli.getOurUser()).contains(Permissions.SEND_MESSAGES)).get()) {
                        final List<IChannel> channels = RequestBuffer.request(guild::getChannels).get();

                        for (IChannel channel : channels) {
                            boolean hasPermission = channel.getModifiedPermissions(cli.getOurUser()).contains(Permissions.SEND_MESSAGES);

                            if (hasPermission) {
                                usingChannel = channel;
                            }
                        }
                    } else {
                        usingChannel = defaultChannel;
                    }

                    if (usingChannel != null) {
                        final IChannel finalChannel = usingChannel;
                        RequestBuffer.request(() -> finalChannel.sendMessage("No music has been played in the last 5 minutes, leaving!"));
                    }

                    RequestBuffer.request(() -> guild.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel().leave());

                    // Removes this manager, so when requested next it's reset
                    ChadVar.musicManagers.remove(guildId);
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
}
