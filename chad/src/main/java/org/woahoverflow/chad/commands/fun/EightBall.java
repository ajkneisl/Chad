package org.woahoverflow.chad.commands.fun;

import java.security.SecureRandom;
import org.json.JSONArray;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class EightBall implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Makes sure they asked a question
            if (args.isEmpty())
            {
                messageHandler.sendError("You didn't ask a question!");
                return;
            }

            // Gets the answers from the cdn
            JSONArray answers = JsonHandler.handle.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json");

            // Sends the answer
            messageHandler.sendEmbed(
                new EmbedBuilder().withDesc(
                    answers.getString(new SecureRandom().nextInt(answers.length()))
                )
            );
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("8ball <question>", "The eight ball always answers your best questions.");
        return Command.helpCommand(st, "Eight Ball", e);
    }
}
