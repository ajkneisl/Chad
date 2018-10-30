package com.jhobot.commands.nsfw;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class NSFW implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler h = new MessageHandler(e.getChannel());
            if (e.getChannel().isNSFW())
            {
                h.send("Removed NSFW status from this channel!", "NSFW");
                e.getChannel().changeNSFW(false);
            }
            else {
                h.send("Added NSFW status to this channel!", "NSFW");
                e.getChannel().changeNSFW(true);
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
