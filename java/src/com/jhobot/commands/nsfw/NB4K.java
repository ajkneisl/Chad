package com.jhobot.commands.nsfw;

import com.jhobot.core.ChadBot;
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

public class NB4K implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            if (!e.getChannel().isNSFW())
            {
                new MessageHandler(e.getChannel()).sendError("This isn't an NSFW channel!");
                return;
            }
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("4k Pornography [NSFW]");
            b.withImage(ChadBot.JSON_HANDLER.read("https://nekobot.xyz/api/image?type=4k").getString("message"));
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("4k", "Gets 4k Pornographic Images");
        return HelpHandler.helpCommand(st, "4k", e);
    }
}