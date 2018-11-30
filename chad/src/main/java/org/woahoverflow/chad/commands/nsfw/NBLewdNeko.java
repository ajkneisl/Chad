package org.woahoverflow.chad.commands.nsfw;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.JSONHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

public class NBLewdNeko implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Checks if the channel is Nsfw
            if (!e.getChannel().isNSFW())
            {
                messageHandler.sendError(MessageHandler.CHANNEL_NOT_NSFW);
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Lewd Neko [Nsfw]");
            embedBuilder.withImage(
                JSONHandler.handle.read("https://nekobot.xyz/api/image?type=lewdneko").getString("message"));
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
