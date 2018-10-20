package com.jhobot.commands.info;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Jho implements Command
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("jhobot");
            b.withDesc("by sho!");
            b.appendField("Version", Util.getCurrentVersion(), true);
            b.appendField("GitHub", "https://github.com/shoganeko/jhobot", true);
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("jho", "Gives information about the bot.");
        return HelpHandler.helpCommand(st, "Jho", e);
    }
}
