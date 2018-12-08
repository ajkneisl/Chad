package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class CatFact implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Gets the fact
            String fact = JsonHandler.handle.read("https://catfact.ninja/fact").getString("fact");

            // Sends the fact
            new MessageHandler(e.getChannel()).sendEmbed(new EmbedBuilder().withDesc(fact));

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
