package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class SetBalance implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (args.size() == 0)
            {
                new MessageHandler(e.getChannel()).sendError("Invalid Arguments!");
                return;
            }
            if (args.size() == 1)
            {
                try {
                    Long.parseLong(args.get(0));
                } catch (NumberFormatException e1) {
                    new MessageHandler(e.getChannel()).sendError("Invalid Integer!");
                    return;
                }
                ChadVar.DATABASE_DEVICE.set(e.getGuild(), e.getAuthor().getStringID() + "_balance", Long.parseLong(args.get(0)));
                new MessageHandler(e.getChannel()).send("Set your balance to `"+args.get(0)+"`.", "Balance");
                return;
            }
            if (args.size() == 2)
            {
                if (e.getMessage().getMentions().size() == 0)
                {
                    new MessageHandler(e.getChannel()).sendError("You didn't mention anybody!");
                    return;
                }
                try {
                    Long.parseLong(args.get(1));
                } catch (NumberFormatException e1) {
                    new MessageHandler(e.getChannel()).sendError("Invalid Integer!");
                    return;
                }
                ChadVar.DATABASE_DEVICE.set(e.getGuild(), e.getMessage().getMentions().get(0).getStringID() + "_balance", Long.parseLong(args.get(0)));
                new MessageHandler(e.getChannel()).send("Set `" + e.getMessage().getMentions().get(0).getName() + "`'s balance to `"+args.get(0)+"`.", "Balance");
            }
            new MessageHandler(e.getChannel()).sendError("Invalid Arguments!");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("setbal <number> [@user]", "Registers your account with Chad.");
        return Command.helpCommand(st, "Register", e);
    }
}
