package org.woahoverflow.chad.commands.function;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;

public class Message implements Command.Class  {

    private static final Pattern LARGE_CODE_BLOCK = Pattern.compile("```");
    private static final Pattern SMALL_CODE_BLOCK = Pattern.compile("`");

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Makes sure there's arguments
            if (args.isEmpty())
            {
                messageHandler.sendError("Invalid Arguments!");
                return;
            }

            // Sets the join message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("join"))
            {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current join message
                String old = Chad.getGuild(e.getGuild()).getDocument().getString("join_message");

                // Sets the new one into the database
                DatabaseHandler.handle.set(e.getGuild(), "join_message", formattedText.trim());

                // Sends the confirmation message
                messageHandler.sendMessage("Set the guild's join message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.sendConfigLog("Join Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());

                // ReCaches the guild
                Chad.getGuild(e.getGuild()).cache();
                return;
            }

            // Sets the leave message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("leave"))
            {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current leave message
                String old = Chad.getGuild(e.getGuild()).getDocument().getString("leave_message");

                // Sets the new one into the database
                DatabaseHandler.handle.set(e.getGuild(), "leave_message", formattedText.trim());

                // Sends the confirmation message
                messageHandler.sendMessage("Set the guild's leave message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.sendConfigLog("Leave Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());

                // ReCaches the guild
                Chad.getGuild(e.getGuild()).cache();
                return;
            }

            // Sets the ban message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("ban"))
            {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current ban message
                String old = Chad.getGuild(e.getGuild()).getDocument().getString("ban_message");

                // Sets the new one into the database
                DatabaseHandler.handle.set(e.getGuild(), "ban_message", formattedText.trim());

                // Sends the confirmation message
                messageHandler.sendMessage("Set the guild's ban message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.sendConfigLog("Ban Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());

                // ReCaches the guild
                Chad.getGuild(e.getGuild()).cache();
                return;
            }

            // Sets the kick message for the guild
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("kick"))
            {
                // Isolates the text
                args.remove(0);

                // Gets the formatted string
                String formattedText = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Gets the current ban message
                String old = Chad.getGuild(e.getGuild()).getDocument().getString("kick_message");

                // Sets the new one into the database
                DatabaseHandler.handle.set(e.getGuild(), "kick_message", formattedText.trim());

                messageHandler.sendMessage("Set the guild's kick message to `"+ SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(formattedText.trim()).replaceAll("<large code-block>")).replaceAll("<small code-block>") + '`');

                // Sends the log
                MessageHandler.sendConfigLog("Kick Message", formattedText.trim(), old, e.getAuthor(), e.getGuild());

                // ReCaches the guild
                Chad.getGuild(e.getGuild()).cache();
                return;
            }

            // Disables or enables the different messages
            if (args.size() == 3 && args.get(0).equalsIgnoreCase("toggle"))
            {
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
                if (args.get(1).equalsIgnoreCase("join"))
                {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's join message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(DatabaseHandler.handle.getBoolean(e.getGuild(),"join_msg_on")), e.getAuthor(), e.getGuild());

                    // Sets it in the database
                    DatabaseHandler.handle.set(e.getGuild(), "join_msg_on", set);

                    // Recaches the guild
                    Chad.getGuild(e.getGuild()).cache();
                    return;
                }

                // If they're toggling ban
                if (args.get(1).equalsIgnoreCase("ban"))
                {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's ban message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(DatabaseHandler.handle
                        .getBoolean(e.getGuild(),"ban_msg_on")), e.getAuthor(), e.getGuild());

                    // Sets it in the database
                    DatabaseHandler.handle.set(e.getGuild(), "ban_msg_on", set);

                    // ReCaches the guild
                    Chad.getGuild(e.getGuild()).cache();
                    return;
                }

                // If they're toggling kick
                if (args.get(1).equalsIgnoreCase("kick"))
                {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's kick message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(DatabaseHandler.handle
                        .getBoolean(e.getGuild(),"kick_msg_on")), e.getAuthor(), e.getGuild());

                    // Sets in the database
                    DatabaseHandler.handle.set(e.getGuild(), "kick_msg_on", set);

                    // ReCaches the guild
                    Chad.getGuild(e.getGuild()).cache();
                    return;
                }

                // If they're toggling leave
                if (args.get(1).equalsIgnoreCase("leave"))
                {
                    // Sends the message
                    messageHandler.sendMessage("Set the guild's leave message toggle to `"+set+ '`');

                    // Sends the log
                    MessageHandler.sendConfigLog("Leave Message Toggle", Boolean.toString(set), Boolean.toString(DatabaseHandler.handle
                        .getBoolean(e.getGuild(),"leave_msg_on")), e.getAuthor(), e.getGuild());

                    // Sets in the database
                    DatabaseHandler.handle.set(e.getGuild(), "leave_msg_on", set);

                    // ReCaches in the guild
                    Chad.getGuild(e.getGuild()).cache();
                    return;
                }
                messageHandler.sendError("Invalid Type!");
            }

            if (args.size() >= 3 && args.get(0).equalsIgnoreCase("setchannel"))
            {
                if (args.get(1).equalsIgnoreCase("join"))
                {
                    // Get the channel string
                    String channelString = DatabaseHandler.handle
                        .getString(e.getGuild(), "join_message_ch");

                    // Makes sure the channel string isn't null
                    if (channelString == null)
                    {
                        messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                        return;
                    }

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
                    if (channelsWithName.isEmpty())
                    {
                        messageHandler.sendError("Invalid Channel!");
                        return;
                    }

                    // Gets the new channel
                    IChannel newChannel = channelsWithName.get(0);

                    // Sets the channel in the database
                    DatabaseHandler.handle.set(e.getGuild(), "join_message_ch", newChannel.getStringID());

                    // Sends the message
                    messageHandler.sendMessage("Set the guild's join channel to `"+newChannel.getName()+ '`');

                    // Sends the log
                    MessageHandler.sendConfigLog("Join Message Channel", newChannel.getName(), oldName, e.getAuthor(), e.getGuild());

                    // ReCaches the guild
                    Chad.getGuild(e.getGuild()).cache();
                    return;
                }

                if (args.get(1).equalsIgnoreCase("leave"))
                {
                    // Get the channel string
                    String channelString = DatabaseHandler.handle
                        .getString(e.getGuild(), "leave_message_ch");

                    // Makes sure the channel string isn't null
                    if (channelString == null)
                    {
                        messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                        return;
                    }

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
                    if (channelsWithName.isEmpty())
                    {
                        messageHandler.sendError("Invalid Channel!");
                        return;
                    }

                    // Gets the new channel
                    IChannel newChannel = channelsWithName.get(0);

                    // Sets the channel in the database
                    DatabaseHandler.handle.set(e.getGuild(), "leave_message_ch", newChannel.getStringID());

                    // Sends the message
                    messageHandler.sendMessage("Set the guild's leave channel to `"+newChannel.getName()+ '`');

                    // Sends the log
                    MessageHandler.sendConfigLog("Leave Message Channel", newChannel.getName(), oldName, e.getAuthor(), e.getGuild());

                    // ReCaches the guild
                    Chad.getGuild(e.getGuild()).cache();
                    return;
                }
                messageHandler.sendError("Invalid Type!");
                return;
            }
            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("im join <message>", "Sets the join message.");
        st.put("im leave <message>", "Sets the leave message.");
        st.put("im ban <message>", "Sets the ban message.");
        st.put("im kick <message>", "Sets the kick message.");
        st.put("im toggle <join/leave/ban/kick> <true/false>", "Toggles the different message types.");
        st.put("im setchannel <join/leave> <channel name>", "Toggles the join/leave messages.");
        st.put("Variables", "&guild&, &user&, &reason& (punishment)");
        return Command.helpCommand(st, "Message", e);
    }
}
