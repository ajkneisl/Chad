package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Sets the prefix for the guild
 *
 * @author sho
 */
public class Prefix implements Command.Class  {
    @Override
    public final Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // The guild's database instance
            Guild guild = GuildHandler.handle.getGuild(e.getGuild().getLongID());

            // If there's no arguments, show the prefix
            if (args.isEmpty()) {
                // Sends
                messageHandler.sendEmbed(new EmbedBuilder()
                    .withDesc((String) guild.getObject(DataType.PREFIX))
                );
                return;
            }

            // If the arguments are 2, set the prefix
            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set")) {
                // Gets the current prefix
                String prefix = (String) guild.getObject(Guild.DataType.PREFIX);

                // The new prefix
                final String newPrefix = args.get(1);

                // Makes sure the prefix isn't over 6 characters long
                if (newPrefix.length() > 6) {
                    messageHandler.sendError("Prefix can't be over 6 characters long!");
                    return;
                }

                // Sends the log
                MessageHandler.sendConfigLog("Prefix", newPrefix, prefix, e.getAuthor(), e.getGuild());

                // Sets the prefix in the database
                guild.setObject(DataType.PREFIX, newPrefix);

                // Sends a the message
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Your prefix is now `" + newPrefix + "`."));
                return;
            }

            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, guild.getObject(DataType.PREFIX) + "prefix set **new prefix**");
        };
    }

    @Override
    public final Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("prefix", "Your prefix.");
        st.put("prefix set <string>", "Sets the prefix.");
        return Command.helpCommand(st, "Prefix", e);
    }
}
