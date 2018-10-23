package com.jhobot.commands.admin;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.ThreadCountHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

public class CurrentThreads implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            System.setProperty("http.agent", "Chrome");
            MessageHandler m = new MessageHandler(e.getChannel());
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Current Threads Running");
            ThreadCountHandler.HANDLER.getMap().forEach((k, v) -> {
                b.appendField(k.getName(), Integer.toString(v.size()), true);
            });
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
