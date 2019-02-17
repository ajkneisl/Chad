package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Gets a random cat fact from an API
 *
 * @author sho
 */
public class CatFact implements Command.Class  {
    @Override
    public final Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            // Gets the fact
            String fact = JsonHandler.handle.read("https://catfact.ninja/fact").getString("fact");

            // Sends the fact
            new MessageHandler(e.getChannel(), e.getAuthor()).credit("catfact.ninja").sendEmbed(new EmbedBuilder().withDesc(fact));

            // i don't even know how i could comprehend something so complicated like this
        };
    }

    @Override
    public final Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catfact", "Gives you a random catfact.");
        return Command.helpCommand(st, "Cat Fact", e);
    }
}
