package org.woahoverflow.chad.commands.gambling;

import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class Balance implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (!DatabaseHandler.handle.contains(e.getGuild(), e.getAuthor().getStringID() + "_balance"))
            {
                new MessageHandler(e.getChannel()).sendError("You don't have an account! \n Use `" + Chad
                    .getGuild(e.getGuild()).getDocument().getString("prefix") + "register` to get one!");
                return;
            }

            new MessageHandler(e.getChannel()).send("Your balance is `"+DatabaseHandler.handle
                .get(e.getGuild(), e.getAuthor().getStringID()+"_balance")+"`.", "Balance");
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("balance", "See your balance.");
        return Command.helpCommand(st, "Balance", e);
    }
}
