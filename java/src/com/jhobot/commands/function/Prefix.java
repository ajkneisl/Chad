package com.jhobot.commands.function;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Prefix implements Command {

    @DefineCommand(category = Category.FUNCTION)
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (args.size() == 0) {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Prefix");
                b.withDesc("Your prefix is " + ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "prefix"));
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                m.sendEmbed(b.build());
                return;
            }

            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
            {
                if (args.get(1).length() > 12)
                {
                    new MessageHandler(e.getChannel()).sendError("Prefix can't be over 12 characters long!");
                }
                m.sendConfigLog("Prefix", args.get(1), ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "prefix"), e.getAuthor(), e.getGuild(), ChadBot.DATABASE_HANDLER);
                ChadBot.DATABASE_HANDLER.set(e.getGuild(), "prefix", args.get(1));
                m.send("Your prefix is now " + args.get(1), "Changed Prefix");
                return;
            }

            m.sendError("Invalid Arguments");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("prefix", "Your prefix.");
        st.put("prefix set <string>", "Sets the prefix.");
        return HelpHandler.helpCommand(st, "Prefix", e);
    }
}
