package com.jhobot.commands.info;

import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.SQL;
import com.jhobot.handle.Util;
import com.jhobot.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Jho implements Command
{
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("jhobot");
        b.withDesc("by j9ke and sho!");
        b.appendField("Version", JSON.get("version"), true);
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        new Messages(e.getChannel()).sendEmbed(b.build());
    }

    @Override
    public void helpCommand(MessageReceivedEvent e) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : Jho");
        b.appendField(SQL.get(e.getGuild(), "prefix") + "jho", "Gives information about the bot.", false);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        new Messages(e.getChannel()).sendEmbed(b.build());
    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e) {
        return e.getChannel().getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES);
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e) {
        return true;
    }
}
