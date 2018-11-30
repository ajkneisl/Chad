package org.woahoverflow.chad.framework;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

// Utility for all Chad commands
public final class Command
{
    // The command categories
    public enum Category
    {
        MONEY, ADMIN, PUNISHMENTS, INFO, FUNCTION, FUN, NSFW
    }

    // The class all of the Chad commands implement from
    public interface Class
    {
        // When the command is run
        Runnable run(MessageReceivedEvent e, List<String> args);
        // When the command is run with help with the only argument
        Runnable help(MessageReceivedEvent e);
    }

    // The command's data
    public static class Data
    {
        // The command's category
        private final Category commandCategory;
        private final Command.Class commandClass;

        // Constructor
        public Data(Category category, Command.Class commandClass)
        {
            commandCategory = category;
            this.commandClass = commandClass;
        }

        // get the command's category
        public Category getCommandCategory() {
            return commandCategory;
        }

        // get the command's class
        public Command.Class getCommandClass() {
            return commandClass;
        }
    }

    // Generates the help command with a hash map
    public static Runnable helpCommand(HashMap<String, String> commands, String commandName, MessageReceivedEvent e)
    {
        return () -> {
            String prefix = DatabaseHandler.handle.getString(e.getGuild(), "prefix");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Help : " + commandName);
            commands.forEach((key, val) -> embedBuilder.appendField(prefix+key, val, false));
            embedBuilder.withFooterText(Util.getTimeStamp());
            embedBuilder.withColor(new Color(new SecureRandom().nextFloat(), new SecureRandom().nextFloat(), new SecureRandom().nextFloat()));
            new MessageHandler(e.getChannel()).sendEmbed(embedBuilder);
        };
    }

}
