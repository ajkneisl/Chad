package org.woahoverflow.chad.core.listener;

import org.bson.Document;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.obj.Permissions;

public final class GuildJoinLeave
{
    @SuppressWarnings("unused")
    @EventSubscriber
    public void joinGuild(GuildCreateEvent e)
    {
        if (!DatabaseHandler.handle.exists(e.getGuild()))
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
            doc.append("stop_swear", false);
            doc.append("swear_message", "No Swearing `&user&`!");

            DatabaseHandler.handle.getCollection().insertOne(doc);
            UIHandler.displayGuild(e.getGuild());
            UIHandler.handle.addLog('<' +e.getGuild().getStringID()+"> Joined Guild", UIHandler.LogLevel.INFO);
            Chad.getGuild(e.getGuild()).cache();
        }
        Chad.getGuild(e.getGuild()).cache();
    }

    @SuppressWarnings("unused")
    @EventSubscriber
    public static void leaveGuild(GuildLeaveEvent e)
    {
        Document get = DatabaseHandler.handle.getCollection().find(new Document("guildid", e.getGuild().getStringID())).first();

        if (get == null)
            return;

        DatabaseHandler.handle.getCollection().deleteOne(get);

        Chad.unCacheGuild(e.getGuild());

        UIHandler.handle.addLog('<' +e.getGuild().getStringID()+"> Left Guild", UIHandler.LogLevel.INFO);
    }
}
