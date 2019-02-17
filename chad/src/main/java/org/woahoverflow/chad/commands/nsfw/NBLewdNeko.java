package org.woahoverflow.chad.commands.nsfw;

import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Sends a NSFW neko picture utilizing Neko Bot's API
 *
 * @author sho, codebasepw
 */
public class NBLewdNeko implements Command.Class  {

    @Override
    public final Runnable run(MessageEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Checks if the channel is Nsfw
            if (!e.getChannel().isNSFW()) {
                messageHandler.sendPresetError(MessageHandler.Messages.CHANNEL_NOT_NSFW);
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
    public final Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("lewdneko", "Gets NSFW Nekos");
        return Command.helpCommand(st, "Lewd Neko", e);
    }
}
