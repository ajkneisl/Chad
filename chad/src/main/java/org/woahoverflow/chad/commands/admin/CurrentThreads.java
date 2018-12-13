package org.woahoverflow.chad.commands.admin;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class CurrentThreads implements Command.Class{
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Creates an embed builder and applies a title
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Current Threads Running");

            // Adds all threads running threads to the stringbuilder, than to the description.
            StringBuilder stringBuilder = new StringBuilder();
            Chad.threadHash.forEach((key, val) -> {
                if (key.isDiscordUser())
                {
                    stringBuilder.append('`')
                        .append(key.getUserId())
                        .append("`: ")
                        .append(val.size())
                        .append('\n');
                }
            });

            // Gets the used ram by the JVM, and the available ram and adds it to the stringbuilder
            stringBuilder.append("\nThe JVM is currently using `")
                .append(Runtime.getRuntime().totalMemory()/1000/1000).append("`mb out of `")
                .append(
                    ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize()/1000/1000
                ).append("`mb.");

            // Get the internal and user run threads
            stringBuilder.append("\n\nThere's currently `").append(Chad.internalRunningThreads)
                .append("` internal thread(s) running, there's currently `")
                .append(Chad.runningThreads).append("` user run threads.");
            // Append to builder
            embedBuilder.appendDesc(stringBuilder.toString());

            // Sends
            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("threads", "Displays all running threads for users.");
        return Command.helpCommand(st, "Current Threads", e);
    }

}
