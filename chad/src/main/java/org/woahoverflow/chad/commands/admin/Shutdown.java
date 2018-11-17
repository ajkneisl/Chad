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
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            new MessageHandler(e.getChannel()).send("Shutting down in 10 seconds...", "Warning");
            ChadVar.UI_HANDLER.addLog("Shutting down in 10 seconds...", UIHandler.LogLevel.SEVERE);
            ChadBot.cli.changePresence(StatusType.DND, ActivityType.PLAYING, "Shutting down...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            ChadBot.cli.logout(); // logout
            System.exit(0); // initiate a system exit with status code 0
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("shutdown", "Shuts the bot down.");
        return Command.helpCommand(hash, "Shutdown", e);
    }
}
