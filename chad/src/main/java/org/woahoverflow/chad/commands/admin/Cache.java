package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

public class Cache implements Command.Class{
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // If there's no arguments, show regular stats
            if (args.isEmpty())
            {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withTitle("Cache Status");
                embedBuilder.withDesc("Current Cached Guilds `" + ChadVar.GUILD_CACHE.size() + "`\n"+
                        "Last Cached in Current Guild `" + ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).lastCached() + "`\n");
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            // Adds a switch for extra arguments
            switch (args.get(0).toLowerCase())
            {
                // ReCaches the current guild
                case "recache":
                    ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());
                    messageHandler.send("ReCached current guild", "Caching Manager");
                    return;
                // ReCaches all guilds
                case "recacheall":
                    ChadVar.CACHE_DEVICE.reCacheAll();
                    messageHandler.send("ReCached all guilds", "Caching Manager");
                    return;

                // if no other arguments were met, error
                default:
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
            }
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("cache", "Giving information about caching.");
        st.put("cache recache", "ReCaches current guild.");
        st.put("cache recacheall", "ReCaches all guilds.");
        return Command.helpCommand(st, "Caching Manager", e);
    }
}
