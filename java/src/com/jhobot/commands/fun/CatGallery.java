package com.jhobot.commands.fun;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.CommandClass;
import org.json.simple.JSONObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CatGallery implements CommandClass {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        if (args.size() == 0)
        {
            new Messages(e.getChannel()).sendFile(db.getRandomCatPicture());
            return;
        }

        if (args.size() == 2 && args.get(0).equalsIgnoreCase("add")) // j!catgallery add [path] [category]
        {
            Boolean allowed = false;
            for (Long lon : JhoBot.allowedUsers())
            {
                if (e.getAuthor().getLongID() == lon)
                    allowed = true;
            }

            if (!allowed)
            {
                new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                return;
            }
            String path = args.get(1);


            File f = new File(System.getenv("appdata") + "\\jho\\catpictures\\" + path);

            if (!f.exists())
            {
                new Messages(e.getChannel()).sendError("Image doesn't exists!");
                return;
            }

            db.addPictureToArray(path);

            new Messages(e.getChannel()).send("Added image!", "Gat Gallery");
            return;
        }

        helpCommand(e, db);
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : Cat Gallery");
        b.appendField(db.getString(e.getGuild(), "prefix") + "catgallery", "Gives you a random cat picture.", false);
        b.appendField(db.getString(e.getGuild(), "prefix") + "catgallery [keyword]", "Gives you a random cat picture within that category.", false);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new Random().nextFloat()));
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
