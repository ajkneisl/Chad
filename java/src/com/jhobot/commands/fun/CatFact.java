package com.jhobot.commands.fun;

import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.CommandClass;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CatFact implements CommandClass {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        try{
            String fact = JSON.read("https://catfact.ninja/fact").getString("fact");
            new Messages(e.getChannel()).send(fact, "Cat Fact");
        } catch (IOException ee)
        {
            ee.printStackTrace();
            new Messages(e.getChannel()).sendError("There was an internal error.");
        }
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : Cat Fact");
        b.appendField(db.getString(e.getGuild(), "prefix") + "catfact", "Gives you a random cat fact.", false);
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
        return e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.SEND_MESSAGES);
    }
}
