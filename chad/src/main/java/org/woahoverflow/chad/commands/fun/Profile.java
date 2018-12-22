package org.woahoverflow.chad.commands.fun;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Class;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.7.0
 */
public class Profile implements Class{

    private static final Pattern LARGE_CODE_BLOCK = Pattern.compile("```");
    private static final Pattern SMALL_CODE_BLOCK = Pattern.compile("`");

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Default variables
            Player player = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // If there's no arguments, get the author's profile
            if (args.isEmpty())
            {
                // Setup the embed builder
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withImage(e.getAuthor().getAvatarURL());

                String title = e.getAuthor().getName();
                // The embed's description

                // Profile Title
                {
                    String titleData = (String) player.getObject(DataType.PROFILE_TITLE);

                    if (!titleData.equals("none"))
                        title += ' ' + titleData;
                }

                // Votes
                String content = "";
                {
                    long upvotes = (long) player.getObject(DataType.PROFILE_UPVOTE);
                    long downvotes = (long) player.getObject(DataType.PROFILE_DOWNVOTE);
                    long calculatedVotes = upvotes-downvotes;

                    if (calculatedVotes < 0)
                    {
                        content+= "**Warning** You have a bad reputation! \n";
                    }

                    title += String.format("  %s (+%s|-%s)", calculatedVotes, upvotes, downvotes);
                }

                content += "**Description** : `"+SMALL_CODE_BLOCK
                    .matcher(LARGE_CODE_BLOCK.matcher((String) player.getObject(DataType.PROFILE_DESCRIPTION)).replaceAll("<lcb>"))
                    .replaceAll("<scb>")+"`\n";

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
                embedBuilder.withTitle(title);
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            if (args.size() == 1 && e.getMessage().getMentions().size() == 1)
            {
                IUser targetUser = e.getMessage().getMentions().get(0);
                Player targetUserProfile = PlayerHandler.handle.getPlayer(targetUser.getLongID());

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withImage(targetUser.getAvatarURL());

                String title = targetUser.getName();

                // Profile Title
                {
                    String titleData = (String) targetUserProfile.getObject(DataType.PROFILE_TITLE);

                    if (!titleData.equals("none"))
                        title += ' ' + titleData;
                }

                String content = "";
                // Votes
                {
                    long upvotes = (long) targetUserProfile.getObject(DataType.PROFILE_UPVOTE);
                    long downvotes = (long) targetUserProfile.getObject(DataType.PROFILE_DOWNVOTE);
                    long calculatedVotes = upvotes-downvotes;


                    if (calculatedVotes <= 0)
                    {
                        content+= "**Warning** User has a bad reputation!\n";
                    }

                    title += String.format("  %s (+%s|-%s)", calculatedVotes, upvotes, downvotes);
                }

                content += "**Description** : `"+SMALL_CODE_BLOCK
                    .matcher(LARGE_CODE_BLOCK.matcher((String) targetUserProfile.getObject(DataType.PROFILE_DESCRIPTION)).replaceAll("<lcb>"))
                    .replaceAll("<scb>")+"`\n";


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
                embedBuilder.withTitle(title);
                return;
            }

            // If they're setting their own description
            if (args.size() >= 1 && args.get(0).equalsIgnoreCase("desc") && e.getMessage().getMentions().isEmpty())
            {
                // Remove the `desc`
                args.remove(0);

                // Build the new description
                String builtString = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                String showString = SMALL_CODE_BLOCK
                    .matcher(LARGE_CODE_BLOCK.matcher(builtString).replaceAll("<lcb>"))
                    .replaceAll("<scb>");

                // Make sure it's not too long
                if (builtString.length() > 200)
                {
                    messageHandler.sendError("Your description is too long!");
                    return;
                }

                // Set and send
                player.setObject(DataType.PROFILE_DESCRIPTION, builtString);
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Set your description to `"+showString+"`!"));
                return;
            }

            // If they're setting their own description
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("desc") && e.getMessage().getMentions().size() == 1 && PermissionHandler.handle.userIsDeveloper(e.getAuthor()))
            {
                IUser targetUser = e.getMessage().getMentions().get(0);

                // If it's somehow mixed
                if (!args.get(1).contains(targetUser.getStringID()))
                {
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                    return;
                }

                // Remove the `desc`
                args.remove(0);

                // Remove the player
                args.remove(0);

                // Build the new description
                String builtString = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // The formatted string to show
                String showString = SMALL_CODE_BLOCK
                    .matcher(LARGE_CODE_BLOCK.matcher(builtString).replaceAll("<lcb>"))
                    .replaceAll("<scb>");

                // Make sure it's not too long
                if (builtString.length() > 200)
                {
                    messageHandler.sendError("Your description is too long!");
                    return;
                }

                // The target user's player instance
                Player otherPlayer = PlayerHandler.handle.getPlayer(targetUser.getLongID());

                // Set and send
                otherPlayer.setObject(DataType.PROFILE_DESCRIPTION, builtString);
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Set `"+targetUser.getName()+"`'s title to `"+showString+"`!"));
                return;
            }

            // Developer only, setting their own title
            if (args.size() >= 1 && args.get(0).equalsIgnoreCase("title") && PermissionHandler.handle.userIsDeveloper(e.getAuthor()) && e.getMessage().getMentions().isEmpty())
            {
                // Remove the `title`
                args.remove(0);

                // Build the new title
                String builtString = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                String showString = SMALL_CODE_BLOCK
                    .matcher(LARGE_CODE_BLOCK.matcher(builtString).replaceAll("<lcb>"))
                    .replaceAll("<scb>");

                // Makes sure the title isn't too long
                if (builtString.length() > 30)
                {
                    messageHandler.sendError("Your title is too long!");
                    return;
                }

                // Set and send
                player.setObject(DataType.PROFILE_TITLE, builtString);
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Set your title to `"+showString+"`!"));
                return;
            }

            // Developer only, setting someone else's title
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("title") && e.getMessage().getMentions().size() == 1 && PermissionHandler.handle.userIsDeveloper(e.getAuthor()))
            {
                if (!args.get(1).contains(e.getMessage().getMentions().get(0).getStringID()))
                {
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                    return;
                }

                // Remove the `title`
                args.remove(0);

                // Removes the pinged user
                args.remove(0);

                // Build the new title
                String builtString = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                String showString = SMALL_CODE_BLOCK
                    .matcher(LARGE_CODE_BLOCK.matcher(builtString).replaceAll("<lcb>"))
                    .replaceAll("<scb>");

                // Makes sure the title isn't too long
                if (builtString.length() > 30)
                {
                    messageHandler.sendError("Your title is too long!");
                    return;
                }

                // Get the other player's IUser instance
                IUser otherIUser = e.getMessage().getMentions().get(0);

                // Get the other user's player instance
                Player otherUser = PlayerHandler.handle.getPlayer(otherIUser.getLongID());

                // Set and send
                otherUser.setObject(DataType.PROFILE_TITLE, builtString);
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Set `"+otherIUser.getName()+"`'s title to `"+showString+"`!"));
                return;
            }

            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
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
