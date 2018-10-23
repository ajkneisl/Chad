package com.jhobot.commands.info;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class RedditTop implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            try {
                System.out.println("Before http request");

                String link = JhoBot.JSON_HANDLER.read("http://reddit.com/r/" + args.get(0) + ".json")
                        .getJSONObject("")
                        .getJSONObject("data")
                        .getJSONArray("children")
                        .getJSONObject(0)
                        .getJSONObject("data")
                        .getString("permalink");

                System.out.println("After http request");
                System.out.println("RedditTop (link=" + link + ")");

                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Reddit");
                b.withDesc("Top posts");
                b.appendField("r/" + args.get(0), link, false);
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                new MessageHandler(e.getChannel()).sendEmbed(b.build());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
