package org.woahoverflow.chad.commands.music;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

/**
 * @author sho
 * @since 0.7.0
 */
public class Leave implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // The channel that
            IVoiceChannel channel = e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel();

            // If Chad's in a channel, leave
            if (channel != null)
            {
                channel.leave();
                new MessageHandler(e.getChannel(), e.getAuthor()).sendMessage("Left the voice channel `"+channel.getName()+"`!");
                Chad.getMusicManager(e.getGuild()).clear();
                return;
            }

            // If Chad's not playing music
            new MessageHandler(e.getChannel(), e.getAuthor()).sendError("You aren't in a channel!");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("leave", "Leaves the voice channel and clears the queue.");
        return Command.helpCommand(st, "Leave", e);
    }
}
