package org.woahoverflow.chad.commands.gambling;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class CoinFlip implements Command.Class{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (!ChadVar.DATABASE_DEVICE.contains(e.getGuild(), e.getAuthor().getStringID() + "_balance"))
            {
                new MessageHandler(e.getChannel()).sendError("You don't have an account! \n Use `" + ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc().getString("prefix") + "register` to get one!");
                return;
            }

            if (args.size() != 2)
            {
                new MessageHandler(e.getChannel()).sendError("Invalid Arguments!");
                return;
            }

            int bet;
            try {
                bet = Integer.parseInt(args.get(0));
            } catch (NumberFormatException throwaway) {
                new MessageHandler(e.getChannel()).sendError("Invalid Bet!");
                return;
            }

            Integer balance = (Integer) ChadVar.DATABASE_DEVICE.get(e.getGuild(), e.getAuthor().getStringID() + "_balance");
            if (bet > balance)
            {
                new MessageHandler(e.getChannel()).sendError("Your bet is too large!");
                return;
            }

            int user;
            if (args.get(1).equalsIgnoreCase("heads"))
                user = 0;
            else if (args.get(1).equalsIgnoreCase("tails"))
            {
                user = 1;
            }
            else {
                new MessageHandler(e.getChannel()).sendError("Please use `heads` or `tails`!");
                return;
            }

            int flip = new java.util.Random().nextInt(2);

            if (flip == user)
            {
                ChadVar.DATABASE_DEVICE.set(e.getGuild(), e.getAuthor().getStringID() + "_balance", balance+bet);
                new MessageHandler(e.getChannel()).send("You won `"+bet+"`, you now have `" + (balance+bet) + "`.", "Coin Flip");
            }
            else {
                ChadVar.DATABASE_DEVICE.set(e.getGuild(), e.getAuthor().getStringID() + "_balance", balance-bet);
                new MessageHandler(e.getChannel()).send("You lost `"+bet+"`, you now have `" + (balance-bet) + "`.", "Coin Flip");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }
}
