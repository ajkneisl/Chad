package org.woahoverflow.chad.core.listener;

import java.util.ArrayList;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
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
     * Discord's Joining Guild Event
     *
     * @param event Guild Create Event
     */
    @EventSubscriber
    @SuppressWarnings({"unused", "unchecked"})
    public void joinGuild(GuildCreateEvent event)
    {
        // Makes sure all users are into the database
        Chad.runThread(() -> event.getGuild().getUsers().forEach(user ->
            Chad.runThread(() -> {
                Player player = PlayerHandler.handle.getPlayer(user.getLongID());

                ArrayList<Long> guildData = (ArrayList<Long>) player.getObject(DataType.GUILD_DATA);

                if (!guildData.contains(event.getGuild().getLongID()))
                {
                    guildData.add(event.getGuild().getLongID());
                    player.setObject(DataType.GUILD_DATA, guildData);
                }
            }, Chad.getInternalConsumer())), Chad.getInternalConsumer());

        if (!DatabaseManager.GUILD_DATA.documentExists(event.getGuild().getLongID()))
        {
            // By retrieving the guild's instance, it creates an instance for the guild within the database
            Guild guild = GuildHandler.handle.getGuild(event.getGuild().getLongID());

            // Display the new guild in the UI
            UIHandler.displayGuild(event.getGuild());

            // Send a log with the new guild
            UIHandler.handle.addLog('[' +event.getGuild().getStringID()+"] Joined Guild", UIHandler.LogLevel.INFO);

            // The guild's default channel
            IChannel defaultChannel = RequestBuffer.request(() -> event.getGuild().getDefaultChannel()).get();

            // The join message
            final String joinMessage = "Hello, I'm Chad!\nMy prefix is by default `c!`, to set it you can do `c!prefix set <prefix>`\nFor more information about my commands, go to https://woahoverflow.org/chad";

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
        DatabaseManager.GUILD_DATA.removeDocument(event.getGuild().getStringID());

        // Removed the guild's cached document
        GuildHandler.handle.removeGuild(event.getGuild().getLongID());

        // Send a log
        UIHandler.handle.addLog('<' +event.getGuild().getStringID()+"> Left Guild", UIHandler.LogLevel.INFO);
    }
}
