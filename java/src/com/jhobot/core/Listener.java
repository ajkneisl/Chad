package com.jhobot;

import com.jhobot.handle.commands.Commands;
import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Util;
import org.bson.BsonArray;
import org.bson.Document;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
            if (!e.getClient().getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_ROLES))
                doc.append("muted_role", "none_np");
            else
                doc.append("muted_role", "none");
            doc.append("muted_role", "none");
            doc.append("logging", false);
            doc.append("logging_channel", "none");
            doc.append("cmd_requires_admin", false);
            doc.append("music_requires_admin", false);
            doc.append("role_on_join", false);
            doc.append("join_role", "none");
            doc.append("ban_message", "You have been banned from &guild&. \n &reason&");
            doc.append("kick_message", "You have been kicked from &guild&. \n &reason&");
            doc.append("allow_level_message", false);
            doc.append("allow_leveling", false);

            dbb.getCollection().insertOne(doc);

            System.out.println(Util.getTimeStamp() + " <" + e.getGuild().getStringID() + "> Joined Guild");
        }
    }

    @EventSubscriber
    public void leaveGuild(GuildLeaveEvent e)
    {
        DB db = new DB(JSON.get("uri_link"));
        Document get = db.getCollection().find(new Document("guildid", e.getGuild().getStringID())).first();

        if (get == null)
            return;

        db.getCollection().deleteOne(get);

        System.out.println(Util.getTimeStamp() + " <" + e.getGuild().getStringID() + "> Left Guild");
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent e)
    {
        com.jhobot.JhoBot.exec.execute(() -> {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    String[] ar = {"hello!", "gamers", "epic gamers", "a bad game", "j!help", "j!prefix set *", "what's going on gamers", "invite me please"};
                    e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, ar[new Random().nextInt(ar.length)]);
                }
            }, 0, 60000*5);
        });

        com.jhobot.JhoBot.exec.execute(() -> {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("---< STATS >---");
                    System.out.println("Guilds > "+ RequestBuffer.request(() -> {
                        return e.getClient().getGuilds().size();
                    }).get());
                    int i = 0;
                    for (IGuild g : RequestBuffer.request(() -> {
                        return e.getClient().getGuilds();
                    }).get())
                    {
                        for (IUser u : RequestBuffer.request(() -> {
                            return g.getUsers();
                        }).get())
                        {
                            i++;
                        }
                    }
                    System.out.println("Total Users in Guilds > " + i);
                }
            }, 0, 60000*5);
        });
    }
}
