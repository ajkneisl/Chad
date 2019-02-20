package org.woahoverflow.chad.commands.music;

import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;

import java.util.HashMap;
import java.util.List;

import static org.woahoverflow.chad.framework.handle.MusicHandlerKt.getMusicManager;

/**
 * Changes the volume of the guild's player
 *
 * @author sho
 */
public class Volume implements Command.Class {
    @Override
    public Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            // Makes sure the value is a valid integer
            int volume;
            try {
                volume = Integer.parseInt(args.get(0));
            } catch (NumberFormatException throwaway) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendError("Invalid Volume!");
                return;
            }

            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // If the guild isn't in a channel
            if (e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel() == null) {
                messageHandler.sendError("I'm not connected!");
                return;
            }

            // If the author isn't in the same channel as the bot
            if (e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel() != e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel()) {
                messageHandler.sendError("You aren't in my channel!");
                return;
            }

            // Make sure the value isn't negative
            if (0 > volume) {
                messageHandler.sendError("Please don't use negative numbers!");
                return;
            }

            // Makes sure the value isn't over 100, but also allows developers to get whatever they want
            if (100 < volume) {
                messageHandler.sendError("That's too high!");
                return;
            }

            // Sets the volume
            getMusicManager(e.getGuild(), e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel()).player.setVolume(volume);

            messageHandler.sendMessage("Set the volume to `"+volume+"`!");
        };
    }

    @Override
    public Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("volume <number>", "Sets the volume of the music.");
        return Command.helpCommand(st, "Volume", e);
    }
}
