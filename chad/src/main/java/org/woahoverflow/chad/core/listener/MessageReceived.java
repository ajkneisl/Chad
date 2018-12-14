package org.woahoverflow.chad.core.listener;

import java.util.regex.Pattern;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Chad.ThreadConsumer;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.obj.Command.Category;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sx.blah.discord.util.RequestBuffer;

/**
 * Message Received
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public final class MessageReceived
{
    static final Pattern COMPILE = Pattern.compile("&user&");

    /**
     * Discord's Message Received Event
     *
     * @param event Message Received Event
     */
    @EventSubscriber
    @SuppressWarnings("unused")
    public void messageReceived(MessageReceivedEvent event)
    {
        // Gets the message, then splits all the different parts with a space.
        String[] argArray = event.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        Guild guild = GuildHandler.handle.getGuild(event.getGuild().getLongID());

        guild.setObject(DataType.MESSAGES_SENT, ((long) guild.getObject(DataType.MESSAGES_SENT)) + 1L);

        // The guild's prefix
        String prefix = ((String) guild.getObject(Guild.DataType.PREFIX)).toLowerCase();

        // The user's threadconsumer
        ThreadConsumer consumer = Chad.getConsumer(event.getAuthor().getLongID());

        // Makes sure the words aren't swears :) (if enabled)
        if ((boolean) guild.getObject(Guild.DataType.SWEAR_FILTER))
        {
            // Gets the message from the cache :)
            String msg = (String) guild.getObject(Guild.DataType.SWEAR_FILTER_MESSAGE);
            msg = msg != null ? COMPILE.matcher(msg).replaceAll(event.getAuthor().getName()) : "No Swearing!";
            for (String s : argArray) {
                if (ChadVar.swearWords.contains(s.toLowerCase())) {
                    new MessageHandler(event.getChannel(), event.getAuthor()).send(msg, "Swearing");
                    RequestBuffer.request(() -> event.getMessage().delete());
                    return;
                }
            }
        }

        // If the prefix isn't the correct prefix it returns
        if (!argArray[0].toLowerCase().startsWith(prefix))
            return;

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(prefix.length()).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);

        // If the user has 3 threads currently running, deny them
        if (!Chad.consumerRunThread(consumer))
            return;

        // If it's about to run, update statistics
        guild.setObject(DataType.COMMANDS_SENT, ((long) guild.getObject(DataType.COMMANDS_SENT)) + 1L);

        ChadVar.COMMANDS.forEach((key, val) -> {
            if (val.usesAliases())
            {
                for (String alias : val.getCommandAliases()) {
                    if (alias.equalsIgnoreCase(commandString)) {
                        // if the command is developer only, and the user is NOT a developer, deny them access
                        if (val.getCommandCategory() == Category.DEVELOPER && !PermissionHandler.handle.userIsDeveloper(event.getAuthor()))
                        {
                            new MessageHandler(event.getChannel(), event.getAuthor()).sendError("This command is Developer only!");
                            return;
                        }

                        // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                        if (PermissionHandler.handle.userNoPermission(key, event.getAuthor(), event.getGuild()) && !event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR))
                        {
                            new MessageHandler(event.getChannel(), event.getAuthor()).sendError(MessageHandler.USER_NO_PERMISSION);
                            return;
                        }
                        Runnable thread = args.size() == 1 && args.get(0).equalsIgnoreCase("help") ? val.getCommandClass().help(event) : val.getCommandClass().run(event, args);

                        // add the command thread to the handler
                        Chad.runThread(thread, consumer);
                    }
                }
            }

            // If it doesn't use aliases, or none of the aliases were the command
            if (commandString.equalsIgnoreCase(key))
            {
                // if the command is developer only, and the user is NOT a developer, deny them access
                if (val.getCommandCategory() == Category.DEVELOPER && !PermissionHandler.handle.userIsDeveloper(event.getAuthor()))
                {
                    new MessageHandler(event.getChannel(), event.getAuthor()).sendError("This command is Developer only!");
                    return;
                }

                // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                if (PermissionHandler.handle.userNoPermission(key, event.getAuthor(), event.getGuild()) && !event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR))
                {
                    new MessageHandler(event.getChannel(), event.getAuthor()).sendError(MessageHandler.USER_NO_PERMISSION);
                    return;
                }
                Runnable thread = args.size() == 1 && args.get(0).equalsIgnoreCase("help") ? val.getCommandClass().help(event) : val.getCommandClass().run(event, args);

                // add the command thread to the handler
                Chad.runThread(thread, consumer);
            }
        });
    }
}
