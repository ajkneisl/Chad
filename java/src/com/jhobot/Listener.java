package com.jhobot;

import com.jhobot.handle.Commands;
import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Util;
import org.bson.Document;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Listener
{
    @EventSubscriber
    public void messageRecieved(MessageReceivedEvent e)
    {
        Commands.call(e);
    }

    @EventSubscriber
    public void joinGuild(GuildCreateEvent e)
    {
        DB dbb = new DB(JSON.get("uri_link"));
        if (!dbb.exists(e.getGuild()))
        {
            Document doc = new Document();

            doc.append("guildid", e.getGuild().getStringID());
            doc.append("prefix", "j!");
            doc.append("muted_role", "none");
            doc.append("logging", false);
            doc.append("logging_channel", "none");
            doc.append("cmd_requires_admin", false);
            doc.append("music_requires_admin", false);
            doc.append("role_on_join", false);
            doc.append("join_role", "none");
            doc.append("ban_message", "You have been banned from &guild&.");
            doc.append("kick_message", "You have been kicked from &guild&.");

            dbb.getCollection().insertOne(doc);

            System.out.println(Util.getTimeStamp() + " <" + e.getGuild().getStringID() + "> Joined Guild");
        }
    }

    @EventSubscriber
    public void leaveGuild(GuildLeaveEvent e)
    {
        // do stuff
    }
}
