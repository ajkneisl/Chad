package org.woahoverflow.chad.framework;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * The command utility for Chad Bot
 *
 * @author sho, codebasepw
 * @since 0.6.3-B2
 */
public final class Command
{

    /**
     * Categories for Commands
     */
    public enum Category
    {
        ADMIN, MONEY, PUNISHMENTS, INFO, FUNCTION, FUN, NSFW, MUSIC
    }

    /**
     * The class the commands implement from.
     */
    public interface Class
    {
        // When the command is run
        Runnable run(MessageReceivedEvent e, List<String> args);
        // When the command is run with help with the only argument
        Runnable help(MessageReceivedEvent e);
    }

    /**
     * The command's data
     */
    public static class Data
    {
        // The command's category
        private final Category commandCategory;
        private final Command.Class commandClass;

        /**
         * The constructor for data
         *
         * @param category The command's category
         * @param commandClass The command's class
         */
        public Data(Category category, Command.Class commandClass)
        {
            commandCategory = category;
            this.commandClass = commandClass;
        }

        /**
         * @return The command's category
         */
        public Category getCommandCategory() {
            return commandCategory;
        }

        /**
         * @return The command's class
         */
        public Command.Class getCommandClass() {
            return commandClass;
        }
    }

    /**
     * Generates a help command
     *
     * @param commands The command's hashmap with their description
     * @param commandName The command's name
     * @param messageReceivedEvent The messagerecievedevent
     * @return The help runnable
     */
    public static Runnable helpCommand(HashMap<String, String> commands, String commandName, MessageReceivedEvent messageReceivedEvent)
    {
        return () -> {
            String prefix = DatabaseHandler.handle.getString(messageReceivedEvent.getGuild(), "prefix");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Help : " + commandName);
            commands.forEach((key, val) -> embedBuilder.appendField(prefix+key, val, false));
            embedBuilder.withFooterText(Util.getTimeStamp());
            embedBuilder.withColor(new Color(new SecureRandom().nextFloat(), new SecureRandom().nextFloat(), new SecureRandom().nextFloat()));
            new MessageHandler(messageReceivedEvent.getChannel()).sendEmbed(embedBuilder);
        };
    }
}
