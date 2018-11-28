package org.woahoverflow.chad.handle.commands;

import java.awt.Color;
import java.security.SecureRandom;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

public final class Command
{
    // Generates the help command with a hash map
    public static Runnable helpCommand(HashMap<String, String> commands, String commandName, MessageReceivedEvent e)
    {
        return () -> {
            String prefix = ChadVar.databaseDevice.getString(e.getGuild(), "prefix");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Help : " + commandName);
            commands.forEach((key, val) -> embedBuilder.appendField(prefix+key, val, false));
            embedBuilder.withFooterText(Util.getTimeStamp());
            embedBuilder.withColor(new Color(new SecureRandom().nextFloat(), new SecureRandom().nextFloat(), new SecureRandom().nextFloat()));
            new MessageHandler(e.getChannel()).sendEmbed(embedBuilder);
        };
    }

    public enum Category
    {
        ADMIN, FUN, FUNCTION, INFO, PUNISHMENTS, NSFW, MONEY
    }

    public interface Class
    {
        Runnable run(MessageReceivedEvent e, List<String> args);
        Runnable help(MessageReceivedEvent e);
    }

    // The command's data
    public static class Data
    {
        public final Category commandCategory;
        public final boolean isDeveloperOnly;
        public final Command.Class commandClass;

        public Data(Category commandCategory, boolean isDeveloperOnly, Command.Class commandClass) {
            this.commandCategory = commandCategory;
            this.isDeveloperOnly = isDeveloperOnly;
            this.commandClass = commandClass;
        }
    }
}
