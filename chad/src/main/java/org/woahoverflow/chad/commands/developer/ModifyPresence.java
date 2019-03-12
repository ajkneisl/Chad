package org.woahoverflow.chad.commands.developer;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.core.ChadInstance;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modify Chad's discord presence
 *
 * @author sho, codebasepw
 */
public class ModifyPresence implements Command.Class {
    @Override
    public final Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return() -> {
            String prefix = (String) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            // Checks if there's no arguments
            if (args.isEmpty()) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix +
                        "modpresence **new presence**");
                return;
            }

            // The switch statement changes the message
            String message;
            switch (args.get(0)) {
                default:
                    // Builds the message
                    String formattedMessage = args.stream().map(str -> str + ' ').collect(Collectors.joining());

                    // Changes the presence
                    ChadInstance.cli.changePresence(ChadVar.statusType, ActivityType.PLAYING, formattedMessage.trim());

                    // Updates the message
                    message = "Changed presence to `" + formattedMessage.trim() + "`.";

                    // Updates the ChadVar variable
                    ChadVar.currentStatus = formattedMessage.trim();

                    break;
                case "rotate":
                    // Enables presence rotation
                    ChadVar.rotatePresence = true;

                    // Updates the message
                    message = "Enabled presence rotation.";
                    break;
                case "static":
                    // Disables presence rotation
                    ChadVar.rotatePresence = false;

                    // Updates the message
                    message = "Disabled presence rotation.";
                    break;
                case "add":
                    // Removes the option argument
                    args.remove(0);

                    // Builds the presence string
                    String string = args.stream().map(str -> str + ' ').collect(Collectors.joining());

                    // Adds the message into the rotation pool.
                    ChadVar.presenceRotation.add(string.trim());

                    // Updates the message
                    message = "Added `" + string.trim() + "` to rotation";
                    break;
                case "view":
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String s : ChadVar.presenceRotation) {
                        stringBuilder.append('`').append(s).append("`, ");
                    }

                    message = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 2);
                    break;
                case "status":
                    // Removes the option argument
                    args.remove(0);

                    if (args.get(0).equalsIgnoreCase("idle")) {
                        // Updates the ChadVar to IDLE
                        ChadVar.statusType = StatusType.IDLE;

                        // Changes the presence
                        e.getClient().changePresence(StatusType.IDLE, ActivityType.PLAYING, ChadVar.currentStatus);

                        // Updates the message
                        message = "Changed status type to `Idle`";
                        break;
                    }

                    if (args.get(0).equalsIgnoreCase("online")) {
                        // Updates the ChadVar to Online
                        ChadVar.statusType = StatusType.ONLINE;

                        // Changes the presence
                        e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, ChadVar.currentStatus);

                        // Updates the message
                        message = "Changed status type to `Online`";
                        break;
                    }

                    if (args.get(0).equalsIgnoreCase("offline")) {
                        // Updates the ChadVar to Offline
                        ChadVar.statusType = StatusType.OFFLINE;

                        // Changes the presence
                        e.getClient().changePresence(StatusType.INVISIBLE, ActivityType.PLAYING, ChadVar.currentStatus);

                        // Updates the message
                        message = "Changed status type to `Offline`";
                        break;
                    }

                    if (args.get(0).equalsIgnoreCase("dnd")) {
                        // Updates the ChadVar to Do Not Disturb
                        ChadVar.statusType = StatusType.DND;

                        // Changes the presence
                        e.getClient().changePresence(StatusType.DND, ActivityType.PLAYING, ChadVar.currentStatus);

                        // Updates the message
                        message = "Changed status type to `Do Not Disturb`";
                        break;
                    }
                    new MessageHandler(e.getChannel(), e.getAuthor()).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "modpresence status **dnd/offline/online/idle**");
                    return;
            }

            // Sends the message
            new MessageHandler(e.getChannel(), e.getAuthor()).sendMessage(message);
        };
    }

    @Override
    public final Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("modpresence <string>", "Changes the bots rich presence message.");
        st.put("modpresence status <dnd/offline/online/idle>", "Changes the bots status.");
        st.put("modpresence <static/rotate>", "Disables or enables the bot's presence rotation.");
        return Command.helpCommand(st, "Modify Presence", e);
    }
}
