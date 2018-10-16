package com.jhobot.commands.info;

import com.jhobot.JhoBot;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Help implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            IMessage m = RequestBuffer.request(() -> {
                return e.getChannel().sendMessage("Fun : `catfact`, `catgallery`, `8ball`, `pe`, `random`, `rrl`\n" +
                        "Function / Admin: `logging`, `prefix`\n" +
                        "Info : `bug`, `guildinfo`, `help`, `jho`, `steam`, `updatelog`, `userinfo`\n" +
                        "Punishments : `ban`, `kick`");
            }).get();
       };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("help", "Displays all commands Jho has to offer.");
        return HelpHandler.helpCommand(st, "Help", e);
    }
}
