package org.woahoverflow.chad.commands.info;

import java.util.HashMap;
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
            embedBuilder.withTitle("**Change Log** : 0.7.0");
            embedBuilder.withDesc(
                "`fixed help commands` : some didn't have help commands, they're good now\n"
                        + "`music commands` : there's now music!\n"
            );

            // Sends the message
            new MessageHandler(e.getChannel()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("changelog", "Gets the current change log");
        return Command.helpCommand(st, "Change Log", e);
    }
}
