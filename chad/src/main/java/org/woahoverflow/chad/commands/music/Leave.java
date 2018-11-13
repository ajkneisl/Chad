package org.woahoverflow.chad.commands.music;

import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.List;

public class Leave implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            IVoiceChannel vc = e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel();

            if (vc.isConnected())
            {
                vc.leave();
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
