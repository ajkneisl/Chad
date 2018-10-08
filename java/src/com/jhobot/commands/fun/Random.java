package com.jhobot.commands.fun;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import org.json.JSONObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Random implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Messages m = new Messages(e.getChannel());
            if (args.size() == 0)
            {
                help(e, args);
                return;
            }

            if (args.get(0).equalsIgnoreCase("number"))
            {
                java.util.Random rand = new java.util.Random();
                if (args.size() == 2)
                {
                    try {
                        Integer i2 = Integer.parseInt(args.get(1));

                        if (i2 == 0)
                        {
                            m.sendError("Cannot use 0!");
                            return;
                        }

                        m.send("Number is : " + rand.nextInt(i2), "Random Number");
                    } catch (NumberFormatException ee)
                    {
                        new Messages(e.getChannel()).sendError("Invalid Number");
                    }
                    return;
                }

                m.send("Number is : " + rand.nextInt(100), "Random Number");
                return;
            }

            if (args.get(0).equalsIgnoreCase("quote"))
            {
                try {
                    JSONObject obj = JSON.read("https://talaikis.com/api/quotes/random/");
                    EmbedBuilder b = new EmbedBuilder();
                    b.withTitle("Random Quote");
                    b.appendField("Author", obj.getString("author"), true);
                    // Switches category's first letter to be uppercase
                    String s1 = obj.getString("cat").substring(0, 1).toUpperCase();
                    String cap = s1 + obj.getString("cat").substring(1);
                    b.appendField("Category", cap, true);
                    b.appendField("Quote", obj.getString("quote"), false);
                    b.withFooterText(Util.getTimeStamp());
                    b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
                    m.sendEmbed(b.build());
                } catch (IOException ee)
                {
                    ee.printStackTrace();
                    m.sendError("API Exception!");
                }
                return;
            }

            help(e, args);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Random");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "random number [max]", "Gives random number.", false);
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "random quote", "Gives random quote.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
