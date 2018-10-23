package com.jhobot.commands.info;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.List;

public class RedditTop implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            try {
                MessageHandler messageHandler = new MessageHandler(e.getChannel());

                System.setProperty("http.agent", "Chrome");

                String link = JhoBot.JSON_HANDLER.read("http://reddit.com/r/" + args.get(0) + ".json").getJSONObject("data").getJSONArray("children").getJSONObject(0).getString("permalink");

                messageHandler.send("Here is the top post from r/" + args.get(0) + ": " + link, "Reddit");

                System.out.println("RedditTop (link=" + link + ")");
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
