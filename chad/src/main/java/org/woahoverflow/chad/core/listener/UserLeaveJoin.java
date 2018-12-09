package org.woahoverflow.chad.core.listener;

import java.util.Objects;
import java.util.regex.Pattern;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.Player.DataType;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.framework.handle.PlayerManager;
import org.woahoverflow.chad.framework.ui.ChadError;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User joining and Leaving events
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public final class UserLeaveJoin
{

    private static final Pattern USER_PATTERN = Pattern.compile("&user&");
    private static final Pattern GUILD_PATTERN = Pattern.compile("&guild&");

    /**
     * The event when a user joins
     *
     * @param e The event
     */
    @SuppressWarnings("unused")
    @EventSubscriber
    public void userJoin(UserJoinEvent e)
    {
        // Logs the user's join
        IGuild guild = e.getGuild();
        MessageHandler messageHandler = new MessageHandler(null);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        // Builds the embed
        embedBuilder.withTitle("User Join : " + e.getUser().getName())
                .withFooterIcon(e.getUser().getAvatarURL())
                .appendField("Join Time", format.format(Date.from(e.getGuild().getCreationDate())), true);

        // Sends the log
        MessageHandler.sendLog(embedBuilder, guild);

        // If the guild has user join messages on, do that
        if (DatabaseHandler.handle.getBoolean(e.getGuild(), "join_msg_on"))
        {
            // Gets the join message channel
            String joinMsgCh = DatabaseHandler.handle.getString(e.getGuild(), "join_message_ch");

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
                if (!channel.isDeleted())
                {
                    // Gets the message, makes sure it isn't null, then sends
                    String msg = DatabaseHandler.handle.getString(e.getGuild(), "join_message");
                    if (msg != null)
                    {
                        msg = GUILD_PATTERN.matcher(USER_PATTERN.matcher(msg).replaceAll(e.getUser().getName())).replaceAll(e.getGuild().getName());
                        String finalMsg = msg;
                        RequestBuffer.request(() -> channel.sendMessage(finalMsg));
                    }
                }
            }
        }

        // does the bot have MANAGE_ROLES?
        if (!ChadBot.cli.getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_ROLES))
        {
            messageHandler.sendError("Auto role assignment failed; Bot doesn't have permission: MANAGE_ROLES.");
            return;
        }

        // you probably shouldn't put code below this comment

        String joinRoleStringID = DatabaseHandler.handle.getString(e.getGuild(), "join_role");
        if (joinRoleStringID != null && !joinRoleStringID.equalsIgnoreCase("none"))
        {
            Long joinRoleID = Long.parseLong(joinRoleStringID);
            List<IRole> botRoles = ChadBot.cli.getOurUser().getRolesForGuild(e.getGuild());
            IRole joinRole = e.getGuild().getRoleByID(joinRoleID);

            // get the bots highest role position in the guild
            int botPosition = 0;
            for (IRole role : botRoles)
                if (role.getPosition() > botPosition)
                    botPosition = role.getPosition();

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
            if (DatabaseHandler.handle.getBoolean(e.getGuild(), "role_on_join"))
                if (!joinRoleStringID.equals("none"))
                    e.getUser().addRole(joinRole);
        }
    }

    /**
     * The event when a user leaves
     *
     * @param e The event
     */
    @SuppressWarnings("unused")
    @EventSubscriber
    public void userLeave(UserLeaveEvent e)
    {
        // Sets their balance to 0
        DatabaseHandler.handle.set(e.getGuild(), e.getUser().getStringID()+"_balance", Long.parseLong("0"));

        // Removes their marriage status
        Player player = PlayerManager.handle.getPlayer(e.getUser().getLongID());
        if (!(((String) player.getObject(DataType.MARRY_DATA)).split("&")[0].equalsIgnoreCase("none") || ((String) player.getObject(DataType.MARRY_DATA)).split("&")[1].equalsIgnoreCase("none")))
        {
            // Data
            String[] marriageData = ((String) player.getObject(DataType.MARRY_DATA)).split("&");

            // Author's Status
            player.setObject(DataType.MARRY_DATA, "none&none");

            // Sets the other player's status
            try {
                long guildId = Long.parseLong(marriageData[1]);
                if (!Util.guildExists(e.getClient(), guildId))
                {
                    player.setObject(DataType.MARRY_DATA, "none&none");
                }
                else {
                    long userId = Long.parseLong(marriageData[0]);
                    IGuild guild = e.getClient().getGuildByID(guildId);
                    IUser user = guild.getUserByID(userId);

                    PlayerManager.handle.getPlayer(user.getLongID()).setObject(DataType.MARRY_DATA, "none&none");
                }
            } catch (NumberFormatException throwaway) {
                // :(
            }
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
        if (DatabaseHandler.handle.getBoolean(e.getGuild(), "leave_msg_on"))
        {
            // Gets the leave message channel
            String leaveMsgCh = DatabaseHandler.handle.getString(e.getGuild(), "leave_message_ch");

            // Makes sure they actually assigned a channel
            if (leaveMsgCh != null && !leaveMsgCh.equalsIgnoreCase("none")) {
                final long id;
                try {
                    id = Long.parseLong(leaveMsgCh);
                } catch (NumberFormatException throwaway)
                {
                    // Throws error in the UI
                    ChadError.throwError("Guild " + guild.getName() + " has an invalid leave message channel!");
                    return;
                }
                // Gets the channel
                IChannel channel = RequestBuffer.request(() -> guild.getChannelByID(id)).get();

                // Makes sure the channel isn't deleted
                if (!channel.isDeleted())
                {
                    // Gets the message, makes sure it isn't null, then sends
                    String msg = DatabaseHandler.handle.getString(e.getGuild(), "leave_message");
                    if (msg != null)
                    {
                        msg = GUILD_PATTERN.matcher(USER_PATTERN.matcher(Objects.requireNonNull(msg)).replaceAll(e.getUser().getName())).replaceAll(e.getGuild().getName());
                        String finalMsg = msg;
                        RequestBuffer.request(() -> channel.sendMessage(finalMsg));
                    }
                }
            }
        }
    }
}
