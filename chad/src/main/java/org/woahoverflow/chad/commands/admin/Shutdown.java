package org.woahoverflow.chad.commands.admin;

import java.util.concurrent.TimeUnit;
import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.StatusType;

import java.util.HashMap;
import java.util.List;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class Shutdown implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Requests to send the confirmation message then gets it
            IMessage confirm = RequestBuffer.request(() -> e.getChannel().sendMessage("Are you sure you want to do this?")).get();

            // The emojis used in the message
            final ReactionEmoji yes = ReactionEmoji.of("✅");
            final ReactionEmoji no = ReactionEmoji.of("❌");

            // Adds both reactions
            RequestBuilder builder = new RequestBuilder(e.getClient());
            builder.shouldBufferRequests(true);
            builder.doAction(() -> {
                confirm.addReaction(yes);
                return true;
            }).andThen(() -> {
                confirm.addReaction(no);
                return true;
            }).execute();

            boolean userReacted = false;
            int timeout = 0;
            while (!userReacted)
            {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                // If the user's taken more than 10 seconds
                if (timeout >= 10)
                {
                    messageHandler.sendError("You didn't react fast enough!");
                    return;
                }

                // Add another second
                timeout++;

                // If they've accepted
                if (confirm.getReactionByEmoji(yes).getUserReacted(e.getAuthor()))
                    userReacted = true;

                // If they've denied
                if (confirm.getReactionByEmoji(no).getUserReacted(e.getAuthor()))
                {
                    messageHandler.sendError("Cancelled shutdown!");
                    return;
                }
            }

            // Deletes the confirmation message
            RequestBuffer.request(confirm::delete);

            // Warns that the bot is shutting down
            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc("Chad is shutting down in 10 seconds..."));

            // Warns within the UI
            UIHandler.handle.addLog("Shutting down in 10 seconds...", UIHandler.LogLevel.SEVERE);

            // Updates the presence
            ChadBot.cli.changePresence(StatusType.DND, ActivityType.PLAYING, "Shutting down...");

            // Waits 10 seconds
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            // Exits
            ChadBot.cli.logout();
            System.exit(0);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("shutdown", "Shuts the bot down.");
        return Command.helpCommand(hash, "Shutdown", e);
    }
}
