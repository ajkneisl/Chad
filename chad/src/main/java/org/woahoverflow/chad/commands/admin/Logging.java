package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Add logging for specific triggers
 *
 * @author sho
 */
public class Logging implements Command.Class  {
    @Override
    public final Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            Guild guild = GuildHandler.handle.getGuild(e.getGuild().getLongID());
            String prefix = ((String) guild.getObject(DataType.PREFIX));

            // Checks if there are any arguments
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "logging **set** **on/off**");
                return;
            }

            // Disables or Enables logging in the guild
            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set")) {
                if (args.get(1).equalsIgnoreCase("off") || args.get(1).equalsIgnoreCase("on")) {
                    // Sets the on or off
                    String bool = args.get(1).equalsIgnoreCase("on") ? "off" : "on";

                    // Sets an actual boolean value
                    boolean actualBoolean = bool.equalsIgnoreCase("off");

                    // Sets in the database
                    GuildHandler.handle.getGuild(e.getGuild().getLongID()).setObject(DataType.LOGGING, actualBoolean);

                    // Sends a log
                    MessageHandler.sendConfigLog("Logging", bool, Boolean.toString((boolean) guild.getObject(Guild.DataType.LOGGING)), e.getAuthor(), e.getGuild());

                    // Sends the message
                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("Logging has been turned `"+bool+"`."));

                    // recaches
                    GuildHandler.handle.refreshGuild(e.getGuild().getLongID());
                    return;
                }
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "logging **set** **on/off**");
                return;
            }

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("setchannel")) {
                // Isolates the channel name
                args.remove(0);

                // Builds the channel name
                String formattedString = args.stream().map(s -> s + ' ').collect(Collectors.joining());

                // Makes sure the channel exists
                if (e.getGuild().getChannelsByName(formattedString.trim()).isEmpty()) {
                    messageHandler.sendError("Invalid Channel");
                    return;
                }

                IChannel channel = e.getGuild().getChannelsByName(formattedString.trim()).get(0);

                // Makes sure it's not null
                if (channel == null) {
                    messageHandler.sendError("Invalid Channel");
                    return;
                }

                // Gets the current logging channel and makes sure it isn't null
                String loggingChannel = (String) guild.getObject(Guild.DataType.LOGGING_CHANNEL);
                if (loggingChannel == null) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION);
                    return;
                }

                // Sends the log
                if (loggingChannel.equalsIgnoreCase("none"))
                    MessageHandler.sendConfigLog("Logging Channel", formattedString.trim(), "none", e.getAuthor(), e.getGuild());
                else
                    MessageHandler.sendConfigLog("Logging Channel", formattedString.trim(), e.getGuild().getChannelByID(Long.parseLong(loggingChannel)).getName(), e.getAuthor(), e.getGuild());

                // Send Message
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Logging channel has been changed to `" + channel.getName() + "`."));

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
    public final Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("logging set <on/off>", "Toggles the logging functionality.");
        st.put("logging setchannel <channel name>", "Sets the logging channel.");
        return Command.helpCommand(st, "Logging", e);
    }
}
