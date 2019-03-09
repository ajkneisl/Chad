package org.woahoverflow.chad.commands.music;

import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;
import java.util.List;

import static org.woahoverflow.chad.framework.handle.MusicHandlerKt.getMusicManager;

/**
 * Leaves and resets queue
 *
 * @author sho
 */
public class Leave implements Command.Class {

    @Override
    public Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            // The channel that
            IVoiceChannel channel = e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel();

            // If Chad's not playing music
            if (channel == null) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendError("Chad's not playing music");
                return;
            }

            // If the author isn't in the same channel as Chad
            if (channel != e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel()) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendError("You aren't in the same channel as Chad!");
                return;
            }

            // If Chad's in a channel, leave
            channel.leave();
            new MessageHandler(e.getChannel(), e.getAuthor()).sendMessage("Left the voice channel `"+channel.getName()+"`!");
            GuildMusicManager manager = getMusicManager(e.getGuild(), channel);
            manager.clear();
            manager.setActive(false);
        };
    }

    @Override
    public Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("leave", "Leaves the voice channel and clears the queue.");
        return Command.helpCommand(st, "Leave", e);
    }
}
