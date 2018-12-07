package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Chad.CachedGuild;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;
import sx.blah.discord.util.RequestBuffer;

/**
 * @author sho
 * @since 0.6.3 B2
 */
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
                embedBuilder.withDesc("Current Cached Guilds `" + Chad.cachedGuilds.size() + "`\n"+
                        "Last Cached in Current Guild `" + Chad.getGuild(e.getGuild().getLongID()).getLastCached() + "`\n");
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            // Adds a switch for extra arguments
            switch (args.get(0).toLowerCase())
            {
                // ReCaches the current guild
                case "recache":
                    Chad.getGuild(e.getGuild().getLongID()).cache();
                    messageHandler.send("ReCached current guild", "Caching Manager");
                    return;
                // ReCaches all guilds
                case "recacheall":
                    List<IGuild> guilds = RequestBuffer.request(e.getClient()::getGuilds).get();
                    Chad.cachedGuilds.clear();
                    guilds.forEach((g) -> Chad.cachedGuilds.put(g.getLongID(), new CachedGuild(g.getLongID())));
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
