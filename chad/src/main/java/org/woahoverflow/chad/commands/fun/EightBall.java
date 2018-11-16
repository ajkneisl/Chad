package org.woahoverflow.chad.commands.fun;

import org.json.JSONArray;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EightBall implements Command.Class  {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (args.size() == 0)
            {
                m.sendError("You didn't ask a question!");
                return;
            }
            JSONArray answers = ChadVar.JSON_HANDLER.readArray("https://raw.githubusercontent.com/woahoverflow/Chad-Repo/master/data/8ball.json");

            Random random = new Random();
            int index = random.nextInt(answers.length());
            m.send((String) answers.get(index), "8Ball");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("8ball <question>", "The eight ball always answers your best questions.");
        return Command.helpCommand(st, "Eight Ball", e);
    }
}
