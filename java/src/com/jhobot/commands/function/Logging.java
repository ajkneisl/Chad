package com.jhobot.commands.function;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Logging implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Messages m = new Messages(e.getChannel());

            if (e.getAuthor().getPermissionsForGuild(e.getGuild).contains(Permissions.ADMINISTRATOR))
            {
                m.sendError("You don't have permissions for this!");
                return;
            }
            if (args.size() == 0)
            {
                help(e, args);
                return;
            }

            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
            {
                String bool;

                if (args.get(1).equalsIgnoreCase("true"))
                    bool = "True";
                else if (args.get(1).equalsIgnoreCase("false"))
                    bool = "False";
                else
                {
                    m.sendError("You didn't input true or false!");
                    return;
                }

                m.sendConfigLog("Logging", bool, Boolean.toString(JhoBot.db.getBoolean(e.getGuild(), "logging")), e.getAuthor(), e.getGuild(), JhoBot.db);
                m.send("Changed logging to " + bool, "Changed Logging");

                return;
            }

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("setchannel"))
            {
                args.remove(0);

                StringBuilder b = new StringBuilder();
                for (String s : args)
                {
                    b.append(s + " ");
                }

                if (e.getGuild().getChannelsByName(b.toString().trim()).isEmpty())
                {
                    new Messages(e.getChannel()).sendError("Invalid Channel");
                    return;
                }

                IChannel ch = e.getGuild().getChannelsByName(b.toString().trim()).get(0);

                if (ch == null)
                {
                    m.sendError("Invalid Channel");
                    return;
                }

                m.sendConfigLog("Logging Channel", b.toString().trim(), e.getGuild().getChannelByID(Long.parseLong(JhoBot.db.getString(e.getGuild(), "logging_channel"))).getName(), e.getAuthor(), e.getGuild(), JhoBot.db);
                m.send("Changed logging channel to " + b.toString().trim(), "Changed Logging Channel");
                JhoBot.db.set(e.getGuild(), "logging_channel", ch.getStringID());
                return;
            }

            m.sendError("Invalid Arguments");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Logging");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "logging set <true/false>", "Gives information about the mentioned user.", false);
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "logging setchannel <channel name>", "Gives information about the mentioned user.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
