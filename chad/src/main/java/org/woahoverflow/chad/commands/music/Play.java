package org.woahoverflow.chad.commands.music;

import static org.woahoverflow.chad.core.ChadVar.playerManager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.audio.obj.GuildMusicManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

/**
 * @author sho
 * @since 0.7.0
 */
public class Play implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            IVoiceChannel channel = e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel();

            if (channel == null)
            {
                new MessageHandler(e.getChannel()).sendError("You aren't in a channel!");
                return;
            }

            boolean connect = false;

            if (e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel() == null)
            {
                connect = true;
            }
            else if (!channel.equals(e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel()))
            {
                new MessageHandler(e.getChannel()).sendError("You aren't in the same channel as Chad!");
                return;
            }

            final boolean finalConnect = connect;

            GuildMusicManager manager = Chad.getMusicManager(e.getGuild());

            if (args.isEmpty())
            {
                if (Chad.getMusicManager(e.getGuild()).player.isPaused())
                {
                    Chad.getMusicManager(e.getGuild()).player.setPaused(false);
                    new MessageHandler(e.getChannel()).sendMessage("Music is now un-paused!");
                    return;
                }
                new MessageHandler(e.getChannel()).sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            String string = args.stream().map(s -> s + ' ').collect(Collectors.joining());

            playerManager.loadItemOrdered(manager, "ytsearch:"+string,
                new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        new MessageHandler(e.getChannel()).send("Queued " + track.getInfo().title + " by " + track.getInfo().author, "Chad");

                        if (finalConnect)
                            channel.join();

                        manager.scheduler.queue(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        if (finalConnect)
                            channel.join();

                        AudioTrack track = playlist.getTracks().get(0);

                        if (track == null)
                        {
                            new MessageHandler(e.getChannel()).sendError("Invalid Song!");
                            return;
                        }

                        manager.scheduler.queue(playlist.getTracks().get(0));
                        new MessageHandler(e.getChannel()).sendMessage("Queued `" + track.getInfo().title + "` by `" + track.getInfo().author + "`\n"
                            + track.getInfo().uri);
                    }

                    @Override
                    public void noMatches() {
                        new MessageHandler(e.getChannel()).sendError("No matches for `"+string+"`!");
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        exception.printStackTrace();
                        new MessageHandler(e.getChannel()).sendError(MessageHandler.INTERNAL_EXCEPTION);
                    }
                });
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("play <youtube search>", "Play a song");
        return Command.helpCommand(st, "Play", e);
    }
}
