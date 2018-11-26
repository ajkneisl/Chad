package org.woahoverflow.chad.commands.function;

import java.security.SecureRandom;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

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
                embedBuilder.withDesc(ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc().getString("prefix"));

                // Sends
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            // If the arguments are 2, set the prefix
            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
            {
                // Gets the current prefix
                String prefix = ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc().getString("prefix");

                // Makes sure the prefix isn't over 12 characters long
                if (args.get(1).length() > 6)
                {
                    messageHandler.sendError("Prefix can't be over 6 characters long!");
                    return;
                }

                // Sends the log
                MessageHandler.sendConfigLog("Prefix", args.get(1), prefix, e.getAuthor(), e.getGuild());

                // Sets the prefix in the database & recaches the guild
                ChadVar.DATABASE_DEVICE.set(e.getGuild(), "prefix", args.get(1));
                ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());

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
