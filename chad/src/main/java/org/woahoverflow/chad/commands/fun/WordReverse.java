package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class WordReverse implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (args.size() == 0)
            {
                new MessageHandler(e.getChannel()).sendError("Invalid Arguments!");
                return;
            }

            StringBuilder b = new StringBuilder();
            for (String s : args)
            {
                b.append(s).append(" ");
            }
            String B = b.toString().trim();
            new MessageHandler(e.getChannel()).send("Word: `" + B + "`\n" + b.reverse().toString().trim(), "Word Reverser");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("wr <word>", "Reverses a word");
        return Command.helpCommand(hash, "Word Reverse", e);
    }
}
