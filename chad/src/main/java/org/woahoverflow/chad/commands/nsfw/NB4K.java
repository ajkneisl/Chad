package org.woahoverflow.chad.commands.nsfw;

import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class NB4K implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Checks if channel is Nsfw
            if (!e.getChannel().isNSFW())
            {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendError(MessageHandler.CHANNEL_NOT_NSFW);
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("4k Pornography [Nsfw]");
            embedBuilder.withImage(JsonHandler.handle.read("https://nekobot.xyz/api/image?type=4k").getString("message"));
            messageHandler.sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("4k", "Gets 4k Pornographic Images");
        return Command.helpCommand(st, "4K Porn", e);
    }
}
