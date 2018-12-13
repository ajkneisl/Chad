package org.woahoverflow.chad.commands.function;

import java.util.stream.Collectors;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.HashMap;
import java.util.List;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class Logging implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            Guild guild = GuildHandler.handle.getGuild(e.getGuild().getLongID());

            // Checks if there are any arguments
            if (args.isEmpty())
            {
                messageHandler.sendError("Invalid Arguments!");
                return;
            }

            // Disables or Enables logging in the guild
            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
            {
                if (args.get(1).equalsIgnoreCase("off") || args.get(1).equalsIgnoreCase("on"))
                {
                    // Sets the on or off
                    String bool = args.get(1).equalsIgnoreCase("on") ? "off" : "on";

                    // Sets an actual boolean value
                    boolean actualBoolean = bool.equalsIgnoreCase("off");

                    // Sets in the database
                    GuildHandler.handle.getGuild(e.getGuild().getLongID()).setObject(DataType.LOGGING, actualBoolean);

                    // Sends a log
                    MessageHandler.sendConfigLog("Logging", bool, Boolean.toString((boolean) guild.getObject(Guild.DataType.LOGGING)), e.getAuthor(), e.getGuild());

                    // Sends the message
                    messageHandler.send("Changed logging to " + bool, "Changed Logging");

                    // recaches
                    GuildHandler.handle.refreshGuild(e.getGuild().getLongID());
                    return;
                }
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("setchannel"))
            {
                // Isolates the channel name
                args.remove(0);

                // Builds the channel name
                String formattedString = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Makes sure the channel exists
                if (e.getGuild().getChannelsByName(formattedString.trim()).isEmpty())
                {
                    new MessageHandler(e.getChannel(), e.getAuthor()).sendError("Invalid Channel");
                    return;
                }

                IChannel channel = e.getGuild().getChannelsByName(formattedString.trim()).get(0);

                // Makes sure it's not null
                if (channel == null)
                {
                    messageHandler.sendError("Invalid Channel");
                    return;
                }

                // Gets the current logging channel and makes sure it isn't null
                String loggingChannel = (String) guild.getObject(Guild.DataType.LOGGING_CHANNEL);
                if (loggingChannel == null)
                {
                    messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                    return;
                }

                // Sends the log
                if (loggingChannel.equalsIgnoreCase("none"))
                    MessageHandler.sendConfigLog("Logging Channel", formattedString.trim(), "none", e.getAuthor(), e.getGuild());
                else
                    MessageHandler.sendConfigLog("Logging Channel", formattedString.trim(), e.getGuild().getChannelByID(Long.parseLong(loggingChannel)).getName(), e.getAuthor(), e.getGuild());

                // Send Message
                messageHandler.send("Changed logging channel to " + formattedString.trim(), "Changed Logging Channel");

                // Sets in the database
                GuildHandler.handle.getGuild(e.getGuild().getLongID()).setObject(DataType.LOGGING, loggingChannel);

                // Recaches
                GuildHandler.handle.refreshGuild(e.getGuild().getLongID());
                return;
            }

            messageHandler.sendError("Invalid Arguments");
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("logging set <on/off>", "Toggles the logging functionality.");
        st.put("logging setchannel <channel name>", "Sets the logging channel.");
        return Command.helpCommand(st, "Logging", e);
    }
}
