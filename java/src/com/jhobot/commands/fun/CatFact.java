package com.jhobot.commands.fun;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CatFact implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            try{
                String fact = JSON.read("https://catfact.ninja/fact").getString("fact");
                new Messages(e.getChannel()).send(fact, "Cat Fact");
            } catch (IOException ee)
            {
                ee.printStackTrace();
                new Messages(e.getChannel()).sendError("There was an internal error.");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catfact", "Gives you a random catfact.");
        return HelpHandler.helpCommand(st, "Cat Fact", e);
    }
}
