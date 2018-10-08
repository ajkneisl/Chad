package com.jhobot.commands.fun;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CatFact implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            try{
                String fact = JSON.read("https://catfact.ninja/fact").getString("fact");
                new Messages(e.getChannel()).send(fact, "Cat Fact");
            } catch (IOException ee)
            {
                ee.printStackTrace();
                new Messages(e.getChannel()).sendError("There was an internal error.");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Cat Fact");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "catfact", "Gives you a random cat fact.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
