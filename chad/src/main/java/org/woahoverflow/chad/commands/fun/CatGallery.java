package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.obj.Command;
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

            messageHandler.sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catgallery", "Gives you a random cat picture.");
        return Command.helpCommand(st, "Cat Gallery", e);
    }
}
