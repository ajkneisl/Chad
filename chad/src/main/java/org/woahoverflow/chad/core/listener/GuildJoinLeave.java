package org.woahoverflow.chad.core.listener;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import org.woahoverflow.chad.framework.handle.database.DatabaseHandle;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

/**
 * The Discord guild and join events
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public final class GuildJoinLeave
{

    /**
     * The handle for the guild's data
     */
    private final DatabaseHandle handle = DatabaseManager.handle.getSeparateCollection("bot", "guildid");

    /**
     * Discord's Joining Guild Event
     *
     * @param event Guild Create Event
     */
    @EventSubscriber
    @SuppressWarnings({"unused", "unchecked"})
    public void joinGuild(GuildCreateEvent event)
    {
        // Makes sure all users are into the database
        event.getGuild().getUsers().forEach(user ->
            Chad.runThread(() -> {
                Player player = PlayerHandler.handle.getPlayer(user.getLongID());

                ArrayList<Long> guildData = (ArrayList<Long>) player.getObject(DataType.GUILD_DATA);

                if (!guildData.contains(event.getGuild().getLongID()))
                {
                    guildData.add(event.getGuild().getLongID());
                    player.setObject(DataType.GUILD_DATA, guildData);
                }
            }, Chad.getInternalConsumer()));

        if (!handle.documentExists(event.getGuild().getStringID()))
        {
            Document doc = new Document();

            doc.append("guildid", event.getGuild().getLongID());
            doc.append("prefix", "j!");
            doc.append("logging", false);
            doc.append("logging_channel", "none");
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

            // Display the new guild in the UI
            UIHandler.displayGuild(event.getGuild());

            // Send a log with the new guild
            UIHandler.handle.addLog('<' +event.getGuild().getStringID()+"> Joined Guild", UIHandler.LogLevel.INFO);

            // Cache the guild
            GuildHandler.handle.refreshGuild(event.getGuild().getLongID());

            // The guild's default channel
            IChannel defaultChannel = RequestBuffer.request(() -> event.getGuild().getDefaultChannel()).get();

            // The join message
            final String joinMessage = "Hello, I'm Chad!\nMy prefix is by default `j!`, to set it you can do `j!prefix set <prefix>`\nFor more information about my commands, go to https://woahoverflow.org/chad";

            // If the bot has permission to, send the join message into the default channel
            if (RequestBuffer.request(() -> defaultChannel.getModifiedPermissions(event.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES)).get())
                RequestBuffer.request(() -> event.getGuild().getDefaultChannel().sendMessage(joinMessage));
            else
            {
                // Parse through all of the guilds, and if the bot has permission send the join message.
                List<IChannel> guilds = RequestBuffer.request(() -> event.getGuild().getChannels()).get();
                int channelSize = guilds.size();

                for (int i = 0; channelSize > i; i++)
                {
                    IChannel channel = guilds.get(0);

                    if (RequestBuffer.request(() -> channel.getModifiedPermissions(event.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES)).get())
                    {
                        RequestBuffer.request(() -> channel.sendMessage(joinMessage));
                    }
                }
            }
        }
        GuildHandler.handle.refreshGuild(event.getGuild().getLongID());
    }

    /**
     * Discord's Leave Guild Event
     *
     * @param event Guild Leave Event
     */
    @EventSubscriber
    @SuppressWarnings("unused")
    public void leaveGuild(GuildLeaveEvent event)
    {
        // Delete the guild's document
        handle.removeDocument(event.getGuild().getStringID());

        // Removed the guild's cached document
        //TODO: pretty sure this is how this is supposed to work
        GuildHandler.handle.removeGuild(event.getGuild().getLongID());

        // Un Cache the guild
        //TODO: pretty sure ya only need to do it once :/

        // Send a log
        UIHandler.handle.addLog('<' +event.getGuild().getStringID()+"> Left Guild", UIHandler.LogLevel.INFO);
    }
}
