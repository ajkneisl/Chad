package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class SetBalance implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Checks if the arguments is empty
            if (args.isEmpty())
            {
                messageHandler.sendError("Invalid Arguments!");
                return;
            }

            // If the arguments size is 0, set the value for the author
            if (args.size() == 1)
            {
                // Makes sure the argument is actually a long
                try {
                    Long.parseLong(args.get(0));
                } catch (NumberFormatException e1) {
                    messageHandler.sendError("Invalid Value!");
                    return;
                }

                // Sets the balance
                DatabaseHandler.handle.set(e.getGuild(), e.getAuthor().getStringID() + "_balance", Long.parseLong(args.get(0)));

                // Sends the message
                messageHandler.send("Set your balance to `"+args.get(0)+"`.", "Balance");
                return;
            }

            // If the arguments size is 2, set the value for another user
            if (args.size() == 2)
            {
                // Checks if anyone is mentioned
                if (e.getMessage().getMentions().isEmpty())
                {
                    messageHandler.sendError(MessageHandler.NO_MENTIONS);
                    return;
                }

                // Makes sure the argument is actually a long
                try {
                    Long.parseLong(args.get(0));
                } catch (NumberFormatException e1) {
                    new MessageHandler(e.getChannel()).sendError("Invalid Integer!");
                    return;
                }

                // Sets the balance of the mentioned user
                DatabaseHandler.handle
                    .set(e.getGuild(), e.getMessage().getMentions().get(0).getStringID() + "_balance", Long.parseLong(args.get(0)));

                // Sends the message
                messageHandler.send("Set `" + e.getMessage().getMentions().get(0).getName() + "`'s balance to `"+args.get(0)+"`.", "Balance");
                return;
            }
            messageHandler.sendError("Invalid Arguments!");
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("setbal <number> [@user]", "Registers your account with Chad.");
        return Command.helpCommand(st, "Register", e);
    }
}
