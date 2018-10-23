package com.jhobot.commands.fun;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EightBall implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (args.size() == 0)
            {
                m.sendError("You didn't ask a question!");
                return;
            }
            String[] answers = {"It is certain", "It is decidedly so", "Without a doubt",
                    "Yes - definitely", "You may rely on it", "As I see it, yes",
                    "Most likely", "Outlook good", "Signs point to yes",
                    "Yes", "Reply hazy, try again", "Ask again later",
                    "Better not tell you now", "Cannot predict now", "Concentrate and ask again",
                    "Don't count on it", "My reply is no", "My sources say no",
                    "Outlook not so good", "Very doubtful"};

            Random random = new Random();
            int index = random.nextInt(answers.length);
            m.send(answers[index], "8Ball");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("8ball <question>", "The eight ball always answers your best questions.");
        return HelpHandler.helpCommand(st, "Eight Ball", e);
    }
}
