package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.HashMap;
import java.util.List;

public class ModifyPresence implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            String option = args.get(0);
            String message;
            switch (option) {
                default:
                    StringBuilder sb = new StringBuilder();
                    for (String str : args) {
                        sb.append(str).append(" ");
                    }
                    ChadBot.cli.changePresence(StatusType.ONLINE, ActivityType.PLAYING, sb.toString().trim());
                    message = "Changed presence to \"" + sb.toString().trim() + "\"";
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
                        e.getClient().changePresence(StatusType.IDLE);
                        message = "Changed status type to `Idle`";
                        break;
                    }
                    else if (args.get(0).equalsIgnoreCase("online"))
                    {
                        e.getClient().changePresence(StatusType.ONLINE);
                        message = "Changed status type to `Online`";
                        break;
                    }
                    else if (args.get(0).equalsIgnoreCase("offline"))
                    {
                        e.getClient().changePresence(StatusType.INVISIBLE);
                        message = "Changed status type to `Offline`";
                        break;
                    }
                    else if (args.get(0).equalsIgnoreCase("dontdisturb"))
                    {
                        e.getClient().changePresence(StatusType.DND);
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
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("modpresence <string>", "Changes the bots rich presence message.");
        return HelpHandler.helpCommand(st, "Modify Presence", e);
    }
}
