package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.HashMap;
import java.util.List;

public class ModifyPresence implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            if (args.size() == 0)
            {
                new MessageHandler(e.getChannel()).sendError("Invalid Arguments");
                return;
            }
            String option = args.get(0);
            String message;
            switch (option) {
                default:
                    StringBuilder sb = new StringBuilder();
                    for (String str : args) {
                        sb.append(str).append(" ");
                    }
                    ChadBot.cli.changePresence(ChadVar.STATUS_TYPE, ActivityType.PLAYING, sb.toString().trim());
                    message = "Changed presence to \"" + sb.toString().trim() + "\"";
                    ChadVar.CURRENT_STATUS = sb.toString().trim();
                    break;
                case "rotate":
                    ChadVar.ROTATE_PRESENCE = true;
                    message = "Enabled presence rotation.";
                    break;
                case "static":
                    ChadVar.ROTATE_PRESENCE = false;
                    message = "Disabled presence rotation.";
                    break;
                case "add":
                    args.remove(0);
                    StringBuilder add_sb = new StringBuilder();
                    for (String str : args) {
                        add_sb.append(str).append(" ");
                    }
                    ChadVar.PRESENCE_ROTATION.add(add_sb.toString().trim());
                    message = "Added \"" + add_sb.toString().trim() + "\" to rotation";
                    break;
                case "time":
                    args.remove(0);
                    StringBuilder time_sb = new StringBuilder();
                    for (String str : args) {
                        time_sb.append(str).append(" ");
                    }
                    ChadVar.ROTATION_TIME = Integer.parseInt(time_sb.toString().trim());
                    message = "Changed rotation period to `" + ChadVar.ROTATION_TIME + "`";
                    break;
                case "status":
                    args.remove(0);
                    if (args.get(0).equalsIgnoreCase("idle"))
                    {
                        ChadVar.STATUS_TYPE = StatusType.IDLE;
                        e.getClient().changePresence(StatusType.IDLE, ActivityType.PLAYING, ChadVar.CURRENT_STATUS);
                        message = "Changed status type to `Idle`";
                        break;
                    }
                    else if (args.get(0).equalsIgnoreCase("online"))
                    {
                        ChadVar.STATUS_TYPE = StatusType.ONLINE;
                        e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, ChadVar.CURRENT_STATUS);
                        message = "Changed status type to `Online`";
                        break;
                    }
                    else if (args.get(0).equalsIgnoreCase("offline"))
                    {
                        ChadVar.STATUS_TYPE = StatusType.OFFLINE;
                        e.getClient().changePresence(StatusType.INVISIBLE, ActivityType.PLAYING, ChadVar.CURRENT_STATUS);
                        message = "Changed status type to `Offline`";
                        break;
                    }
                    else if (args.get(0).equalsIgnoreCase("dnd"))
                    {
                        ChadVar.STATUS_TYPE = StatusType.DND;
                        e.getClient().changePresence(StatusType.DND, ActivityType.PLAYING, ChadVar.CURRENT_STATUS);
                        message = "Changed status type to `Do Not Disturb`";
                        break;
                    }
                    else {
                        message = "Invalid";
                    }
            }

            new MessageHandler(e.getChannel()).sendMessage(message);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("modpresence <string>", "Changes the bots rich presence message.");
        st.put("modpresence status <status>", "Changes the bots status.");
        st.put("modpresence <static/rotate>", "Disables or enables the bot's presence rotation.");
        return Command.helpCommand(st, "Modify Presence", e);
    }
}
