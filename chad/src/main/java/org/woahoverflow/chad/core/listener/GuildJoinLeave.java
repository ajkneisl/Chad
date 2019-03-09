package org.woahoverflow.chad.core.listener;

import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import org.woahoverflow.chad.framework.ui.UI;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * The Discord guild join and leave events
 *
 * @author sho, codebasepw
 */
public final class GuildJoinLeave {
    /**
     * Discord's Joining Guild Event
     *
     * @param event Guild Create Event
     */
    @EventSubscriber
    @SuppressWarnings({"unused", "unchecked"})
    public void joinGuild(GuildCreateEvent event) {
        // Makes sure all users are into the database
        new Thread(() -> {
            List<IUser> users = RequestBuffer.request(() -> event.getGuild().getUsers()).get();
            for (IUser user : users) {
                Player player = PlayerHandler.handle.getPlayer(user.getLongID());
                ArrayList<Long> guildData = (ArrayList<Long>) player.getObject(DataType.GUILD_DATA);
                if (!guildData.contains(event.getGuild().getLongID())) {
                    guildData.add(event.getGuild().getLongID());
                    player.setObject(DataType.GUILD_DATA, guildData);
                }
            }
        });

        if (!GuildHandler.handle.guildDataExists(event.getGuild().getLongID())) {
            // By retrieving the guild's instance, it creates an instance for the guild within the database
            Guild guild = GuildHandler.handle.getGuild(event.getGuild().getLongID());

            // Display the new guild in the UI
            UI.displayGuild(event.getGuild());

            // Send a log with the new guild
            UI.handle.addLog('[' +event.getGuild().getStringID()+"] Joined Guild", UI.LogLevel.INFO);

            // The guild's default channel
            IChannel defaultChannel = RequestBuffer.request(() -> event.getGuild().getDefaultChannel()).get();

            // The join message
            final String joinMessage = "Hello, i'm Chad!\n"
                + "My prefix is `c!`\n\n"
                + "View my commands with `c!help`";

            // If the bot has permission to, send the join message into the default channel
            if (RequestBuffer.request(() -> defaultChannel.getModifiedPermissions(event.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES)).get())
                RequestBuffer.request(() -> event.getGuild().getDefaultChannel().sendMessage(joinMessage));
            else {
                // Parse through all of the guilds, and if the bot has permission send the join message.
                List<IChannel> guilds = RequestBuffer.request(() -> event.getGuild().getChannels()).get();
                int channelSize = guilds.size();

                for (int i = 0; channelSize > i; i++) {
                    IChannel channel = guilds.get(0);

                    if (RequestBuffer.request(() -> channel.getModifiedPermissions(event.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES)).get()) {
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
        UI.handle.addLog('<' +event.getGuild().getStringID()+"> Left Guild", UI.LogLevel.INFO);
    }
}
