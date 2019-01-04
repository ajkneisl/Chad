package org.woahoverflow.chad.commands.nsfw;

import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Sends a NSFW picture utilizing Neko Bot's API
 *
 * @author sho, codebasepw
 */
public class NBPorn implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Checks if channel is Nsfw
            if (!e.getChannel().isNSFW()) {
                messageHandler.sendPresetError(MessageHandler.Messages.CHANNEL_NOT_NSFW);
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Porn [NSFW]");
            embedBuilder.withImage(JsonHandler.handle.read("https://nekobot.xyz/api/image?type=4k").getString("message"));
            messageHandler.sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("porn", "Gets Porn");
        return Command.helpCommand(st, "Porn", e);
    }
}
