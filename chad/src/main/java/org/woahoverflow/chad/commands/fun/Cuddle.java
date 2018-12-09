package org.woahoverflow.chad.commands.fun;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class Cuddle implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // TODO do this
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("cuddle <@user>", "Cuddle with another user.");
        st.put("cuddle", "Cuddle with yourself :)");
        return Command.helpCommand(st, "Marry", e);
    }
}
