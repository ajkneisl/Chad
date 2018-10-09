package com.jhobot.commands.info;

import com.jhobot.JhoBot;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Help implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            IMessage m = RequestBuffer.request(() -> {
                return e.getChannel().sendMessage("Fun : ```catfact, catgallery, 8ball, pe, random```\nFunction / Admin: ```logging, prefix```\nInfo : ```bug, guildinfo, help, jho, steam, updatelog, userinfo```\nPunishments : ```ban, kick```");
            }).get();
       };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Help");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "help", "Displays all command Jho has to offer.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
