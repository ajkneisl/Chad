package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SystemInfo implements Command.Class  {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                    java.lang.management.ManagementFactory.getOperatingSystemMXBean();

            String os_name = os.getName();
            String os_version = os.getVersion();
            String memory = Util.humanReadableByteCount(os.getTotalPhysicalMemorySize(), true);

            int available_processors = os.getAvailableProcessors();
            double load_average = os.getSystemCpuLoad();
            IShard shard = e.getAuthor().getShard();
            long ping = shard.getResponseTime();

            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("System Information");
            b.withDesc("OS `"+os_name + " [" + os_version + "]`" +
                    "\n Available cores `" + available_processors + "`" +
                    "\n CPU Load `" +  load_average + "`" +
                    "\n Memory `" + memory + "`" +
                    "\n Shard Response Time `" + ping + "`"
            );
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("systeminfo", "Displays system/connectivity information about the bot.");
        return Command.helpCommand(st, "System Information", e);
    }
}
