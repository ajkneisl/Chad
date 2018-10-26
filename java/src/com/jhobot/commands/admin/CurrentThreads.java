package com.jhobot.commands.admin;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CurrentThreads implements Command {
    @DefineCommand(category = Category.ADMIN, devOnly = true)
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            /*boolean allowed = false;
            String[] admins = JhoBot.JSON_HANDLER.get("admins").split(" ");
            for (int i = 0; admins.length > i; i++)
            {
                if (Long.parseLong(admins[0]) == e.getAuthor().getLongID())
                    allowed = true;
            }
            if (!allowed)
            {
                m.sendError("You don't have permissions for this!");
                return;
            }*/
            EmbedBuilder b = new EmbedBuilder();
            b.withFooterText(Util.getTimeStamp());

            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withTitle("Current Threads Running");
            ThreadCountHandler.HANDLER.getMap().forEach((k, v) -> b.appendField(k.getName() + " [" + k.getLongID() + "]", Integer.toString(v.size()), false));
            m.sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("threads", "Displays all running threads for users.");
        return HelpHandler.helpCommand(st, "Current Threads", e);
    }

}
