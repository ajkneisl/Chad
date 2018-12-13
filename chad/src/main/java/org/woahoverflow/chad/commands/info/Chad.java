package org.woahoverflow.chad.commands.info;

import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class Chad implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Creates an embed builder, and adds links etc to it.
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Chad");
            embedBuilder.withDesc("by woahoveflow");
            embedBuilder.appendField("Uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 60000 +" minutes", true);
            embedBuilder.appendField("Ping", e.getClient().getShards().get(0).getResponseTime()+"ms", true);
            embedBuilder.appendField("GitHub", "http://woahoverflow.org/github", false);
            embedBuilder.appendField("Website", "http://woahoverflow.org/chad", false);

            // Sends
            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("chad", "Gives information about the bot.");
        return Command.helpCommand(st, "Chad", e);
    }
}
