package com.jhobot.commands.punishments;

import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ban implements Command {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        Messages m = new Messages(e.getChannel());
        IUser user = null;
        List<String> reason = new ArrayList<>();
        if (!e.getMessage().getMentions().isEmpty() && args.get(0).equalsIgnoreCase( "@" + e.getMessage().getMentions().get(0).getName()))
        {
            user = e.getMessage().getMentions().get(0);
            args.remove(0);
            reason = args;
        }
        else {
            StringBuilder sb = new StringBuilder();
            for (String s : args)
            {
                if (user == null)
                {
                    sb.append(s + " ");
                    if (!e.getGuild().getUsersByName(sb.toString().trim()).isEmpty())
                    {
                        user = e.getGuild().getUsersByName(sb.toString().trim()).get(0);
                    }
                }
                else {
                    reason.add(s);
                }
            }
        }

        if (user == null)
        {
            m.sendError("Invalid User!");
            return;
        }

        if (user.getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
        {
            m.sendError("Bot can't do this!");
            return;
        }

        StringBuilder sb2 = new StringBuilder();
        for (String s : reason)
        {
            sb2.append(s + " ");
        }

        if (!db.getString(e.getGuild(), "ban_message").equalsIgnoreCase("&disabled&") || !db.getString(e.getGuild(), "ban_message").toLowerCase().contains("&disabled&") && !user.isBot())
        {
            String[] msgsArray = db.getString(e.getGuild(), "ban_message").split(" ");
            List<String> msgs = Arrays.asList(msgsArray);
            for (int i = 0; msgs.size() > i; i++)
            {
                if (msgs.get(i).toLowerCase().contains("&guild&"))
                    msgs.set(i, e.getGuild().getName() + msgs.get(i).substring(7));
                if (msgs.get(i).toLowerCase().contains("&user&"))
                    msgs.set(i, user.getName() + msgs.get(i).substring(6));
                if (msgs.get(i).toLowerCase().contains("&reason&"))
                    msgs.set(i, sb2.toString().trim() + msgs.get(i).substring(8));
            }
            StringBuilder sb = new StringBuilder();
            for (String s : msgs)
            {
                sb.append(s + " ");
            }

            new MessageBuilder(e.getClient()).withChannel(e.getClient().getOrCreatePMChannel(user)).withContent(sb.toString().trim()).build();
        }

        // ban
        e.getGuild().banUser(user);
        m.send("Successfully banned " + user.getName() + " for " + sb2.toString().trim() + ".", "Banned User");
        m.sendPunishLog("Ban", user, e.getAuthor(), db, e.getGuild(), reason);
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {

    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e, DB db) {
        return e.getClient().getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.BAN);
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e, DB db) {
        return e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.BAN);
    }
}
