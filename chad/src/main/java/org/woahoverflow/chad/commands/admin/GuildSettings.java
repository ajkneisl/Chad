package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Category;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Modify the guild's settings
 *
 * @author sho
 */
public class GuildSettings implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Guild guild = GuildHandler.handle.getGuild(e.getGuild().getLongID());
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            String prefix = ((String) guild.getObject(DataType.PREFIX));

            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "guildsettings **category/clearstats/stats**");
                return;
            }

            switch (args.get(0).toLowerCase()) {
                // Clears the statistics for the guild
                case "clearstats":
                    guild.clearStatistics();

                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("Cleared guild's statistics!"));

                    return;
                case "category":
                    // Arguments : cmd category <category> <off/on>
                    if (args.size() != 3)
                    {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "guildsettings category **category name** **on/off**");
                        return;
                    }

                    // Makes sure the user inputted on or off
                    if (!(args.get(2).equalsIgnoreCase("off") || args.get(2).equalsIgnoreCase("on")))
                    {
                        messageHandler.sendError("Please use **on** or **off**!");
                        return;
                    }

                    Category category = null;

                    // Makes sure the category suggested is an actual category
                    for (Command.Category ct : Command.Category.values()) {
                        if (args.get(1).equalsIgnoreCase(ct.toString())) {
                            category = ct;
                        }
                    }

                    // Makes sure a category was actually found
                    if (category == null)
                    {
                        messageHandler.sendError("Invalid Category!");
                        return;
                    }

                    // Turns the on/off to a boolean
                    boolean bool = !args.get(2).equalsIgnoreCase("off");

                    if (bool && !((ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES)).contains(category.toString().toLowerCase())) {
                        messageHandler.sendError("That category isn't disabled!");
                        return;
                    }

                    if (!bool && ((ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES)).contains(category.toString().toLowerCase())) {
                        messageHandler.sendError("That category is already disabled!");
                        return;
                    }

                    if (bool && ((ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES)).contains(category.toString().toLowerCase())) {
                        ArrayList<String> disabled = (ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES);

                        disabled.remove(category.toString().toLowerCase());

                        guild.setObject(DataType.DISABLED_CATEGORIES, disabled);

                        messageHandler.sendEmbed(new EmbedBuilder().withDesc("Enabled category `"  + category.toString().toLowerCase() + "`!"));
                        return;
                    }

                    if (!bool && !((ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES)).contains(category.toString().toLowerCase())) {
                        ArrayList<String> disabled = (ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES);

                        disabled.add(category.toString().toLowerCase());

                        guild.setObject(DataType.DISABLED_CATEGORIES, disabled);

                        messageHandler.sendEmbed(new EmbedBuilder().withDesc("Disabled category `"  + category.toString().toLowerCase() + "`!"));
                        return;
                    }
                    return;
                case "stats":
                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("There's `" + guild.getObject(
                        DataType.MESSAGES_SENT) + "` messages sent.\nThere's `" + guild.getObject(DataType.COMMANDS_SENT) + "` commands sent."));
                    return;

                default:
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "guildsettings **category/clearstats/stats**");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("guildsettings clearstats", "Clears your guild's statistics.");
        st.put("guildsettings stats", "Gets your guild's statistics.");
        st.put("guildsettings category <category name> <on/off>", "Enables or disables a category.");
        return Command.helpCommand(st, "Guild Settings", e);
    }
}
