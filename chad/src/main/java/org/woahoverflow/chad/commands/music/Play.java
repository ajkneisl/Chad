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

            // The guild's music manager
            GuildMusicManager manager = Chad.getMusicManager(e.getGuild());

            if (!manager.player.isPaused()) {
                messageHandler.sendError("Music isn't paused");
                return;
            }

            // The channel the author's in
            IVoiceChannel channel = e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel();

            if (channel == null) {
                messageHandler.sendError("You aren't in a channel!");
                return;
            }

            channel.join();
            manager.player.setPaused(false);
            messageHandler.sendMessage("Music is now un-paused!");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("play yt <youtube search>", "Play a track from YouTube.");
        st.put("play sc <soundcloud search>", "Play a song from SoundCloud.");
        st.put("play", "If paused, continues playing.");
        return Command.helpCommand(st, "Play", e);
    }
}
