package com.jhobot.commands.info;

import com.jhobot.JhoBot;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.DB;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
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
            b.appendField("Version", JSON.get("version"), true);
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Jho");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "jho", "Gives information about the bot.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
