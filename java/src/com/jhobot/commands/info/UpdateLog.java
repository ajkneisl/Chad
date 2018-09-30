package com.jhobot.commands.info;

import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.CommandClass;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class UpdateLog implements CommandClass
{
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        if (args.size() == 0)
        {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Current Version : unstable-0.1.03");
            b.appendField("Fixed", "An error where if you put 0 as a random number entry it did nothing.", true);
            b.appendField("Added", "This command.", true);
            b.appendField("Added", "Cat Gallery (" + db.getString(e.getGuild(), "prefix") + "catgallery)", true);
            b.appendField("Added", "Russian Roulette (" + db.getString(e.getGuild(), "prefix") + "rrl)", true);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
            return;
        }

        new Messages(e.getChannel()).sendError("No other versions are available for review.");
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : Update Log");
        b.appendField(db.getString(e.getGuild(), "prefix") + "updatelog", "Gives you info about recent updates.", false);
        b.appendField(db.getString(e.getGuild(), "prefix") + "updatelog [update id]", "Gives you info about a specific update.", false);
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
