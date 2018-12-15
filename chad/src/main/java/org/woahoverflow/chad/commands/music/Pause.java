package org.woahoverflow.chad.commands.music;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

/**
 * @author sho
 * @since 0.7.0
 */
public class Pause implements Command.Class
{

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // The channel the bot is in
            IVoiceChannel channel = e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel();

            // If it's connected
            if (channel == null)
            {
                messageHandler.sendError("I'm not connected!");
                return;
            }

            GuildMusicManager musicManager = Chad.getMusicManager(e.getGuild());

            // Pauses
            musicManager.player.setPaused(true);

            messageHandler.sendMessage("Music is now paused!");

            // If no one's there, leave
            if (channel.getConnectedUsers().size() == 1)
            {
                channel.leave();
                messageHandler.sendMessage("It's quite empty in `"+channel.getName()+"`!");
                musicManager.clear();
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("pause", "Pauses the currently playing music.");
        return Command.helpCommand(st, "Stop", e);
    }
}
