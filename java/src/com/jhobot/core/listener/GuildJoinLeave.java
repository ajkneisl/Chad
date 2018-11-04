package com.jhobot.core.listener;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.DatabaseHandler;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import org.bson.Document;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GuildJoinLeave
{
    @EventSubscriber
    public void userJoin(UserJoinEvent e)
    {
        // for logging
        IGuild g = e.getGuild();
        MessageHandler m = new MessageHandler(null);
        EmbedBuilder b = new EmbedBuilder();
        Date date = Date.from(e.getGuild().getCreationDate());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        b.withTitle("User Join : " + e.getUser().getName())
                .withFooterIcon(e.getUser().getAvatarURL())
                .withFooterText(Util.getTimeStamp())
                .appendField("Join Time", format.format(date), true);

        m.sendLog(b.build(), ChadVar.DATABASE_HANDLER, g);


        if (ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(), "join_msg_on"))
        {
            String joinMsgCh = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "join_message_ch");
            if (!joinMsgCh.equalsIgnoreCase("none")) {
                Long id = Long.parseLong(joinMsgCh);
                IChannel ch = RequestBuffer.request(() -> g.getChannelByID(id)).get();
                if (!ch.isDeleted())
                {
                    String msg = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "join_message");
                    msg = msg.replaceAll("&user&", e.getUser().getName()).replaceAll("&guild&", e.getGuild().getName());
                    new MessageHandler(ch).sendMessage(msg);
                }
            }
        }

        // does the bot have MANAGE_ROLES?
        if (!ChadBot.cli.getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_ROLES)) {
            new MessageHandler(e.getGuild().getDefaultChannel()).sendError("Auto role assignment failed; Bot doesn't have permission: MANAGE_ROLES.");
            return;
        }

        // you probably shouldnt put code below this comment

        String joinRoleStringID = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "join_role");
        Long joinRoleID = Long.parseLong(joinRoleStringID);
        List<IRole> botRoles = ChadBot.cli.getOurUser().getRolesForGuild(e.getGuild());
        IRole joinRole = e.getGuild().getRoleByID(joinRoleID);

        // get the bots highest role position in the guild
        int botPosition = 0;
        for (IRole role : botRoles) {
            if (role.getPosition() > botPosition) {
                botPosition = role.getPosition();
            }
        }

        // can the bot assign the user the configured role?
        if (joinRole.getPosition() > botPosition) {
            new MessageHandler(e.getGuild().getDefaultChannel()).sendError("Auto role assignment failed; Bot isn't allowed to assign the role.");
            return;
        }

        // is the role @everyone?
        if (joinRole.isEveryoneRole()) {
            new MessageHandler(e.getGuild().getDefaultChannel()).sendError("Auto role assignment failed; Misconfigured role.");
            return;
        }

        // assign the role
        if (ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(), "role_on_join")) {
            if (!joinRoleStringID.equals("none")) {
                e.getUser().addRole(joinRole);
            }
        }
    }

    @EventSubscriber
    public void userLeave(UserLeaveEvent e)
    {
        // for logging
        IGuild g = e.getGuild();
        MessageHandler m = new MessageHandler(null);
        EmbedBuilder b = new EmbedBuilder();
        Date date = Date.from(e.getGuild().getCreationDate());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        b.withTitle("User Leave : " + e.getUser().getName())
                .withFooterIcon(e.getUser().getAvatarURL())
                .withFooterText(Util.getTimeStamp())
                .appendField("Leave Time", format.format(date), true);

        m.sendLog(b.build(), ChadVar.DATABASE_HANDLER, g);

        ChadVar.CACHE_DEVICE.unCacheGuild(e.getGuild());
        if (ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(), "leave_msg_on"))
        {
            String leaveMsgCh = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "leave_message_ch");
            if (!leaveMsgCh.equalsIgnoreCase("none"))
            {
                Long id = Long.parseLong(leaveMsgCh);
                IChannel ch = RequestBuffer.request(() -> g.getChannelByID(id)).get();
                if (!ch.isDeleted())
                {
                    String msg = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "leave_message");
                    msg = msg.replaceAll("&user&", e.getUser().getName()).replaceAll("&guild&", e.getGuild().getName());
                    new MessageHandler(ch).sendMessage(msg);
                }
            }
        }
    }

    @EventSubscriber
    public void joinGuild(GuildCreateEvent e)
    {
        DatabaseHandler dbb = ChadVar.DATABASE_HANDLER;
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
            doc.append("join_message", "`&user&` has joined the guild!");
            doc.append("leave_message", "`&user&` has left the guild!");
            doc.append("join_msg_on", false);
            doc.append("leave_msg_on", false);
            doc.append("ban_msg_on", true);
            doc.append("kick_msg_on", true);
            doc.append("join_message_ch", "none");
            doc.append("leave_message_ch", "none");

            dbb.getCollection().insertOne(doc);

            ChadVar.UI_HANDLER.addLog("<"+e.getGuild().getStringID()+"> Joined Guild");
        }
    }

    @EventSubscriber
    public void leaveGuild(GuildLeaveEvent e)
    {
        DatabaseHandler databaseHandler = ChadVar.DATABASE_HANDLER;
        Document get = databaseHandler.getCollection().find(new Document("guildid", e.getGuild().getStringID())).first();

        if (get == null)
            return;

        databaseHandler.getCollection().deleteOne(get);

        ChadVar.UI_HANDLER.addLog("<"+e.getGuild().getStringID()+"> Joined Guild");
    }
}
