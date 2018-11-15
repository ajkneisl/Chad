package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SystemInfo implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                    java.lang.management.ManagementFactory.getOperatingSystemMXBean();

            String os_name = os.getName();
            String os_version = os.getVersion();
            String os_arch = os.getArch();
            String memory = Util.humanReadableByteCount(os.getTotalPhysicalMemorySize(), true);

            int available_processors = os.getAvailableProcessors();
            double load_average = os.getSystemCpuLoad();
            IShard shard = e.getAuthor().getShard();
            long ping = shard.getResponseTime();

            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("System Information");
            b.withDesc(os_name + "/" + os_version + " - " + os_arch);
            b.appendField("Available cores", Integer.toString(available_processors), true);
            b.appendField("CPU Load", Double.toString(load_average), true);
            b.appendField("Memory", memory, true);
            b.appendField("Shard Response Time", Long.toString(ping), false);
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("systeminfo", "Displays system/connectivity information about the bot server.");
        return HelpHandler.helpCommand(st, "System Information", e);
    }
}
