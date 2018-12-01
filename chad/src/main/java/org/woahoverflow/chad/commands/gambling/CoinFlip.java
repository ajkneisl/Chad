package org.woahoverflow.chad.commands.gambling;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.List;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

public class CoinFlip implements Command.Class{
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (!DatabaseHandler.handle.contains(e.getGuild(), e.getAuthor().getStringID() + "_balance"))
            {
                new MessageHandler(e.getChannel()).sendError("You don't have an account! \n Use `" + Chad
                    .getGuild(e.getGuild()).getDocument().getString("prefix") + "register` to get one!");
                return;
            }

            if (args.size() == 2 && e.getMessage().getAttachments().isEmpty() && args.get(1).equalsIgnoreCase("tails") || args.get(1).equalsIgnoreCase("heads"))
            {
                long bet;
                try {
                    bet = Long.parseLong(args.get(0));
                } catch (NumberFormatException throwaway) {
                    new MessageHandler(e.getChannel()).sendError("Invalid Bet!");
                    return;
                }

                if (!(bet > 0))
                {
                    new MessageHandler(e.getChannel()).sendError("Invalid Number!");
                    return;
                }

                long balance = (long) DatabaseHandler.handle
                    .get(e.getGuild(), e.getAuthor().getStringID() + "_balance");
                if (bet > balance)
                {
                    new MessageHandler(e.getChannel()).sendError("Your bet is too large!");
                    return;
                }

                if (bet+balance < 0)
                {
                    new MessageHandler(e.getChannel()).sendError("Your balance is too big!\nPlease report this on https://woahoverflow.org/forums");
                    return;
                }

                int user;
                if (args.get(1).equalsIgnoreCase("heads")) {
                    user = 0;
                } else if (args.get(1).equalsIgnoreCase("tails"))
                {
                    user = 1;
                }
                else {
                    new MessageHandler(e.getChannel()).sendError("Please use `heads` or `tails`!");
                    return;
                }

                int flip = new SecureRandom().nextInt(2);

                if (flip == user)
                {
                    DatabaseHandler.handle
                        .set(e.getGuild(), e.getAuthor().getStringID() + "_balance", balance+bet);
                    new MessageHandler(e.getChannel()).send("You won `"+bet+"`, you now have `" + (balance+bet) + "`.", "Coin Flip");
                }
                else {
                    DatabaseHandler.handle
                        .set(e.getGuild(), e.getAuthor().getStringID() + "_balance", balance-bet);
                    new MessageHandler(e.getChannel()).send("You lost `"+bet+"`, you now have `" + (balance-bet) + "`.", "Coin Flip");
                }
            }
            else // assuming that the conditions are met for this
            {
                // Arguments are removed during user building, so I put them here :)
                final String arg0 = args.get(0);

                // Opponent
                IUser opponentUser = null;

                // Builds the user from the arguments
                if (e.getMessage().getMentions().isEmpty())
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    args.remove(0);
                    for (String s : args)
                    {
                        stringBuilder.append(s).append(' ');
                        if (!e.getGuild().getUsersByName(stringBuilder.toString().trim()).isEmpty())
                        {
                            opponentUser = e.getGuild().getUsersByName(stringBuilder.toString().trim()).get(0);
                            break;
                        }
                    }

                    // Checks if the loop actually found a user
                    if (opponentUser == null)
                    {
                        new MessageHandler(e.getChannel()).sendError("Invalid User!");
                        return;
                    }
                }
                else {
                    // If there's a mention use that instead
                    if (args.get(1).contains(e.getMessage().getMentions().get(0).getStringID()))
                    {
                        opponentUser = e.getMessage().getMentions().get(0);
                    }
                    else {
                        new MessageHandler(e.getChannel()).sendError("Invalid User!");
                        return;
                    }
                }

                // only used once, but thanks lamda
                final IUser user = opponentUser;

                // Sends the invitation message
                IMessage acceptMessage = RequestBuffer.request(() -> e.getChannel().sendMessage("Do you accept `" + e.getAuthor().getName() + "`'s challenge, `" + user.getName() + "`?")).get();

                // Creates a request buffer and reacts with the Y and N emojis
                RequestBuilder rb = new RequestBuilder(e.getClient());
                rb.shouldBufferRequests(true);
                rb.doAction(() -> {
                    acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")); // Y
                    return true;
                }).andThen(() -> {
                    acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")); // N
                    return true;
                }).execute(); // Executes

                // Assigns variables
                boolean reacted = true;
                int timeout = 0;

                while (reacted)
                {
                    // If it's been 10 seconds, exit
                    if (timeout == 10)
                    {
                        new MessageHandler(e.getChannel()).sendError('`' +user.getName()+"` didn't respond in time!");
                        return;
                    }

                    // Sleeps a second so it doesn't go so fast
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    // Increases the timeout value
                    timeout++;

                    // Gets both reactions
                    IReaction yReaction = RequestBuffer.request(() -> acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE"))).get();
                    IReaction nReaction = RequestBuffer.request(() -> acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3"))).get();

                    // Checks if the user reacted to the Y
                    if (yReaction.getUserReacted(user))
                    {
                        reacted = false;
                    }

                    // Checks if the user reacted with the N
                    if (nReaction.getUserReacted(user))
                    {
                        new MessageHandler(e.getChannel()).send("User Denied!", "CoinFlip");
                        return;
                    }
                }

                // Calculates the Bet
                long bet;
                try {
                    bet = Long.parseLong(arg0);
                } catch (NumberFormatException throwaway) {
                    new MessageHandler(e.getChannel()).sendError("Invalid Bet!");
                    return;
                }

                if (!(bet > 0))
                {
                    new MessageHandler(e.getChannel()).sendError("Invalid Number!");
                    return;
                }

                // Gets the author's balance
                long balance = (long) DatabaseHandler.handle
                    .get(e.getGuild(), e.getAuthor().getStringID() + "_balance");

                // Checks if the user's bet is bigger than the balance.
                if (bet > balance)
                {
                    new MessageHandler(e.getChannel()).sendError("Your bet is too large!");
                    return;
                }

                // Gets the opponent's balance
                long opponentBalance = (long) DatabaseHandler.handle
                    .get(e.getGuild(), opponentUser.getStringID() + "_balance");

                // Checks if the bet's bigger than the opponent's balance
                if (bet > opponentBalance)
                {
                    new MessageHandler(e.getChannel()).sendError("Your bet is too large for `"+opponentUser.getName()+"`!");
                    return;
                }

                // Checks if the author's balance is too big
                if (bet+balance < 0)
                {
                    new MessageHandler(e.getChannel()).sendError("Your balance is too big!\nPlease report this on https://woahoverflow.org/forums");
                    return;
                }

                // Checks if the opponent's balance is too big
                if (bet+opponentBalance < 0)
                {
                    new MessageHandler(e.getChannel()).sendError('`' +opponentUser.getName()+"`'s balance is too big!\nPlease report this on https://woahoverflow.org/forums");
                    return;
                }

                // Sends a message for the tails & heads declaring
                IMessage pick = RequestBuffer.request(() -> e.getChannel().sendMessage("**X**`heads` or **O**`tails`? __(react)__")).get();

                // Request buffer to apply the reactions
                RequestBuilder r = new RequestBuilder(e.getClient());
                r.shouldBufferRequests(true);

                r.doAction(() -> {
                    pick.addReaction(ReactionEmoji.of("\uD83C\uDDFD")); // Reacts with X
                    return true;
                }).andThen(() -> {
                    pick.addReaction(ReactionEmoji.of("\uD83C\uDDF4")); // Reacts with O
                    return true;
                }).execute(); // Executes the builder

                // Variable declaring
                timeout = 0;
                IUser tails = null;
                IUser heads = null;
                boolean bothReacted = false;
                while (!bothReacted)
                {
                    // So it doesn't go so fast.
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    // If the user hasn't responded within 10 seconds, it times out.
                    if (timeout == 10)
                    {
                        new MessageHandler(e.getChannel()).sendError('`' +opponentUser.getName()+"` didn't respond in time!");
                        return;
                    }

                    // Adds another second to the timeout
                    timeout++;

                    // X reaction
                    final IReaction x = RequestBuffer.request(() -> pick.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFD"))).get();

                    // O reaction
                    final IReaction o = RequestBuffer.request(() -> pick.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF4"))).get();

                    // TODO: comment this stuff, I can't be bothered atm

                    if (heads == null)
                    {
                        if (o.getUserReacted(e.getAuthor()))
                        {
                            if (tails == null)
                                heads = e.getAuthor();
                            else if (!tails.equals(e.getAuthor()))
                                heads = e.getAuthor();
                        }

                        if (o.getUserReacted(opponentUser))
                        {
                            if (tails == null)
                                heads = opponentUser;
                            else if (!tails.equals(opponentUser))
                                heads = opponentUser;
                        }
                    }

                    if (tails == null)
                    {
                        if (x.getUserReacted(e.getAuthor()))
                        {
                            if (heads == null)
                                tails = e.getAuthor();
                            else if (!heads.equals(e.getAuthor()))
                                tails = e.getAuthor();
                        }

                        if (x.getUserReacted(opponentUser))
                        {
                            if (heads == null)
                                tails = opponentUser;
                            else if (!heads.equals(opponentUser))
                                tails = opponentUser;
                        }
                    }


                    // If both users have selected one, the loop stops.
                    if (tails != null & heads != null)
                        bothReacted = true;
                }

                // Removes all the reactions
                RequestBuffer.request(pick::removeAllReactions);

                // Ties the user's balances to their name
                long tailsBalance;
                long headsBalance;
                if (e.getAuthor().equals(tails) && user.equals(heads))
                {
                    tailsBalance = balance;
                    headsBalance = opponentBalance;
                }
                else {
                    tailsBalance = opponentBalance;
                    headsBalance = balance;
                }

                // 0 is tails winning, 1 is heads winning
                if (Util.coinflip())
                {
                    // Sets the user's balances
                    DatabaseHandler.handle
                        .set(e.getGuild(), tails.getStringID() + "_balance", tailsBalance+bet);
                    DatabaseHandler.handle
                        .set(e.getGuild(), heads.getStringID()+"_balance", headsBalance-bet);

                    // Creates the edit string, then applies.
                    final String editString = '`' +tails.getName()+"` has won `" + bet + "`!"
                        + "\n\n`"+tails.getName()+"` now has `"+(tailsBalance+bet)+"`, `"+heads.getName()+"` now has `"+(headsBalance-bet)+ '`';
                    RequestBuffer.request(() -> pick.edit(editString));
                }
                else /* flip is 1, so heads wins this */
                {
                    // Sets the user's balances
                    DatabaseHandler.handle
                        .set(e.getGuild(), tails.getStringID() + "_balance", tailsBalance-bet);
                    DatabaseHandler.handle
                        .set(e.getGuild(), heads.getStringID()+"_balance", headsBalance+bet);

                    // Creates the edit string, then applies.
                    final String editString = '`' +heads.getName()+"` has won `" + bet + "`!"
                        + "\n`"+heads.getName()+"` now has `"+(headsBalance+bet)+"`, `"+tails.getName()+"` now has `"+(tailsBalance-bet)+ '`';
                    RequestBuffer.request(() -> pick.edit(editString));
                }
            }
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("coinflip <amount to bet> <tails/heads>", "College?");
        st.put("coinflip <amount to bet> <@user>", "Play coinflip with another user!");
        return Command.helpCommand(st, "CoinFlip", e);
    }
}
