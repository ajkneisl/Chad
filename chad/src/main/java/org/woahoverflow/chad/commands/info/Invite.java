package org.woahoverflow.chad.commands.info;


import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Invite implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> new MessageHandler(e.getChannel()).send("https://discord.gg/bMH5V7h","Invite");
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("invite", "Gets an invite");
        return HelpHandler.helpCommand(st, "invite", e);
    }
}