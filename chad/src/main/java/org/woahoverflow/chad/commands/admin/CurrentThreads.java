package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CurrentThreads implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            EmbedBuilder b = new EmbedBuilder();
            b.withFooterText(Util.getTimeStamp());

            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withTitle("Current Threads Running");
            ChadVar.THREAD_HANDLER.getMap().forEach((k, v) -> b.appendField(k.getName() + " [" + k.getLongID() + "]", Integer.toString(v.size()), false));
            m.sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("threads", "Displays all running threads for users.");
        return HelpHandler.helpCommand(st, "Current Threads", e);
    }

}
