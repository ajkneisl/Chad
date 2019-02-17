package org.woahoverflow.chad.commands.info;

import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets info about a discord user
 *
 * @author sho, codebasepw
 */
public class UserInfo implements Command.Class {
    @Override
    public final Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            IUser u;
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Gets the user from the mentions
            if (!e.getMessage().getMentions().isEmpty() && args.size() == 1)
                u = e.getMessage().getMentions().get(0);
            else {
                // If user wasn't mentioned, return
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_USER, GuildHandler.handle.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX) + "userinfo **@user**");
                return;
            }


            String roleBuilder = u.getRolesForGuild(e.getGuild()).stream()
                .filter(r -> !r.isEveryoneRole()) // Makes sure role isn't @everyone
                .map(r -> r.getName() + ", ") // Puts them all together
                .collect(Collectors.joining()); // Joins

            // Create an embed builder, and begin
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("User : " + u.getName());

            // If the user has no roles, set to none, if not add the roles.
            String roleString = roleBuilder.isEmpty() ? "none"
                : roleBuilder.substring(0, roleBuilder.length() - 2) + " [" + (
                    u.getRolesForGuild(e.getGuild()).size() - 1) + ']';

            // If the user is a bot, no, if they're a human yes
            String human = u.isBot() ? "No" : "Yes";

            // To make the dates look slightly better
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

            // Sets the description
            embedBuilder.withDesc(
                    "Human `"+human+ '`' +
                            "\nRoles `"+roleString+ '`' +
                            "\nGuild Join Date `"+format.format(Date.from(e.getGuild().getJoinTimeForUser(u)))+ '`'+
                            "\nAccount Creation Date `"+format.format(Date.from(u.getCreationDate()))+ '`'
            );

            // Sends the embed with the user's avatar.
            embedBuilder.withImage(u.getAvatarURL());
            messageHandler.sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("userinfo <@user>", "Gives information about the mentioned user.");
        return Command.helpCommand(st, "User Info", e);
    }
}
