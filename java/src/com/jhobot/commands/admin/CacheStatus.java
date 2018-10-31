package com.jhobot.commands.admin;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

public class CacheStatus implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Cache Status");
            b.withDesc("Current Cached Guilds `" + ChadVar.GUILD_CACHE.size() + "`\n"+
                    "Last Cached in Current Guild `" + ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).lastCached() + "`\n");
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
