package org.woahoverflow.chad.commands.info;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Category;
import org.woahoverflow.chad.framework.obj.Command.Data;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import org.woahoverflow.chad.framework.util.Util;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * All Chad commands
 *
 * @author sho, codebasepw
 */
public class Help implements Command.Class {

    private static final Pattern REGEX = Pattern.compile(", $");

    @Override
    public final Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            StringBuilder stringBuilder = new StringBuilder();
            // Go through each category and add all it's commands to the help string
            for (Category category : Command.Category.values()) {
                // If the category is Nsfw and the channel isn't Nsfw, don't show.
                if (category == Command.Category.NSFW && !e.getChannel().isNSFW())
                    continue;
                // If the category is Admin and the user isn't an Admin, don't show.
                if (category == Command.Category.DEVELOPER && !PermissionHandler.userIsDeveloper(e.getAuthor()))
                    continue;
                // If the category is disabled
                if (((ArrayList<String>) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(
                    DataType.DISABLED_CATEGORIES)).contains(category.toString().toLowerCase()))
                    continue;

                // The commands builder
                StringBuilder commandsBuilder = new StringBuilder();
                for (Entry<String, Data> stringDataEntry : ChadVar.getCOMMANDS().entrySet()) {
                    // Gets the command's data
                    Data meta = stringDataEntry.getValue();

                    // Makes sure the command is in the right area
                    if (meta.getCommandCategory() != category)
                        continue;

                    // Makes sure the user has permission
                    if (PermissionHandler.userNoPermission(stringDataEntry.getKey(), e.getAuthor(), e.getGuild()))
                        continue;

                    // Adds the command to the builder
                    String str = '`' + stringDataEntry.getKey() + "`, ";
                    commandsBuilder.append(str);
                }

                // Replaces the end and makes sure there's content
                if (commandsBuilder.length() != 0) {
                    stringBuilder.append("\n\n").append("**").append(Util.fixEnumString(category.toString().toLowerCase())).append("**").append(": \n").append(REGEX.matcher(commandsBuilder.toString()).replaceAll(""));
                }
            }

            // Adds a warning that you can only see the commands you have permission to
            stringBuilder.append("\n\nYou can only see the commands you have permission to!");

            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.withDesc("**Support Discord**: https://woahoverflow.org/discord\n**Invite Chad**: https://woahoverflow.org/chad/invite" + stringBuilder.toString());
            embedBuilder.withTitle("Chad's Commands");

            // Sends the message
            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(embedBuilder);
       };
    }

    @Override
    public final Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("help", "Displays all commands Chad has to offer.");
        return Command.helpCommand(st, "Help", e);
    }
}
