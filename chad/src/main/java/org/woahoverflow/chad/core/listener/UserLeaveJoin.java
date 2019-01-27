package org.woahoverflow.chad.core.listener;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.woahoverflow.chad.core.ChadInstance;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import org.woahoverflow.chad.framework.ui.ChadError;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * User joining and Leaving events
 *
 * @author sho, codebasepw
 */
public final class UserLeaveJoin {

    private static final Pattern USER_PATTERN = Pattern.compile("&user&");
    private static final Pattern GUILD_PATTERN = Pattern.compile("&guild&");

    /**
     * The event when a user joins
     *
     * @param e The event
     */
    @SuppressWarnings("unused")
    @EventSubscriber
    public void userJoin(UserJoinEvent e) {
        // Add it to the user's data set
        Player player = PlayerHandler.handle.getPlayer(e.getUser().getLongID());

        @SuppressWarnings("unchecked")
        ArrayList<Long> guildData = (ArrayList<Long>) player.getObject(DataType.GUILD_DATA);

        if (!guildData.contains(e.getGuild().getLongID()))
            guildData.add(e.getGuild().getLongID());

        player.setObject(
            DataType.GUILD_DATA,
            guildData
        );

        // Logs the user's join
        IGuild guild = e.getGuild();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        // Builds the embed
        embedBuilder.withTitle("User Join : " + e.getUser().getName())
                .withFooterIcon(e.getUser().getAvatarURL())
                .appendField("Join Time", format.format(Date.from(e.getGuild().getCreationDate())), true);

        // Sends the log
        MessageHandler.sendLog(embedBuilder, guild);

        // If the guild has user join messages on, do that
        Guild g = GuildHandler.handle.getGuild(e.getGuild().getLongID());
        if ((boolean) g.getObject(Guild.DataType.JOIN_MESSAGE_ON)) {
            // Gets the join message channel
            String joinMsgCh = (String) g.getObject(Guild.DataType.JOIN_MESSAGE_CHANNEL);

            // Makes sure they actually assigned a channel
            if (joinMsgCh != null && !joinMsgCh.equalsIgnoreCase("none")) {
                final long id;
                try {
                    id = Long.parseLong(joinMsgCh);
                } catch (NumberFormatException throwaway)
                {
                    // Throws error in the UI
                    ChadError.throwError("Guild " + guild.getName() + " has an invalid join message channel!");
                    return;
                }
                // Gets the channel
                IChannel channel = RequestBuffer.request(() -> guild.getChannelByID(id)).get();

                // Makes sure the channel isn't deleted
                if (!channel.isDeleted()) {
                    // Gets the message, makes sure it isn't null, then sends
                    String msg = (String) g.getObject(Guild.DataType.JOIN_MESSAGE);
                    if (msg != null) {
                        msg = GUILD_PATTERN.matcher(USER_PATTERN.matcher(msg).replaceAll(e.getUser().getName())).replaceAll(e.getGuild().getName());
                        String finalMsg = msg;
                        RequestBuffer.request(() -> channel.sendMessage(finalMsg));
                    }
                }
            }
        }

        // does the bot have MANAGE_ROLES?
        if (!ChadInstance.cli.getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_ROLES)) {
            new MessageHandler(e.getGuild().getDefaultChannel(), e.getUser()).sendError("Auto role assignment failed; Bot doesn't have permission: MANAGE_ROLES.");
            return;
        }

        // you probably shouldn't put code below this comment

        String joinRoleStringID = (String) g.getObject(Guild.DataType.JOIN_ROLE);
        if (joinRoleStringID != null && !joinRoleStringID.equalsIgnoreCase("none")) {
            Long joinRoleID = Long.parseLong(joinRoleStringID);
            List<IRole> botRoles = ChadInstance.cli.getOurUser().getRolesForGuild(e.getGuild());
            IRole joinRole = e.getGuild().getRoleByID(joinRoleID);

            // get the bots highest role position in the guild
            int botPosition = 0;
            for (IRole role : botRoles)
                if (role.getPosition() > botPosition)
                    botPosition = role.getPosition();

            // can the bot assign the user the configured role?
            if (joinRole.getPosition() > botPosition) {
                new MessageHandler(e.getGuild().getDefaultChannel(), e.getUser()).sendError("Auto role assignment failed; Bot isn't allowed to assign the role.");
                return;
            }

            // is the role @everyone?
            if (joinRole.isEveryoneRole()) {
                new MessageHandler(e.getGuild().getDefaultChannel(), e.getUser()).sendError("Auto role assignment failed; Misconfigured role.");
                return;
            }

            // assign the role
            if ((boolean) g.getObject(Guild.DataType.ROLE_ON_JOIN))
                if (!joinRoleStringID.equals("none"))
                    e.getUser().addRole(joinRole);
        }
    }

    /**
     * The event when a user leaves
     *
     * @param e The event
     */
    @SuppressWarnings({"unused", "unchecked"})
    @EventSubscriber
    public void userLeave(UserLeaveEvent e) {
        // Remove it from the user's data set
        Player player = PlayerHandler.handle.getPlayer(e.getUser().getLongID());

        ArrayList<Long> guildData = (ArrayList<Long>) player.getObject(DataType.GUILD_DATA);

        // Remove the guild that was left
        guildData.remove(e.getGuild().getLongID());

        Guild g = GuildHandler.handle.getGuild(e.getGuild().getLongID());

        if (guildData.isEmpty()) {
            // If it's the last guild that they're in with Chad, remove theirs
            MongoCollection<Document> col = DatabaseManager.USER_DATA.collection;
            Document document = col.find(new Document("id", e.getUser().getLongID())).first();

            if (document != null)
                col.deleteOne(document);
        } else {
            player.setObject(
                DataType.GUILD_DATA,
                guildData
            );
        }

        // Log if the user leaves
        IGuild guild = e.getGuild();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        // Builds the embed
        embedBuilder.withTitle("User Leave : " + e.getUser().getName())
            .withFooterIcon(e.getUser().getAvatarURL())
            .appendField("User Leave", format.format(Date.from(e.getGuild().getCreationDate())), true);

        // Sends the log
        MessageHandler.sendLog(embedBuilder, guild);

        // If the guild has user leave messages on, do that
        if ((boolean) g.getObject(Guild.DataType.LEAVE_MESSAGE_ON)) {
            // Gets the leave message channel
            String leaveMsgCh = (String) g.getObject(Guild.DataType.LEAVE_MESSAGE_CHANNEL);

            // Makes sure they actually assigned a channel
            if (leaveMsgCh != null && !leaveMsgCh.equalsIgnoreCase("none")) {
                final long id;
                try {
                    id = Long.parseLong(leaveMsgCh);
                } catch (NumberFormatException throwaway) {
                    // Throws error in the UI
                    ChadError.throwError("Guild " + guild.getName() + " has an invalid leave message channel!");
                    return;
                }
                // Gets the channel
                IChannel channel = RequestBuffer.request(() -> guild.getChannelByID(id)).get();

                // Makes sure the channel isn't deleted
                if (!channel.isDeleted()) {
                    // Gets the message, makes sure it isn't null, then sends
                    String msg = (String) g.getObject(Guild.DataType.LEAVE_MESSAGE);
                    if (msg != null) {
                        msg = GUILD_PATTERN.matcher(USER_PATTERN.matcher(Objects.requireNonNull(msg)).replaceAll(e.getUser().getName())).replaceAll(e.getGuild().getName());
                        String finalMsg = msg;
                        RequestBuffer.request(() -> channel.sendMessage(finalMsg));
                    }
                }
            }
        }
    }
}
