package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.HashMap;
import java.util.List;

public class Shutdown implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            // Warns that the bot is shutting down
            new MessageHandler(e.getChannel()).send("Shutting down in 10 seconds...", "Warning");

            // Warns within the UI
            ChadVar.UI_DEVICE.addLog("Shutting down in 10 seconds...", UIHandler.LogLevel.SEVERE);

            // Updates the presence
            ChadBot.cli.changePresence(StatusType.DND, ActivityType.PLAYING, "Shutting down...");

            // Waits 10 seconds
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            // Exits
            ChadBot.cli.logout();
            System.exit(0);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("shutdown", "Shuts the bot down.");
        return Command.helpCommand(hash, "Shutdown", e);
    }
}
