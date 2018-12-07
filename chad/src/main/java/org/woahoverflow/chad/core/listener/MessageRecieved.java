package org.woahoverflow.chad.core.listener;

import java.util.regex.Pattern;
import org.bson.Document;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Chad.ThreadConsumer;
import org.woahoverflow.chad.framework.Command.Category;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
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
public final class MessageRecieved
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

        // The guild's cached document
        Document cachedDocument = Chad.getGuild(event.getGuild().getLongID()).getDocument();

        // The guild's prefix
        String prefix = cachedDocument.getString("prefix").toLowerCase();

        // The user's threadconsumer
        ThreadConsumer consumer = Chad.getConsumer(event.getAuthor().getLongID());

        // Makes sure the words aren't swears :) (if enabled)
        if (cachedDocument.getBoolean("stop_swear"))
        {
            // Gets the message from the cache :)
            String msg = cachedDocument.getString("swear_message");
            msg = msg != null ? COMPILE.matcher(msg).replaceAll(event.getAuthor().getName()) : "No Swearing!";
            for (String s : argArray) {
                if (ChadVar.swearWords.contains(s.toLowerCase())) {
                    new MessageHandler(event.getChannel()).send(msg, "Swearing");
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

        ChadVar.COMMANDS.forEach((key, val) -> {
            if (val.usesAliases())
            {
                for (String alias : val.getCommandAliases()) {
                    if (alias.equalsIgnoreCase(commandString)) {
                        // if the command is developer only, and the user is NOT a developer, deny them access
                        if (val.getCommandCategory() == Category.ADMIN && !PermissionHandler.handle.userIsDeveloper(event.getAuthor()))
                        {
                            new MessageHandler(event.getChannel()).sendError("This command is Developer only!");
                            return;
                        }

                        // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                        if (!PermissionHandler.handle.userHasPermission(key, event.getAuthor(), event.getGuild()) && !event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR))
                        {
                            new MessageHandler(event.getChannel()).sendError("You don't have permission for this command!");
                            return;
                        }
                        Runnable thread = args.size() == 1 && args.get(0).equalsIgnoreCase("help") ? val.getCommandClass().help(event) : val.getCommandClass().run(event, args);

                        // add the command thread to the handler
                        Chad.runThread(thread, consumer);
                    }
                }
            }
            else if (commandString.equalsIgnoreCase(key))
            {
                // if the command is developer only, and the user is NOT a developer, deny them access
                if (val.getCommandCategory() == Category.ADMIN && !PermissionHandler.handle.userIsDeveloper(event.getAuthor()))
                {
                    new MessageHandler(event.getChannel()).sendError("Oh noes! It looks like you're not a developer. Too bad, ain't it?");
                    return;
                }

                // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                if (!PermissionHandler.handle.userHasPermission(key, event.getAuthor(), event.getGuild()) && !event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR))
                {
                    new MessageHandler(event.getChannel()).sendError("You don't have permission for this command!");
                    return;
                }
                Runnable thread = args.size() == 1 && args.get(0).equalsIgnoreCase("help") ? val.getCommandClass().help(event) : val.getCommandClass().run(event, args);

                // add the command thread to the handler
                Chad.runThread(thread, consumer);
            }
        });
    }
}
