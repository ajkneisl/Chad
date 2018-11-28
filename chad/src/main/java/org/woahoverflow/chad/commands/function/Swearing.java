package org.woahoverflow.chad.commands.function;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.CachingHandler;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.Command.Class;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class Swearing implements Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // if there's no arguments, give statistics
            if (args.isEmpty())
            {
                // creates an embed builder and applies values
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withTitle("Swear Filter");
                String status = CachingHandler.getGuild(e.getGuild()).getDoc().getBoolean("stop_swear") ? "enabled" :  "disabled";
                embedBuilder.withDesc("Swearing in this guild is `"+status+"`.");
                // send
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            if (args.size() == 1 && args.get(0).equalsIgnoreCase("on") || args.get(0).equalsIgnoreCase("off"))
            {
                // actual boolean value
                boolean toggle = args.get(0).equalsIgnoreCase("on");
                // good looking value
                String toggleString = toggle ? "enabled" : "disabled";
                // sets in database
                ChadVar.databaseDevice.set(e.getGuild(), "stop_swear", toggle);
                // recaches
                ChadVar.cacheDevice.cacheGuild(e.getGuild());
                // sends message
                messageHandler.send("Swear filtering has been `"+toggleString+ '`', "Swear Filter");
                return;
            }

            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("swearfilter <on/off>", "Toggles the swear filter.");
        st.put("swearfilter", "Gets the status of the swear filter.");
        return Command.helpCommand(st, "Swear Filter", e);
    }
}
