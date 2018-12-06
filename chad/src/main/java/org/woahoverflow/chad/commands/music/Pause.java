package org.woahoverflow.chad.commands.music;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
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
            MessageHandler messageHandler = new MessageHandler(e.getChannel());
            if (e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel() == null)
            {
                messageHandler.sendError("I'm not connected!");
                return;
            }

            Chad.getMusicManager(e.getGuild()).player.setPaused(true);
            messageHandler.sendMessage("Music is now paused!");

            IVoiceChannel channel = e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel();

            if (channel.getConnectedUsers().size() == 1)
            {
                channel.leave();
                new MessageHandler(e.getChannel()).sendMessage("It's quite empty in `"+channel.getName()+"`!");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("stop", "Stops playing music");
        return Command.helpCommand(st, "Stop", e);
    }
}
