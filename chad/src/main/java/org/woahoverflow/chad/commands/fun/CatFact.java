package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class CatFact implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Gets the fact
            String fact = ChadVar.jsonDevice.read("https://catfact.ninja/fact").getString("fact");

            // Sends the fact
            new MessageHandler(e.getChannel()).send(fact, "Cat Fact");

            // rotationInteger don't even know how rotationInteger could comprehend something so complicated like this
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catfact", "Gives you a random catfact.");
        return Command.helpCommand(st, "Cat Fact", e);
    }
}
