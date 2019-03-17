package org.woahoverflow.chad.commands.admin;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Log things such as user joins or user leaves.
 *
 * @author sho
 */
public class Message implements Command.Class  {

    private static final Pattern LARGE_CODE_BLOCK = Pattern.compile("```");
    private static final Pattern SMALL_CODE_BLOCK = Pattern.compile("`");

    @Override
    public final Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            Guild guild = GuildHandler.getGuild(e.getGuild().getLongID());

            String prefix = ((String) guild.getObject(DataType.PREFIX));

            // Makes sure there's arguments
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "im help");
                return;
            }

            // Sets the join message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("join")) {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current join message
                String old = (String) guild.getObject(Guild.DataType.JOIN_MESSAGE);

                // Sets the new one into the database
                guild.setObject(DataType.JOIN_MESSAGE, formattedText.trim());

                // Sends the confirmation message
                messageHandler.sendMessage("Set the guild's join message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.Companion.sendConfigLog("Join Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());
                return;
            }

            // Sets the leave message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("leave")) {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current leave message
                String old = (String) guild.getObject(Guild.DataType.LEAVE_MESSAGE);

                // Sets the new one into the database
                guild.setObject(DataType.LEAVE_MESSAGE, formattedText.trim());

                // Sends the confirmation message
                messageHandler.sendMessage("Set the guild's leave message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.Companion.sendConfigLog("Leave Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());
                return;
            }

            // Sets the ban message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("ban")) {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current ban message
                String old = (String) guild.getObject(Guild.DataType.BAN_MESSAGE);

                // Sets the new one into the database
                guild.setObject(DataType.BAN_MESSAGE, formattedText.trim());

                // Sends the confirmation message
                messageHandler.sendMessage("Set the guild's ban message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.Companion.sendConfigLog("Ban Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());
                return;
            }

            // Sets the kick message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("kick")) {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current ban message
                String old = (String) guild.getObject(Guild.DataType.KICK_MESSAGE);

                // Sets the new one into the database
                guild.setObject(DataType.KICK_MESSAGE, formattedText.trim());

                messageHandler.sendMessage("Set the guild's kick message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.Companion.sendConfigLog("Kick Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());
                return;
            }

            // Disables or enables the different messages
            if (args.size() == 3 && args.get(0).equalsIgnoreCase("toggle")) {
                // Gets a boolean of the on or off
                boolean set;
                if (args.get(2).equalsIgnoreCase("on"))
                    set = true;
                else if (args.get(2).equalsIgnoreCase("off"))
                    set = false;
                else {
                    messageHandler.sendError("Please use on or off!");
                    return;
                }

                // If they're toggling join
                if (args.get(1).equalsIgnoreCase("join")) {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's join message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.Companion.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(
                            (boolean) guild.getObject(Guild.DataType.JOIN_MESSAGE_ON)), e.getAuthor(), e.getGuild());

                    // Sets it in the database
                    guild.setObject(DataType.JOIN_MESSAGE_ON, set);
                    return;
                }

                // If they're toggling ban
                if (args.get(1).equalsIgnoreCase("ban")) {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's ban message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.Companion.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(
                            (boolean) guild.getObject(Guild.DataType.BAN_MESSAGE)), e.getAuthor(), e.getGuild());

                    // Sets it in the database
                    guild.setObject(DataType.BAN_MESSAGE_ON, set);
                    return;
                }

                // If they're toggling kick
                if (args.get(1).equalsIgnoreCase("kick")) {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's kick message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.Companion.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(
                            (boolean) guild.getObject(Guild.DataType.KICK_MESSAGE)), e.getAuthor(), e.getGuild());

                    // Sets in the database
                    guild.setObject(DataType.KICK_MESSAGE_ON, set);
                    return;
                }

                // If they're toggling leave
                if (args.get(1).equalsIgnoreCase("leave")) {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's leave message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.Companion.sendConfigLog("Leave Message Toggle", Boolean.toString(set), Boolean.toString(
                            (boolean) guild.getObject(Guild.DataType.LEAVE_MESSAGE_ON)), e.getAuthor(), e.getGuild());

                    // Sets in the database
                    guild.setObject(DataType.LEAVE_MESSAGE_ON, set);
                    return;
                }
                messageHandler.sendError("Invalid Type!");
            }

            if (args.size() >= 3 && args.get(0).equalsIgnoreCase("setchannel")) {
                if (args.get(1).equalsIgnoreCase("join")) {
                    // Get the channel string
                    String channelString = (String) guild.getObject(Guild.DataType.JOIN_MESSAGE_CHANNEL);

                    // Makes sure the channel string isn't null

                    // Gets the old channel name
                    String oldName;
                    if (channelString.equalsIgnoreCase("none"))
                        oldName = "none";
                    else {
                        // Since it's declared, get it.
                        IChannel oldChannel = RequestBuffer.request(() -> e.getGuild().getChannelByID(Long.parseLong(channelString))).get();

                        // if it's deleted, set it to deleted, if not set it to the name
                        oldName = oldChannel.isDeleted() ? "Deleted Channel" : oldChannel.getName();
                    }

                    // Removes "setchannel" and "join" from it to isolate the channel name
                    args.remove(0);
                    args.remove(0);

                    // Builds the channel name
                    String channelName = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                    // Trims it
                    String newValue = channelName.trim();

                    // Gets the channels with the same name
                    List<IChannel> channelsWithName = RequestBuffer.request(() -> e.getGuild().getChannelsByName(newValue)).get();

                    // Sees if there's any channels with the same name
                    if (channelsWithName.isEmpty()) {
                        messageHandler.sendError("Invalid Channel!");
                        return;
                    }

                    // Gets the new channel
                    IChannel newChannel = channelsWithName.get(0);

                    // Sets the channel in the database
                    guild.setObject(DataType.JOIN_MESSAGE_CHANNEL, newChannel.getStringID());

                    // Sends the message
                    messageHandler.sendMessage("Set the guild's join channel to `"+newChannel.getName()+ '`');

                    // Sends the log
                    MessageHandler.Companion.sendConfigLog("Join Message Channel", newChannel.getName(), oldName, e.getAuthor(), e.getGuild());
                    return;
                }

                if (args.get(1).equalsIgnoreCase("leave")) {
                    // Get the channel string
                    String channelString = (String) guild.getObject(Guild.DataType.LEAVE_MESSAGE_CHANNEL);

                    // Makes sure the channel string isn't null

                    // Gets the old channel name
                    String oldName;
                    if (channelString.equalsIgnoreCase("none"))
                        oldName = "none";
                    else {
                        // Since it's declared, get it.
                        IChannel oldChannel = RequestBuffer.request(() -> e.getGuild().getChannelByID(Long.parseLong(channelString))).get();

                        // if it's deleted, set it to deleted, if not set it to the name
                        oldName = oldChannel.isDeleted() ? "Deleted Channel" : oldChannel.getName();
                    }

                    // Removes "setchannel" and "leave" from it to isolate the channel name
                    args.remove(0);
                    args.remove(0);

                    // Builds the channel name
                    String channelName = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                    // Trims it
                    String newValue = channelName.trim();

                    // Gets the channels with the same name
                    List<IChannel> channelsWithName = RequestBuffer.request(() -> e.getGuild().getChannelsByName(newValue)).get();

                    // Sees if there's any channels with the same name
                    if (channelsWithName.isEmpty()) {
                        messageHandler.sendError("Invalid Channel!");
                        return;
                    }

                    // Gets the new channel
                    IChannel newChannel = channelsWithName.get(0);

                    // Sets the channel in the database
                    guild.setObject(DataType.LEAVE_MESSAGE_CHANNEL, newChannel.getStringID());

                    // Sends the message
                    messageHandler.sendMessage("Set the guild's leave channel to `"+newChannel.getName()+ '`');

                    // Sends the log
                    MessageHandler.Companion.sendConfigLog("Leave Message Channel", newChannel.getName(), oldName, e.getAuthor(), e.getGuild());
                    return;
                }
                messageHandler.sendError("Invalid Type!");
                return;
            }
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "im help");
        };
    }

    @Override
    public final Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("im join <message]", "Sets the join message.");
        st.put("im leave <message]", "Sets the leave message.");
        st.put("im ban <message]", "Sets the ban message.");
        st.put("im kick <message]", "Sets the kick message.");
        st.put("im toggle <join/leave/ban/kick] <true/false]", "Toggles the different message types.");
        st.put("im setchannel <join/leave> <channel name]", "Toggles the join/leave messages.");
        st.put("Variables", "&guild&, &user&, &reason& (punishment)");
        return Command.helpCommand(st, "Interactive Message", e);
    }
}
