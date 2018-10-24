package com.jhobot.commands.admin;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import com.jhobot.handle.commands.PermissionLevels;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.List;

public class SetLevel implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            IUser user = e.getMessage().getMentions().get(0);
            JhoBot.PERMISSIONS_HANDLER.setPermission(user, PermissionLevels.valueOf(args.get(1)));
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("setlevel <user> <level>", "Sets the users permission level.");
        return HelpHandler.helpCommand(st, "Set Level", e);
    }

    @Override
    public PermissionLevels level() {
        return PermissionLevels.SYSTEM_ADMINISTRATOR;
    }
}
