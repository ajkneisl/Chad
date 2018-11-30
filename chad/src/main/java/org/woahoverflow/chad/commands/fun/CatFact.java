package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.JSONHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class CatFact implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Gets the fact
            String fact = JSONHandler.handle.read("https://catfact.ninja/fact").getString("fact");

            // Sends the fact
            new MessageHandler(e.getChannel()).send(fact, "Cat Fact");

            // i don't even know how i could comprehend something so complicated like this
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catfact", "Gives you a random catfact.");
        return Command.helpCommand(st, "Cat Fact", e);
    }
}
