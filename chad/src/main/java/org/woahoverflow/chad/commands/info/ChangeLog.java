package org.woahoverflow.chad.commands.info;

import java.util.List;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class ChangeLog implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            // Builds it
            embedBuilder.withTitle("**Change Log** : 0.6.3 B2");
            embedBuilder.withDesc(
                "`changelog command` : this command\n"
                        + "`improved internal stuff` : you won't notice this, but I do :)\n"
                        + "`prefix capitalization` : capitalization no longer matters within prefixes"
            );

            // Sends the message
            new MessageHandler(e.getChannel()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }
}
