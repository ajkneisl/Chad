package com.jhobot.commands.info;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UpdateLog implements Command
{
    @DefineCommand(category = Category.INFO)
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (args.size() == 0)
            {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Current Version : v0.2");
                b.appendField("Added", "Bug reports!", true);
                b.appendField("Added", "Multithreading!", true);
                b.appendField("Fixed", "Random bugs.", true);
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                m.sendEmbed(b.build());
                return;
            }

            m.sendError("Invalid Arguments");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("updatelog", "Gives you info about the most recent update.");
        return HelpHandler.helpCommand(st, "Update Log", e);
    }
}
