package com.jhobot.commands.info;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.CommandData;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.*;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;

public class Help implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            StringBuilder sb = new StringBuilder();
            // go through each category and add all its commands to the help string
            for (Category category : Category.values()) {
                if (category == Category.ADMIN && !ChadVar.PERMISSION_HANDLER.userIsDeveloper(e.getAuthor())) // no admin commands (unless admin)
                    continue;
                sb.append("\n").append(Util.fixEnumString(category.toString().toLowerCase())).append(": ");
                StringBuilder scuffed_builder = new StringBuilder();
                for (String k : ChadVar.COMMANDS.keySet())
                {
                    CommandData meta = ChadVar.COMMANDS.get(k);
                    if (meta.category == Category.ADMIN && !ChadVar.PERMISSION_HANDLER.userIsDeveloper(e.getAuthor())) // seriously, no admin commands (unless admin)
                        continue;
                    if (meta.category != category)
                        continue;
                    String str = "`" + k + "`, ";
                    scuffed_builder.append(str);
                }
                sb.append(scuffed_builder.toString().replaceAll(", $", ""));
            }
            new MessageHandler(e.getChannel()).send(sb.toString(), "Help");
       };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("help", "Displays all commands Chad has to offer.");
        return HelpHandler.helpCommand(st, "Help", e);
    }
}
