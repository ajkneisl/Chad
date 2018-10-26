package com.jhobot.commands.admin;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.HashMap;
import java.util.List;

public class ModifyPresence implements Command {

    @DefineCommand(category = Category.ADMIN, devOnly = true)
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            StringBuilder sb = new StringBuilder();
            for (String str : args) {
                sb.append(str + " ");
            }
            ChadBot.cli.changePresence(StatusType.ONLINE, ActivityType.PLAYING, sb.toString().trim());
            new MessageHandler(e.getChannel()).sendMessage("Changed presence to \"" + sb.toString().trim() + "\"");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("modpresence <string>", "Changes the bots rich presence message.");
        return HelpHandler.helpCommand(st, "Modify Presence", e);
    }
}
