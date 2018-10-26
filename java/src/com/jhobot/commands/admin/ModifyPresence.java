package com.jhobot.commands.admin;

import com.jhobot.commands.function.Message;
import com.jhobot.core.ChadBot;
import com.jhobot.core.Listener;
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
            String option = args.get(0);
            String message;
            switch (option) {
                default:
                    StringBuilder sb = new StringBuilder();
                    for (String str : args) {
                        sb.append(str + " ");
                    }
                    ChadBot.cli.changePresence(StatusType.ONLINE, ActivityType.PLAYING, sb.toString().trim());
                    message = "Changed presence to \"" + sb.toString().trim() + "\"";
                    break;
                case "rotate":
                    Listener.ROTATE_PRESENCE = true;
                    message = "Enabled presence rotation.";
                    break;
                case "static":
                    Listener.ROTATE_PRESENCE = false;
                    message = "Disabled presence rotation.";
                    break;
                case "add":
                    args.remove(0);
                    StringBuilder add_sb = new StringBuilder();
                    for (String str : args) {
                        add_sb.append(str + " ");
                    }
                    Listener.PRESENCE_ROTATION.add(add_sb.toString().trim());
                    message = "Added \"" + add_sb.toString().trim() + "\" to rotation";
                    break;
                case "time":
                    args.remove(0);
                    StringBuilder time_sb = new StringBuilder();
                    for (String str : args) {
                        time_sb.append(str + " ");
                    }
                    Listener.ROTATION_TIME = Integer.parseInt(time_sb.toString().trim());
                    message = "Changed rotation period to `" + Listener.ROTATION_TIME + "`";
                    break;
            }

            new MessageHandler(e.getChannel()).sendMessage(message);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("modpresence <string>", "Changes the bots rich presence message.");
        return HelpHandler.helpCommand(st, "Modify Presence", e);
    }
}
