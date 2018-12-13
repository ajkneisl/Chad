package org.woahoverflow.chad.commands.gambling;

import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
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
        return () -> new MessageHandler(e.getChannel(), e.getAuthor()).send("Your balance is `"+
            PlayerHandler.handle.getPlayer(e.getAuthor().getLongID()).getObject(DataType.BALANCE)
            +"`.", "Balance");
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("balance", "See your balance.");
        return Command.helpCommand(st, "Balance", e);
    }
}
