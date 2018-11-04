package com.jhobot.commands.admin;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Cache implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (args.size() == 1 && args.get(0).equalsIgnoreCase("recache"))
            {
                ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());
                new MessageHandler(e.getChannel()).send("ReCached current guild", "Caching");
                return;
            }
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Cache Status");
            b.withDesc("Current Cached Guilds `" + ChadVar.GUILD_CACHE.size() + "`\n"+
                    "Last Cached in Current Guild `" + ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).lastCached() + "`\n");
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("cache", "Giving information about caching.");
        return HelpHandler.helpCommand(st, "Cache Status", e);
    }
}
