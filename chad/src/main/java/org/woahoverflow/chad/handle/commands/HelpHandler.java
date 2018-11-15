package org.woahoverflow.chad.handle.commands;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class HelpHandler
{
    public static Runnable helpCommand(HashMap<String, String> cmds, String commandName, MessageReceivedEvent e)
    {
        return () -> {
            String prefix = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "prefix");
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : " + commandName);
            cmds.forEach((k, v) -> b.appendField(prefix+k, v, false));
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }
}
