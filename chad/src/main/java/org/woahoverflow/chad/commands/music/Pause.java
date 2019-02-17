package org.woahoverflow.chad.commands.music;

import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;
import java.util.List;

import static org.woahoverflow.chad.framework.handle.MusicHandlerKt.getMusicManager;

/**
 * Pauses the guild's player
 *
 * @author sho
 */
public class Pause implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // The channel the bot is in
            IVoiceChannel channel = e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel();

            // If it's connected
            if (channel == null) {
                messageHandler.sendError("I'm not connected!");
                return;
            }

            // Makes sure the user is in the same channel as the bot
            if (e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel() != channel) {
                messageHandler.sendError("You aren't in Chads channel!");
                return;
            }

            getMusicManager(e.getGuild(), channel).player.setPaused(true);
            messageHandler.sendMessage("Music is now paused!");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("pause", "Pauses the currently playing music.");
        return Command.helpCommand(st, "Pause", e);
    }
}
