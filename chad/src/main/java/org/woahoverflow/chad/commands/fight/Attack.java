package org.woahoverflow.chad.commands.fight;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

public class Attack implements Command.Class
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

                Player authorPlayer = PlayerManager.handle.getPlayer(e.getAuthor().getLongID());

                // opponent existence sanity check
                if (PlayerManager.handle.getPlayer(opponentUser.getLongID()) == null)
                {
                    messageHandler.sendMessage(String.format("Oh noes! %s doesn't exist. It really do be like that sometimes, don't it?", opponentUser.mention()));
                    return;
                }

                // author health sanity check
                if (authorPlayer.getPlayerHealth() < 1)
                {
                    messageHandler.sendMessage(String.format("Slow down there cowboy, dead men tell no tales."));
                    return;
                }

                // randomize damage and decrement opponent's health
                int damage = new Random().nextInt(3);
                PlayerManager.handle.attackPlayer(opponentUser.getLongID(), damage);

                messageHandler.sendMessage(String.format("You did %s damage to %s", damage, opponentUser.mention()));

                // after attack, decrement author sword health by the same amount of damage done to the opponent
                authorPlayer.decrementSwordHealth(damage);

                // opponent death sanity check
                if (PlayerManager.handle.getPlayer(opponentUser.getLongID()).getPlayerHealth() < 1)
                {
                    messageHandler.sendMessage(String.format("Well done kinsman, you managed to finish off %s!", opponentUser.mention()));
                }

                // author sword sanity check
                if (authorPlayer.getSwordHealth() < 1)
                {
                    messageHandler.sendMessage(String.format("Oh noes! Your sword broke!"));
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
