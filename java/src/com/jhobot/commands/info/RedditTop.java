package com.jhobot.commands.info;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import org.json.JSONObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RedditTop implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            String link = null;
            JSONObject post = null;
            try {
                int index = 0;
                post = JhoBot.JSON_HANDLER.read("https://reddit.com/r/" + args.get(0) + ".json?sort=hot")
                        .getJSONObject("data")
                        .getJSONArray("children")
                        .getJSONObject(index)
                        .getJSONObject("data");
                while (post.getBoolean("stickied")) {
                    index++;
                    post = JhoBot.JSON_HANDLER.read("https://reddit.com/r/" + args.get(0) + ".json?sort=hot")
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
            b.withTitle("Reddit");
            b.withDesc("Top post");
            b.appendField("Title", post.getString("title"), false);
            b.appendField("Link", "https://reddit.com" + post.getString("permalink"), false);
            b.withImage(post.getString("url"));
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("rtop <subreddit>", "Displays a post from that subreddit.");
        return HelpHandler.helpCommand(st, "Reddit Top", e);
    }
}
