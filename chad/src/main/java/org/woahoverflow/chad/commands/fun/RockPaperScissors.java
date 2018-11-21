package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class RockPaperScissors implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () ->
        {
            // TODO clean this up, it's really unneccesary to have each one have a message, rather just create a calculator with 2 ints 1 - 3 for ea user
            if (args.size() == 0)
            {
                new MessageHandler(e.getChannel()).sendError("Invalid Arguments");
                return;
            }

            if (args.get(0).equalsIgnoreCase("rock") || args.get(0).equalsIgnoreCase("paper") || args.get(0).equalsIgnoreCase("scissors"))
            {
                int i = new java.util.Random().nextInt(3);
                System.out.println("i = " + i);

                int i2;
                if (args.get(0).equalsIgnoreCase("rock"))
                    i2 = 1;
                else if (args.get(0).equalsIgnoreCase("paper"))
                    i2 = 2;
                else
                    i2 = 3;

                // block with user being rock
                if (i2 == 1 && i == 0)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `rock`, tie!", "Rock Paper Scissors");
                    return;
                }
                else if (i2 == 1 && i == 1)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `paper`, Chad wins!", "Rock Paper Scissors");
                    return;
                }
                else if (i2 == 1 && i == 2)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `scissors`, you win!", "Rock Paper Scissors");
                    return;
                }

                // block with user being paper
                if (i2 == 2 && i == 0)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `rock`, you win!", "Rock Paper Scissors");
                    return;
                }
                else if (i2 == 2 && i == 1)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `paper`, tie!", "Rock Paper Scissors");
                    return;
                }
                else if (i2 == 2 && i == 2)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `scissors`, Chad wins!", "Rock Paper Scissors");
                    return;
                }

                if (i2 == 3 && i == 0)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `rock`, Chad wins!", "Rock Paper Scissors");
                    return;
                }
                else if (i2 == 3 && i == 1)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `paper`, you win!", "Rock Paper Scissors");
                    return;
                }
                else if (i2 == 3 && i == 2)
                {
                    new MessageHandler(e.getChannel()).send("Chad had `scissors`, tie!", "Rock Paper Scissors");
                    return;
                }

                new MessageHandler(e.getChannel()).sendError("Internal Exception");
            }
            else {
                new MessageHandler(e.getChannel()).sendError("Invalid Arguments");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }
}
