package org.woahoverflow.chad.commands.function;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class Swearing implements Command.Class {

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
                String status = Chad.getGuild(e.getGuild().getLongID()).getDocument().getBoolean("stop_swear") ? "enabled" :  "disabled";
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
                DatabaseHandler.handle.set(e.getGuild(), "stop_swear", toggle);
                // recaches
                Chad.getGuild(e.getGuild().getLongID()).cache();
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
