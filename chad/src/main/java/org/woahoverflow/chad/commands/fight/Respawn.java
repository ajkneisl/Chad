package org.woahoverflow.chad.commands.fight;

import java.util.HashMap;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.ui.ChadError;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Respawn implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Sends the invitation message
            IMessage acceptMessage = RequestBuffer
                    .request(() -> e.getChannel().sendMessage("Are you sure you want to respawn?")).get();

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
                    messageHandler.sendError("You didn't respond in time!");
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
                if (yReaction.getUserReacted(e.getAuthor()))
                {
                    int oldBalance = (int)PlayerHandler.handle.getPlayer(e.getAuthor().getLongID()).getObject(Player.DataType.BALANCE);
                    PlayerHandler.handle.removePlayer(e.getAuthor().getLongID());
                    PlayerHandler.handle.createSetPlayer(e.getAuthor().getLongID(), 50, 50, 50, oldBalance);

                    messageHandler.sendMessage("Successfully respawned you with half stats. Your balance will remain the same.");
                    reacted = false;
                    return;
                }

                // Checks if the user reacted with the N
                if (nReaction.getUserReacted(e.getAuthor()))
                {
                    messageHandler.sendMessage("Ok, we won't respawn you.");
                    return;
                }
            }

            ChadError.throwError("Respawn thread died");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        //st.put("", "");
        return Command.helpCommand(st, "Respawn", e);
    }
}
