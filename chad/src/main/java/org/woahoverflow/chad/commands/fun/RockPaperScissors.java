package org.woahoverflow.chad.commands.fun;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.stream.Stream;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class RockPaperScissors implements Command.Class
{
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () ->
        {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Checks if there's arguments
            if (args.isEmpty())
            {
                new MessageHandler(e.getChannel()).sendError("Invalid Arguments");
                return;
            }

            // Makes sure the arguments are rock, paper and scissors
            if (Stream.of("rock", "paper", "scissors").anyMatch(s -> args.get(0).equalsIgnoreCase(s)))
            {
                // Gets Chad's value
                int i2 = new SecureRandom().nextInt(3);

                // Forms the author's value
                int i = 420;
                if (args.get(0).equalsIgnoreCase("rock"))
                {
                    i = 0;
                }
                else if (args.get(0).equalsIgnoreCase("paper"))
                {
                    i = 1;
                }
                else if (args.get(0).equalsIgnoreCase("scissors"))
                {
                     i = 2;
                }

                // Sends the result
                messageHandler.send(calculateWinner(i, i2), "Rock Paper Scissors");
            }
            else {
                messageHandler.sendError("Invalid Arguments");
            }
        };
    }

    // Builds the string for RPS
    private static String calculateWinner(int i, int i2)
    {
        // 'i' is meant for the user's input
        // 'i2' is meant for the bot's input

        // Builds Chad's value
        String chadValue;
        switch (i2) {
            case 0:
                chadValue = "Rock";
                break;
            case 1:
                chadValue = "Paper";
                break;
            case 2:
                chadValue = "Scissors";
                break;
            default:
                return MessageHandler.INTERNAL_EXCEPTION;
        }

        // If they're both equal, tie
        if (i2 == i)
        {
            return "Chad had `"+chadValue+"`, tie!";
        }

        // If Chad has scissors and author has paper
        if (i2 == 2 && i == 1)
        {
            return "Chad had `"+chadValue+"`, Chad wins!";
        }

        // If Chad has rock and the author has scissors
        if (i2 == 0 && i == 2)
        {
            return "Chad had `"+chadValue+"`, Chad wins!";
        }

        // If Chad has Paper and the author has rock
        if (i2 == 1 && i == 0)
        {
            return "Chad had `"+chadValue+"`, Chad wins!";
        }

        // The rest of the inputs are the author winning, so
        return "Chad had `"+chadValue+"`, you win!";
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("rps <rock/paper/scissors>", "Plays rock paper scissors with Chad.");
        return Command.helpCommand(st, "Rock Paper Scissors", e);
    }
}
