package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Gets random cat pictures from an API
 *
 * @author sho
 */
public class CatGallery implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // The embed builder
            EmbedBuilder embedBuilder = new EmbedBuilder();

            // The API we use for our cat images :)
            String url = "https://api.thecatapi.com/v1/images/search?size=full";

            embedBuilder.withImage(
                JsonHandler.handle.readArray(url).getJSONObject(0).getString("url")
            );

            messageHandler.credit("thecatapi.com").sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catgallery", "Gives you a random cat picture.");
        return Command.helpCommand(st, "Cat Gallery", e);
    }
}
