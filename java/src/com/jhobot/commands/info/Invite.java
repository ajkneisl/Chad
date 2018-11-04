package com.jhobot.commands.info;


import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

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