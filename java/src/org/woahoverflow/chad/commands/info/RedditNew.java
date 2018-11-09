package org.woahoverflow.chad.commands.info;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.json.JSONObject;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RedditNew implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            if (args.size() < 1) {
                new MessageHandler(e.getChannel()).sendError("Invalid arguments.");
            }

            String link = null;
            JSONObject post = null;
            try {
                int index = 0;
                post = ChadVar.JSON_HANDLER.read("https://reddit.com/r/" + args.get(0) + "/new.json")
                        .getJSONObject("data")
                        .getJSONArray("children")
                        .getJSONObject(index)
                        .getJSONObject("data");
                while (post.getBoolean("stickied")) {
                    index++;
                    post = ChadVar.JSON_HANDLER.read("https://reddit.com/r/" + args.get(0) + "/new.json")
                            .getJSONObject("data")
                            .getJSONArray("children")
                            .getJSONObject(index)
                            .getJSONObject("data");
                }
            } catch (Exception e1) {
                new MessageHandler(e.getChannel()).sendError("Invalid subreddit.");
                return;
            }

            EmbedBuilder b = new EmbedBuilder();
            b.withTitle(post.getString("title"));
            b.withDesc(post.getString("author"));
            b.appendField("Score", Integer.toString(post.getInt("score")) + " (" + Integer.toString(post.getInt("ups")) + "/" + Integer.toString(post.getInt("downs")) + ")", true);
            b.appendField("Comments", Integer.toString(post.getInt("num_comments")), true);
            b.withImage(post.getString("url"));
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withUrl("https://reddit.com" + post.getString("permalink"));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("rnew <subreddit>", "Displays the most recent post from a subreddit.");
        return HelpHandler.helpCommand(st, "Reddit New", e);
    }
}
