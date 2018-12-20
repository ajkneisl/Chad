package org.woahoverflow.chad.commands.function;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.7.0
 */
public class GuildSettings implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Guild guild = GuildHandler.handle.getGuild(e.getGuild().getLongID());
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            if (args.isEmpty())
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            switch (args.get(0).toLowerCase())
            {
                // Clears the statistics for the guild
                case "clearstats":
                    guild.clearStatistics();

                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("Cleared guild's statistics!"));

                    return;
                case "community":
                    // Arguments : cmd community <off/on>
                    if (args.size() != 2)
                    {
                        messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                        return;
                    }

                    // Makes sure the user inputted on or off
                    if (!(args.get(1).equalsIgnoreCase("off") || args.get(1).equalsIgnoreCase("on")))
                    {
                        messageHandler.sendError("Please use **on** or **off**!");
                        return;
                    }

                    // Turns the on/off to a boolean
                    boolean bool = !args.get(1).equalsIgnoreCase("off");

                    // Sets within the database
                    guild.setObject(DataType.ALLOW_COMMUNITY_FEATURES, bool);

                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("Community features have been turned `"+args.get(1).toLowerCase()+"`!"));

                    return;
                case "stats":
                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("There's `" + guild.getObject(
                        DataType.MESSAGES_SENT) + "` messages sent.\nThere's `" + guild.getObject(DataType.COMMANDS_SENT) + "` commands sent."));
                    return;

                default:
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("guildsettings clearstats", "Clears your guild's statistics.");
        st.put("guildsettings stats", "Gets your guild's statistics.");
        st.put("guildsettings community <on/off>", "Enables or disables community features in your guild. (unrecommended)");
        return Command.helpCommand(st, "Help", e);
    }
}
