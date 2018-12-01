package org.woahoverflow.chad.commands.info;

import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Command.Category;
import org.woahoverflow.chad.framework.Command.Data;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class Help implements Command.Class {

    private static final Pattern REGEX = Pattern.compile(", $");

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            StringBuilder stringBuilder = new StringBuilder();
            // Go through each commandCategory and add all it's commands to the help string
            for (Category category : Category.values()) {
                // If the commandCategory is Nsfw and the channel isn't Nsfw, don't show.
                if (category == Category.NSFW && !e.getChannel().isNSFW())
                    continue;
                // If the commandCategory is Admin and the user isn't an Admin, don't show.
                if (category == Category.ADMIN && !PermissionHandler.handle.userIsDeveloper(e.getAuthor()))
                    continue;

                // Append the commandCategory.
                stringBuilder.append('\n').append(Util.fixEnumString(category.toString().toLowerCase())).append(": ");
                StringBuilder commandsBuilder = new StringBuilder();
                for (Entry<String, Data> stringDataEntry : ChadVar.COMMANDS.entrySet())
                {
                    // Gets the command's data
                    Data meta = stringDataEntry.getValue();

                    // Makes sure the command is in the right area
                    if (meta.getCommandCategory() != category)
                        continue;

                    // Adds the command to the builder
                    String str = '`' + stringDataEntry.getKey() + "`, ";
                    commandsBuilder.append(str);
                }
                // Replaces the end
                stringBuilder.append(REGEX.matcher(commandsBuilder.toString()).replaceAll(""));
            }

            // Sends the message
            new MessageHandler(e.getChannel()).send(stringBuilder.toString(), "Help");
       };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("help", "Displays all commands Chad has to offer.");
        return Command.helpCommand(st, "Help", e);
    }
}
