package org.woahoverflow.chad.commands.gambling;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class Balance implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (!ChadVar.DATABASE_DEVICE.contains(e.getGuild(), e.getAuthor().getStringID() + "_balance"))
            {
                new MessageHandler(e.getChannel()).sendError("You don't have an account! \n Use `" + ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc().getString("prefix") + "register` to get one!");
                return;
            }

            new MessageHandler(e.getChannel()).send("Your balance is `"+ChadVar.DATABASE_DEVICE.get(e.getGuild(), e.getAuthor().getStringID()+"_balance")+"`.", "Balance");
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("balance", "See your balance.");
        return Command.helpCommand(st, "Balance", e);
    }
}
