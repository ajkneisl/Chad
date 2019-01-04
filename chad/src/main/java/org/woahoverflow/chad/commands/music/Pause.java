package org.woahoverflow.chad.commands.music;

import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;
import java.util.List;

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
                messageHandler.sendError("You aren't in my channel!");
                return;
            }

            GuildMusicManager musicManager = Chad.getMusicManager(e.getGuild());

            // Pauses
            musicManager.player.setPaused(true);

            // Leave the channel (this makes sure the bot isn't just sitting in the channel paused)
            channel.leave();

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
