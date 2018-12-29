package org.woahoverflow.chad.framework.obj;

import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
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
        DEVELOPER, GAMBLING, PUNISHMENTS, INFO, ADMINISTRATOR, FUN, NSFW, MUSIC, COMMUNITY
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
        private String[] commandAliases = {};
        private final boolean usesAliases;

        /**
         * The constructor for data with aliases
         *
         * @param category The command's category
         * @param commandClass The command's class
         * @param commandAliases The command's aliases
         */
        public Data(Category category, Command.Class commandClass, String... commandAliases)
        {
            this.commandAliases = commandAliases;
            commandCategory = category;
            this.commandClass = commandClass;
            usesAliases = true;
        }

        /**
         * The constructor for data without aliases
         *
         * @param category The command's category
         * @param commandClass The command's class
         */
        public Data(Category category, Command.Class commandClass)
        {
            commandCategory = category;
            this.commandClass = commandClass;
            usesAliases = false;
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

        /**
         * @return The command's aliases
         */
        public String[] getCommandAliases()
        {
            if (!usesAliases())
                return null;

            return commandAliases;
        }

        /**
         * @return If it uses aliases
         */
        public boolean usesAliases()
        {
            return usesAliases;
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
    public static synchronized Runnable helpCommand(HashMap<String, String> commands, String commandName, MessageReceivedEvent messageReceivedEvent)
    {
        return () -> {
            // The guild's prefix
            String prefix = (String) GuildHandler.handle.getGuild(messageReceivedEvent.getGuild().getLongID()).getObject(
                DataType.PREFIX);

            // The embed builder
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Help : `" + commandName + '`');
            commands.forEach((key, val) -> embedBuilder.appendField(String.format("`%s%s`", prefix, key), val, false));

            // Sends it
            new MessageHandler(messageReceivedEvent.getChannel(), messageReceivedEvent.getAuthor()).sendEmbed(embedBuilder);
        };
    }
}
