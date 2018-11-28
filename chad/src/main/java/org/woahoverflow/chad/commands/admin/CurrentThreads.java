package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

public class CurrentThreads implements Command.Class{
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Creates an embed builder and applies a title
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Current Threads Running");

            // Adds all threads running threads to the stringbuilder, than to the description.
            StringBuilder stringBuilder = new StringBuilder();
            ChadVar.threadDevice
                .getMap().forEach((key, val) -> stringBuilder.append(key.getName()).append(" [").append(key.getLongID()).append("] ").append(val.size()).append('\n'));
            embedBuilder.appendDesc(stringBuilder.toString());

            // Sends
            new MessageHandler(e.getChannel()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("threads", "Displays all running threads for users.");
        return Command.helpCommand(st, "Current Threads", e);
    }

}
