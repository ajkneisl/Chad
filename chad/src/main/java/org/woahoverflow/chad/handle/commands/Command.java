package org.woahoverflow.chad.handle.commands;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Command
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
    public enum Category
    {
        ADMIN, FUN, FUNCTION, INFO, PUNISHMENTS, NSFW
    }
    public interface Class
    {
        Runnable run(MessageReceivedEvent e, List<String> args);
        Runnable help(MessageReceivedEvent e, List<String> args);
    }

    public static class Data
    {
        public final Category category;
        public final boolean isDevOnly;
        public final Command.Class commandClass;

        public Data(Category category, boolean isDevOnly, Command.Class commandClass) {
            this.category = category;
            this.isDevOnly = isDevOnly;
            this.commandClass = commandClass;
        }
    }
}
