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
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

/**
 * @author sho
 * @since 0.7.0
 */
public class Play implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // The channel the author's in
            IVoiceChannel channel = e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel();

            // Makes sure the author's in a channel
            if (channel == null)
            {
                messageHandler.sendError("You aren't in a channel!");
                return;
            }

            // If the bot needs to connect to the author's channel
            boolean connect = false;
            if (e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel() == null)
            {
                connect = true;
            }
            else if (!channel.equals(e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel()))
            {
                messageHandler.sendError("You aren't in the same channel as Chad!");
                return;
            }

            // Finalize the connect value
            final boolean finalConnect = connect;

            // The guild's music manager
            GuildMusicManager manager = Chad.getMusicManager(e.getGuild());

            // If the music is paused, unpause it
            if (args.isEmpty())
            {
                if (manager.player.isPaused())
                {
                    manager.player.setPaused(false);
                    messageHandler.sendMessage("Music is now un-paused!");
                    return;
                }

                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            // Makes sure there's 2+ arguments [.play (yt/sc) (name)]
            if (!(args.size() >= 2))
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            // If the bot doesn't have permission to connect, return
            if (finalConnect && !channel.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.VOICE_CONNECT) || !channel.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.VOICE_SPEAK))
            {
                messageHandler.sendError("I don't have permission to speak/join in channel `"+channel.getName()+"`!");
                return;
            }

            // Gets the option from the arguments (soundcloud/youtube)
            String stringOption;
            if (args.get(0).equalsIgnoreCase("youtube") || args.get(0).equalsIgnoreCase("yt"))
            {
                stringOption = "ytsearch:";
            }
            else if (args.get(0).equalsIgnoreCase("soundcloud") || args.get(0).equalsIgnoreCase("sc"))
            {
                stringOption = "scsearch:";
            }
            else {
                messageHandler.sendError("Please use `YouTube` or `SoundCloud`!");
                return;
            }

            // Removes the soundcloud or youtube option
            args.remove(0);

            // Builds the music name
            String string = args.stream().map(s -> s + ' ').collect(Collectors.joining());

            // Queues the song
            playerManager.loadItemOrdered(manager, stringOption+string,
                new AudioLoadResultHandler() {

                    // When a track is loaded (https, not used)
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        messageHandler.sendMessage("Queued `" + track.getInfo().title + "` by `" + track.getInfo().author + "`\n" + track.getInfo().uri);

                        if (finalConnect)
                            channel.join();

                        manager.scheduler.queue(track);
                    }

                    // When a value is search (thru soundcloud/youtube, used)
                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        AudioTrack track = playlist.getTracks().get(0);

                        if (track == null)
                        {
                            messageHandler.sendError("Invalid Song!");
                            return;
                        }

                        if (track.getInfo().length/1000 > 3600)
                        {
                            messageHandler.sendError("Please don't play songs over 1 hour!");
                            return;
                        }

                        if (finalConnect)
                            channel.join();

                        manager.scheduler.queue(track);
                        messageHandler.sendMessage("Queued `" + track.getInfo().title + "` by `" + track.getInfo().author + "`\n" + track.getInfo().uri);
                    }

                    // If an option wasn't found
                    @Override
                    public void noMatches() {
                        messageHandler.sendError("No matches for **"+string+"**!");
                    }

                    // If there's an exception
                    @Override
                    public void loadFailed(FriendlyException exception) {
                        exception.printStackTrace();
                        messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
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
