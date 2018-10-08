package com.jhobot.commands.info;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class UpdateLog implements Command
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Messages m = new Messages(e.getChannel());
            if (args.size() == 0)
            {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Current Version : unstable-0.2");
                b.appendField("Added", "Bug reports!", true);
                b.appendField("Added", "Multithreading!", true);
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                m.sendEmbed(b.build());
                return;
            }

            if (args.size() == 1 && args.get(0).equalsIgnoreCase("unstable-0.01.5"))
            {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Current Version : unstable-0.1.05");
                b.appendField("Removed", "Russian Roulette command. It stopped other servers from using commands completely.", true);
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                m.sendEmbed(b.build());
                return;
            }

            if (args.size() == 1 && args.get(0).equalsIgnoreCase("unstable-0.01.03")) // unstable-0.01.03
            {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Current Version : unstable-0.1.03");
                b.appendField("Fixed", "An error where if you put 0 as a random number entry it did nothing.", true);
                b.appendField("Added", "This command.", true);
                b.appendField("Added", "Cat Gallery (" + JhoBot.db.getString(e.getGuild(), "prefix") + "catgallery)", true);
                b.appendField("Added", "Russian Roulette (" + JhoBot.db.getString(e.getGuild(), "prefix") + "rrl)", true);
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                m.sendEmbed(b.build());
                return;
            }

            if (args.size() == 1 && args.get(0).equalsIgnoreCase("unstable-0.01.03")) // unstable-0.01.04
            {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Current Version : unstable-0.1.04");
                b.appendField("Changed", "The way the catgallery gets images. The change should make it faster.", true);
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                m.sendEmbed(b.build());
                return;
            }
            m.sendError("No other versions are available for review.");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Update Log");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "updatelog", "Gives you info about recent updates.", false);
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "updatelog [update id]", "Gives you info about a specific update.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
