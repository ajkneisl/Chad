package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class Nsfw implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // If the channel is NSFW, revoke, if not, add
            if (e.getChannel().isNSFW())
            {
                messageHandler.send("Removed Nsfw status from this channel!", "Nsfw");
                e.getChannel().changeNSFW(false);
            }
            else {
                messageHandler.send("Added Nsfw status to this channel!", "Nsfw");
                e.getChannel().changeNSFW(true);
            }
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("nsfw", "Toggles Nsfw status for the channel");
        return Command.helpCommand(st, "Nsfw", e);
    }
}
