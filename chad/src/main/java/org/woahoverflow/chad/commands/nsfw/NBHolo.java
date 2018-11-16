package org.woahoverflow.chad.commands.nsfw;

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

public class NBHolo implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            if (!e.getChannel().isNSFW())
            {
                new MessageHandler(e.getChannel()).sendError("This isn't an NSFW channel!");
                return;
            }
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Holo [NSFW]");
            b.withImage(ChadVar.JSON_HANDLER.read("https://nekobot.xyz/api/image?type=holo").getString("message"));
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("holo", "Gets NFSW Holo images");
        return Command.helpCommand(st, "holo", e);
    }
}
