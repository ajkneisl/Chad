package com.jhobot.commands.function;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.*;
import org.bson.Document;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Prefix implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (args.size() == 0) {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Prefix");
                ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc().getString("prefix");
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                m.sendEmbed(b.build());
                return;
            }

            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
            {
                String prefix = ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc().getString("prefix");
                if (args.get(1).length() > 12)
                {
                    new MessageHandler(e.getChannel()).sendError("Prefix can't be over 12 characters long!");
                }
                m.sendConfigLog("Prefix", args.get(1), prefix, e.getAuthor(), e.getGuild());
                ChadVar.DATABASE_HANDLER.set(e.getGuild(), "prefix", args.get(1));
                m.send("Your prefix is now " + args.get(1), "Changed Prefix");
                ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());
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
