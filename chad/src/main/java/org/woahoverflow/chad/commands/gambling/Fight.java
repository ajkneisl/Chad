package org.woahoverflow.chad.commands.gambling;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

public class Fight implements Command.Class
{
    public MessageHandler messageHandler;

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            messageHandler = new MessageHandler(e.getChannel());

            if (!(args.size() >= 2))
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            int player1Health = 0;
            int player1Sword = 0;
            int player1Shield = 0;

            int player2Health = 0;
            int player2Sword = 0;
            int player2Shield = 0;

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
                    messageHandler.sendError("Invalid User!");
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
                    messageHandler.sendError("Invalid User!");
                    return;
                }
            }

            IMessage acceptMessage = askToPlay(e, e.getAuthor(), opponentUser);

            // only used once, but thanks lambda
            final IUser user = opponentUser;

            // Assigns variables
            boolean reacted = true;
            int timeout = 0;

            while (reacted)
            {
                // If it's been 10 seconds, exit
                if (timeout == 10)
                {
                    messageHandler.sendError('`' +user.getName()+"` didn't respond in time!");
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
                    player1Health = 5;
                    player1Sword = 5;
                    player1Shield = 5;
                    player2Health = 5;
                    player2Sword = 5;
                    player2Shield = 5;

                    IUser player1 = e.getAuthor();
                    IUser player2 = opponentUser;

                    IUser activeUser = player1; // player1 gets the first turn (player1 is always the author)

                    boolean firstTurn = true;

                    boolean canPlay = true;
                    while (canPlay)
                    {
                        int player1HealthOffset = 0;
                        int player1SwordOffset = 0;
                        int player1ShieldOffset = 0;

                        int player2HealthOffset = 0;
                        int player2SwordOffset = 0;
                        int player2ShieldOffset = 0;

                        // check for a winner
                        boolean player1Dead = false;
                        boolean player2Dead = false;
                        if (player1Health < 1)
                        {
                            player1Dead = true;
                            canPlay = false;
                        }

                        if (player2Health < 1)
                        {
                            player2Dead = true;
                            canPlay = false;
                        }

                        if (player1Dead || player2Dead)
                        {
                            if (!player1Dead && player2Dead) // player1 wins
                            {
                                doEdit(e, acceptMessage, "GAME OVER! `" + player1.getName() + "` wins!");
                                return;
                            }
                            else if (player1Dead && !player2Dead) // player2 wins
                            {
                                doEdit(e, acceptMessage, "GAME OVER! `" + player2.getName() + "` wins!");
                                return;
                            }
                            else if (player1Dead && player2Dead) // its a draw
                            {
                                doEdit(e, acceptMessage, "GAME OVER! It was a draw.");
                                return;
                            }
                        }

                        /* ----- player1's turn ----- */

                        IMessage fightMessage = changeTurn(e, activeUser);
                        acceptMessage.delete();

                        IReaction aReaction = RequestBuffer.request(() -> fightMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDE6"))).get();
                        IReaction dReaction = RequestBuffer.request(() -> fightMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDE9"))).get();

                        boolean canAttack = true;
                        boolean canDefend = true;

                        int to = 0;
                        while (to < 10)
                        {
                            try
                            {
                                TimeUnit.SECONDS.sleep(1);
                                to++;
                                if (aReaction.getUserReacted(activeUser) || dReaction.getUserReacted(activeUser))
                                    break;
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }

                        if (to == 10)
                        {
                            fightMessage.delete();
                            messageHandler.sendMessage("`" + player1.getName() + "` didn't choose fast enough!");
                            return;
                        }

                        if (aReaction.getUserReacted(activeUser) && canAttack) // i dont think i need this check, but ill add it just in case
                        {
                            if (activeUser == player1) // if the active user is player1, attack player2
                            {
                                player2HealthOffset++; // reduce player2's health
                                player1SwordOffset++; // reduce player1's sword
                            }
                            else // if the active user is player2, attack player1 (btw i dont think i need this check because activeUser is always player1 during this stage)
                            {
                                player1HealthOffset++; // reduce player1's health
                                player2SwordOffset++; // reduce player2's sword
                            }

                            canDefend = false; // can only choose one :)
                        }

                        if (dReaction.getUserReacted(activeUser) && canDefend)
                        {
                            if (activeUser == player1) // if active user is player1
                            {
                                if (player1HealthOffset > 0) // if player1 health is supposed to go down
                                {
                                    player1HealthOffset--; // reduce it by 1
                                    player1ShieldOffset++; // reduce shield by 1
                                }
                            }
                            else // if active user is player2
                            {
                                if (player2HealthOffset > 0) // if player2 health is supposed to go down
                                {
                                    player1HealthOffset--; // reduce it by 1
                                    player1ShieldOffset++;
                                }
                            }

                            canAttack = false;
                        }

                        /* ----- player2's turn ----- */

                        activeUser = player2;

                        fightMessage.delete();
                        IMessage newFightMessage = changeTurn(e, activeUser);

                        aReaction = RequestBuffer.request(() -> newFightMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDE6"))).get();
                        dReaction = RequestBuffer.request(() -> newFightMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDE9"))).get();

                        // make sure player2 can attack & defend
                        canAttack = true;
                        canDefend = true;

                        int to2 = 0;
                        while (to2 < 10)
                        {
                            try
                            {
                                TimeUnit.SECONDS.sleep(1);
                                to2++;
                                if (aReaction.getUserReacted(activeUser) || dReaction.getUserReacted(activeUser))
                                    break;
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }

                        if (to2 == 10)
                        {
                            newFightMessage.delete();
                            messageHandler.sendMessage("`" + player2.getName() + "` didn't choose fast enough!");
                            return;
                        }

                        if (aReaction.getUserReacted(activeUser) && canAttack) // i dont think i need this check, but ill add it just in case
                        {
                            if (activeUser == player1) // if the active user is player1, attack player2
                            {
                                player2HealthOffset++; // reduce player2's health
                                player1SwordOffset++; // reduce player1's sword
                            }
                            else // if the active user is player2, attack player1 (btw i dont think i need this check because activeUser is always player1 during this stage)
                            {
                                player1HealthOffset++; // reduce player1's health
                                player2SwordOffset++; // reduce player2's sword
                            }

                            canDefend = false; // can only choose one :)
                        }

                        if (dReaction.getUserReacted(activeUser) && canDefend)
                        {
                            if (activeUser == player1) // if active user is player1
                            {
                                if (player1HealthOffset > 0) // if player1 health is supposed to go down
                                {
                                    player1HealthOffset--; // reduce it by 1
                                    player1ShieldOffset++; // reduce shield by 1
                                }
                            }
                            else // if active user is player2
                            {
                                if (player2HealthOffset > 0) // if player2 health is supposed to go down
                                {
                                    player1HealthOffset--; // reduce it by 1
                                    player1ShieldOffset++;
                                }
                            }

                            canAttack = false;
                        }

                        /* ----- calculate new values ----- */

                        System.out.println("\nplayer1HealthOffset = " + player1HealthOffset);
                        System.out.println("Player2HealthOffset = " + player2HealthOffset);

                        player1Health = player1Health - player1HealthOffset;
                        player2Health = player2Health - player2HealthOffset;

                        player1Shield = player1Shield - player1ShieldOffset;
                        player2Shield = player2Shield - player2ShieldOffset;

                        System.out.println("\nnew player1Health = " + player1Health);
                        System.out.println("new player2Health = " + player2Health);

                        // set the active user back to player1
                        activeUser = player1;
                    }

                    messageHandler.sendError("Thread died.");
                    reacted = false;
                }

                // Checks if the user reacted with the N
                if (nReaction.getUserReacted(user))
                {
                    messageHandler.send("They didn't want to play :'(", "CoinFlip");
                    return;
                }
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }

    public IMessage askToPlay(MessageReceivedEvent e, IUser authorUser, IUser opponentUser)
    {
        // only used once, but thanks lambda
        final IUser user = opponentUser;

        // Sends the invitation message
        IMessage acceptMessage = RequestBuffer
                .request(() -> e.getChannel().sendMessage("Do you accept `" + authorUser.getName() + "`'s challenge, `" + user.getName() + "`?")).get();

        // Creates a request buffer and reacts with the Y and N emotes
        RequestBuilder rb = new RequestBuilder(e.getClient());
        rb.shouldBufferRequests(true);
        rb.doAction(() -> {
            acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")); // Y
            return true;
        }).andThen(() -> {
            acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")); // N
            return true;
        }).execute(); // Executes

        return acceptMessage;
    }

    public IMessage changeTurn(MessageReceivedEvent e, IUser activeUser)
    {
        IMessage fightMessage = RequestBuffer
                .request(() -> e.getChannel().sendMessage("Active player: `" + activeUser.getName() + "`\nWhat do you want do to?\n**A** for `attack`.\n**D** for `defend`.")).get();

        RequestBuilder rb = new RequestBuilder(e.getClient());
        rb.shouldBufferRequests(true);
        rb.doAction(() -> {
            fightMessage.addReaction(ReactionEmoji.of("\uD83C\uDDE6")); // A
            return true;
        }).andThen(() -> {
            fightMessage.addReaction(ReactionEmoji.of("\uD83C\uDDE9")); // D
            return true;
        }).execute(); // Executes

        return fightMessage;
    }

    public void doEdit(MessageReceivedEvent e, IMessage message, String content)
    {
        message.edit(content);
    }
}
