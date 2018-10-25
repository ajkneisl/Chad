package com.jhobot.commands.admin;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Category;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import com.jhobot.handle.commands.PermissionLevels;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.HashMap;
import java.util.List;

public class ModifyPresence implements Command {

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

    @Override
    public PermissionLevels level() {
        return PermissionLevels.SYSTEM_ADMINISTRATOR;
    }

    @Override
    public Category category() {
        return Category.ADMIN;
    }
}
