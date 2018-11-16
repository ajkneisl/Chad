package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Cache implements Command.Class{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            if (args.size() == 0)
            {
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Cache Status");
                b.withDesc("Current Cached Guilds `" + ChadVar.GUILD_CACHE.size() + "`\n"+
                        "Last Cached in Current Guild `" + ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).lastCached() + "`\n");
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                b.withFooterText(Util.getTimeStamp());
                new MessageHandler(e.getChannel()).sendEmbed(b.build());
                return;
            }
            switch(args.get(0).toLowerCase())
            {
                case "recache":
                    ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());
                    new MessageHandler(e.getChannel()).send("ReCached current guild", "Caching Manager");
                    return;
                case "recacheall":
                    ChadVar.CACHE_DEVICE.reCacheAll();
                    new MessageHandler(e.getChannel()).send("ReCached all guilds", "Caching Manager");
                    return;
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("cache", "Giving information about caching.");
        st.put("cache recache", "ReCaches current guild.");
        st.put("cache recacheall", "ReCaches all guilds.");
        return Command.helpCommand(st, "Caching Manager", e);
    }
}
