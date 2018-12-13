package org.woahoverflow.chad.commands.admin;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
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
public class SystemInfo implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            // Gets the OperatingSystemMXBean
            OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            // Creates an EmbedBuilder and applies all the values
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("System Information");
            embedBuilder.withDesc("OS `"+os.getName() + " [" + os.getVersion() + "]`" +
                    "\n Available cores `" + os.getAvailableProcessors() + '`' +
                    "\n CPU Load `" +  os.getSystemCpuLoad() + '`' +
                    "\n Memory `" + os.getTotalPhysicalMemorySize()/1000/1000 + "mb" +
                    "\n Shard Response Time `" + e.getAuthor().getShard().getResponseTime() + '`'
            );

            // Sends the embed builder
            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("systeminfo", "Displays system/connectivity information about the bot.");
        return Command.helpCommand(st, "System Information", e);
    }
}
