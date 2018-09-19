package com.jhobot.commands.info;

import com.jhobot.handle.Messages;
import com.jhobot.handle.DB;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.CommandClass;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GuildInfo implements CommandClass {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        IGuild g = e.getGuild();
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Guild : " + g.getName());
        b.appendField("Owner", g.getOwner().getName(), true);
        b.appendField("Role Amount", Integer.toString(g.getRoles().size()), true);
        int human = 0;
        int bot = 0;
        for (IUser u : g.getUsers())
        {
            if (u.isBot())
                human++;
            else
                bot++;
        }
        b.appendField("Humans", Integer.toString(human), true);
        b.appendField("Bots", Integer.toString(bot), true);
        b.appendField("User Amount", Integer.toString(g.getUsers().size()), true);
        b.appendField("Voice Channels", Integer.toString(g.getVoiceChannels().size()), true);
        b.appendField("Text Channels", Integer.toString(g.getChannels().size()), true);
        b.appendField("Categories", Integer.toString(g.getCategories().size()), true);
        Date date = Date.from(e.getGuild().getCreationDate());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        b.appendField("Creation Date", format.format(date), false);

        b.withImage(e.getGuild().getIconURL());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        b.withFooterText(Util.getTimeStamp());
        new Messages(e.getChannel()).sendEmbed(b.build());
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : GuildInfo");
        b.appendField(db.getString(e.getGuild(), "prefix") + "guildinfo", "Gives information about the current guild.", false);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        new Messages(e.getChannel()).sendEmbed(b.build());
    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e, DB db) {
        return e.getChannel().getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES);
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e, DB db) {
        return true;
    }
}
