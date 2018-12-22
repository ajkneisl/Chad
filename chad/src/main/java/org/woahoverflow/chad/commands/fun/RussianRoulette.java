package org.woahoverflow.chad.commands.fun;

import java.security.SecureRandom;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class RussianRoulette implements Command.Class {
    @Override
    public final Runnable help(MessageReceivedEvent e)
    {
        HashMap<String, String> st = new HashMap<>();
        st.put("rrl <user/@user>", "Plays russian roulette with a selected user.");
        return Command.helpCommand(st, "Russian Roulette", e);
    }

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Assigns the user to the mentioned user
            IUser unFinalUser;
            if (!e.getMessage().getMentions().isEmpty())
                unFinalUser = e.getMessage().getMentions().get(0);
            else {
                messageHandler.sendError(MessageHandler.NO_MENTIONS);
                return;
            }

            // If the user equals themselves or chad, deny
            if (unFinalUser.equals(e.getAuthor()) || unFinalUser.equals(e.getClient().getOurUser()))
            {
                messageHandler.sendError("You can't play with that person!");
                return;
            }

            // Makes the user final
            final IUser user = unFinalUser;

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
                    reacted = false;

                // Checks if the user reacted with the N
                if (nReaction.getUserReacted(user))
                {
                    messageHandler.sendError("User Denied!");
                    return;
                }
            }

            // Calculates the winner with two random numbers
            IUser win = null;
            IUser loser = null;
            int r1 = new SecureRandom().nextInt(100);
            int r2 = new SecureRandom().nextInt(100);
            if (r1 > r2) {
                win = e.getAuthor();
                loser = user;
            }
            if (r2 > r1) {
                win = user;
                loser = e.getAuthor();
            }

            // Makes sure the users aren't null
            if (win == null || loser == null)
            {
                messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                return;
            }

            // Sends the message
            messageHandler.sendMessage('`' +win.getName()+"` is the winner! \n`"+loser.getName()+"`\uD83D\uDD2B");
        };
    }
}
