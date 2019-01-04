package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Divorces a player if a player is married
 *
 * @see MarryPlayer
 * @author sho
 */
public class DivorcePlayer implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            Player player = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());

            // Player's marry data, in format `player_id&guild_id`
            String[] playerMarryData = ((String) player.getObject(DataType.MARRY_DATA)).split("&");

            // Makes sure it's just the username and the guild id
            if (playerMarryData.length != 2) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION);
                return;
            }

            // If either are none, return
            if (playerMarryData[0].equalsIgnoreCase("none") || playerMarryData[1].equalsIgnoreCase("none")) {
                messageHandler.sendError("You aren't married to anyone!");
                return;
            }

            // Gets the guild
            IGuild guild;
            try {
                guild = e.getClient().getGuildByID(Long.parseLong(playerMarryData[1]));
            } catch (NumberFormatException throwaway) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION);
                return;
            }

            // Makes sure the guild isn't deleted/doesn't exist
            if (!Util.guildExists(e.getClient(), guild.getLongID()) || guild.isDeleted()) {
                messageHandler.sendError("The user wasn't found, divorcing!");
                player.setObject(DataType.MARRY_DATA, "none&none");
                return;
            }

            // Gets the user
            IUser user;
            try {
                user = guild.getUserByID(Long.parseLong(playerMarryData[0]));
            } catch (NumberFormatException throwaway) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION);
                return;
            }

            // Set the divorcee
            player.setObject(DataType.MARRY_DATA, "none&none");

            // Set the divorced player
            PlayerHandler.handle.getPlayer(user.getLongID()).setObject(DataType.MARRY_DATA, "none&none");

            messageHandler.sendEmbed(new EmbedBuilder().withDesc("Divorced player `" + user.getName() + "`."));
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("divorce <@user>", "Request to divorce a user.");
        return Command.helpCommand(st, "Divorce", e);
    }
}
