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
public class NBLewdNeko implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Checks if the channel is Nsfw
            if (!e.getChannel().isNSFW())
            {
                messageHandler.sendError(MessageHandler.CHANNEL_NOT_NSFW);
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Lewd Neko [NSFW]");
            embedBuilder.withImage(
                JsonHandler.handle.read("https://nekobot.xyz/api/image?type=lewdneko").getString("message"));
            messageHandler.sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("lewdneko", "Gets Nsfw Nekos");
        return Command.helpCommand(st, "Lewd Neko", e);
    }
}
