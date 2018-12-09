package org.woahoverflow.chad.commands.fun;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.Player.DataType;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.7.0
 */
public class Profile implements Command.Class{

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Default variables
            Player player = PlayerManager.handle.getPlayer(e.getAuthor().getLongID());
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // If there's no arguments, get the author's profile
            if (args.isEmpty())
            {
                // Setup the embed builder
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withImage(e.getAuthor().getAvatarURL());
                embedBuilder.withTitle(e.getAuthor().getName());

                // The embed's description
                String content = "**Description** : `"+player.getObject(DataType.PROFILE_DESCRIPTION)+"`\n";

                // Marriage Data
                {
                    String[] marriageData = ((String) player.getObject(DataType.MARRY_DATA)).split("&");

                    // If they're not married to anyone, it's set to none
                    if (marriageData[0].equalsIgnoreCase("none") || marriageData[1].equalsIgnoreCase("none"))
                    {
                        content += "**Marriage** : Not married to anyone\n";
                    }
                    else {
                        // Make sure the guild is valid
                        long guildId;
                        try {
                            guildId = Long.parseLong(marriageData[1]);
                        } catch (NumberFormatException throwaway) {
                            messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                            return;
                        }

                        // Make sure the bot still has the guild
                        if (!Util.guildExists(e.getClient(), guildId))
                        {
                            messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                            player.setObject(DataType.MARRY_DATA, "none&none");
                            return;
                        }

                        // Make sure the user is valid
                        long userId;
                        try {
                            userId = Long.parseLong(marriageData[0]);
                        } catch (NumberFormatException throwaway) {
                            messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                            return;
                        }

                        // The found guild and user
                        IGuild guild = e.getClient().getGuildByID(guildId);
                        IUser user = guild.getUserByID(userId);

                        // Makes sure the user isn't null
                        if (user == null)
                        {
                            messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                            return;
                        }

                        // Add to the content
                        content += "**Marriage** : Married to `" + user.getName() + "`\n";
                    }
                }

                // Send
                embedBuilder.withDesc(content);
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            if (args.size() == 1 && e.getMessage().getMentions().size() == 1)
            {
                IUser targetUser = e.getMessage().getMentions().get(0);
                Player targetUserProfile = PlayerManager.handle.getPlayer(targetUser.getLongID());

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withImage(targetUser.getAvatarURL());
                embedBuilder.withTitle(targetUser.getName());

                String content = "**Description** : `"+targetUserProfile.getObject(DataType.PROFILE_DESCRIPTION)+"`\n";

                // Marriage Data
                {
                    String[] marriageData = ((String) targetUserProfile.getObject(DataType.MARRY_DATA)).split("&");

                    // If they're not married to anyone, it's set to none
                    if (marriageData[0].equalsIgnoreCase("none") || marriageData[1].equalsIgnoreCase("none"))
                    {
                        content += "**Marriage** : Not married to anyone\n";
                    }
                    else {
                        // Make sure the guild is valid
                        long guildId;
                        try {
                            guildId = Long.parseLong(marriageData[1]);
                        } catch (NumberFormatException throwaway) {
                            messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                            return;
                        }

                        // Make sure the bot still has the guild
                        if (!Util.guildExists(e.getClient(), guildId))
                        {
                            messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                            player.setObject(DataType.MARRY_DATA, "none&none");
                            return;
                        }

                        // Make sure the user is valid
                        long userId;
                        try {
                            userId = Long.parseLong(marriageData[0]);
                        } catch (NumberFormatException throwaway) {
                            messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                            return;
                        }

                        // The found guild and user
                        IGuild guild = e.getClient().getGuildByID(guildId);
                        IUser user = guild.getUserByID(userId);

                        // Makes sure the user isn't null
                        content +=
                            user != null ? "**Marriage** : Married to `" + user.getName() + "`\n"
                                : "**Marriage** : Not married to anyone\n";
                    }
                }

                // Send
                embedBuilder.withDesc(content);
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            // If they're setting their own description
            if (args.size() >= 1 && args.get(0).equalsIgnoreCase("desc"))
            {
                // Remove the `desc`
                args.remove(0);

                // Build the new description
                String builtString = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Make sure it's not too long
                if (builtString.length() > 200)
                {
                    messageHandler.sendError("Your description is too long!");
                    return;
                }

                // Set and send
                player.setObject(DataType.PROFILE_DESCRIPTION, builtString);
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Set your description to `"+builtString+"`!"));
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("profile <@user>", "View a user's profile.");
        st.put("profile", "View your own profile.");
        st.put("profile desc <description>", "Set your own description.");
        return Command.helpCommand(st, "Profile", e);
    }
}
