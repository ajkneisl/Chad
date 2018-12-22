package org.woahoverflow.chad.commands.function;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Filters through swears
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class Swearing implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            Guild guild = GuildHandler.handle.getGuild(e.getGuild().getLongID());

            // if there's no arguments, give statistics
            if (args.isEmpty())
            {
                // creates an embed builder and applies values
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withTitle("Swear Filter");

                String status = (boolean) guild.getObject(Guild.DataType.SWEAR_FILTER) ? "enabled" :  "disabled";

                embedBuilder.withDesc("Swearing in this guild is `"+status+"`.");

                // send
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            if (args.size() == 1 && args.get(0).equalsIgnoreCase("on") || args.get(0).equalsIgnoreCase("off"))
            {
                // actual boolean value
                boolean toggle = args.get(0).equalsIgnoreCase("on");

                // good looking value
                String toggleString = toggle ? "enabled" : "disabled";

                // sets in database
                GuildHandler.handle.getGuild(e.getGuild().getLongID()).setObject(DataType.SWEAR_FILTER, toggle);

                // the message
                String message = toggle ? "Swear filtering has been `"+toggleString+"`.\n\n"
                    + "Keep in mind that the swear filter isn't always accurate.\nSome words may be blocked due to having a swear word in them,\n or some may be unblocked due to "
                    + "having an odd combination of different letters.\nIf you find a word that shouldn't/should be blocked, please tell us." : "Swear filtering has been `"+toggleString+"`.";

                // sends message
                messageHandler.sendEmbed(new EmbedBuilder().withDesc(message));
                return;
            }

            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("swearfilter <on/off>", "Toggles the swear filter.");
        st.put("swearfilter", "Gets the status of the swear filter.");
        return Command.helpCommand(st, "Swear Filter", e);
    }
}
