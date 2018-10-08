package com.jhobot.commands.fun;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Random;

public class CatGallery implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (args.size() == 0)
            {
                File[] files = new File(System.getenv("appdata") + "\\jho\\catpictures\\").listFiles();
                new Messages(e.getChannel()).sendFile(files[new Random().nextInt(files.length)]);
                return;
            }

            help(e, args);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Cat Gallery");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "catgallery", "Gives you a random cat picture.", false);
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "catgallery [keyword]", "Gives you a random cat picture within that category.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
