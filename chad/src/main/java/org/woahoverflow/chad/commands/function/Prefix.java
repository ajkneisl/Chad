package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class Prefix implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // If there's no arguments, show the prefix
            if (args.isEmpty())
            {
                // Sets up embed builder with the prefix in it
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withTitle("Prefix");
                embedBuilder.withDesc(Chad.getGuild(e.getGuild()).getDocument().getString("prefix"));

                // Sends
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            // If the arguments are 2, set the prefix
            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
            {
                // Gets the current prefix
                String prefix = Chad.getGuild(e.getGuild()).getDocument().getString("prefix");

                // Makes sure the prefix isn't over 6 characters long
                if (args.get(1).length() > 6)
                {
                    messageHandler.sendError("Prefix can't be over 6 characters long!");
                    return;
                }

                // Sends the log
                MessageHandler.sendConfigLog("Prefix", args.get(1), prefix, e.getAuthor(), e.getGuild());

                // Sets the prefix in the database & recaches the guild
                DatabaseHandler.handle.set(e.getGuild(), "prefix", args.get(1));
                Chad.getGuild(e.getGuild()).cache();

                // Sends a the message
                messageHandler.send("Your prefix is now " + args.get(1), "Changed Prefix");
                return;
            }

            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("prefix", "Your prefix.");
        st.put("prefix set <string>", "Sets the prefix.");
        return Command.helpCommand(st, "Prefix", e);
    }
}
