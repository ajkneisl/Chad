package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class WordReverse implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());
            // Makes sure the arguments aren't empty
            if (args.isEmpty())
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            // Gets the word from all the arguments
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : args)
            {
                stringBuilder.append(s).append(' ');
            }

            // Gets the word & sends
            String word = stringBuilder.toString().trim();
            new MessageHandler(e.getChannel()).send("Word: `" + word + "`\n" + stringBuilder.reverse().toString().trim(), "Word Reverser");
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("wr <word>", "Reverses a word");
        return Command.helpCommand(hash, "Word Reverse", e);
    }
}
