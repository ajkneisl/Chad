package com.jhobot.commands.function;

import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.DB;
import com.jhobot.handle.Util;
import com.jhobot.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Prefix implements Command {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        if (args.size() == 0) {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Prefix");
            b.withDesc("Your prefix is " + db.getString(e.getGuild(), "prefix"));
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
            return;
        }

        if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
        {
            if (args.get(1).length() > 12)
            {
                new Messages(e.getChannel()).sendError("Prefix can't be over 12 characters long!");
            }
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Changed Prefix");
            b.withDesc("Your prefix is now " + args.get(1));
            db.set(e.getGuild(), "prefix", args.get(1));
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
            return;
        }

        new Messages(e.getChannel()).sendError("Invalid Arguments");
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : Prefix");
        b.appendField(db.getString(e.getGuild(), "prefix")+"prefix", "Gives information about your prefixes.", false);
        b.appendField(db.getString(e.getGuild(), "prefix") + "prefix set <prefix>", "Sets your guild's prefix.", false);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        new Messages(e.getChannel()).sendEmbed(b.build());
    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e, DB db) {
        return e.getClient().getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.SEND_MESSAGES);
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e, DB db) {
        return e.getChannel().getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES);
    }
}
