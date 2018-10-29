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

public class nbgonewild implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Gone Wild");
            b.withImage(ChadBot.JSON_HANDLER.read("https://nekobot.xyz/api/image?type=gonewild").getString("message"));
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("gonewild", "nsfw gonewild");
        return HelpHandler.helpCommand(st, "Gone Wild", e);
    }
}
