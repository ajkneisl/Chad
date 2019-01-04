package org.woahoverflow.chad.core.listener;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Chad.ThreadConsumer;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import org.woahoverflow.chad.framework.obj.Command.Category;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Guild.DataType;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message Received
 *
 * @author sho, codebasepw
 */
public final class MessageReceived {
    /**
     * Discord's Message Received Event
     *
     * @param event Message Received Event
     */
    @EventSubscriber
    @SuppressWarnings("unused")
    public void messageReceived(MessageReceivedEvent event) {
        // Gets the message, then splits all the different parts with a space.
        String[] argArray = event.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        // The guild's guild instance
        Guild guild = GuildHandler.handle.getGuild(event.getGuild().getLongID());

        // Update their statistics
        guild.messageSent();

        // The guild's prefix
        String prefix = ((String) guild.getObject(DataType.PREFIX)).toLowerCase();

        // Makes sure the words aren't swears :) (if enabled)
        if ((boolean) guild.getObject(DataType.SWEAR_FILTER)) {
            // Builds together the message & removes the special characters
            String character = String.join("", argArray);
            Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
            Matcher match = pt.matcher(character);
            while (match.find()) {
                character=character.replaceAll('\\' + match.group(), "");
            }

            // Checks if the word contains a swear word
            for (String swearWord : ChadVar.swearWords) {
                // Ass is a special case, due to words like `bass`
                if (swearWord.equalsIgnoreCase("ass") && character.contains("ass")) {
                    // Goes through all of the arguments
                    for (String argument : argArray) {
                        // If the argument is just ass
                        if (argument.equalsIgnoreCase("ass")) {
                            // Delete it
                            RequestBuffer.request(event.getMessage()::delete);
                            return;
                        }
                    }
                    continue;
                }

                // If it contains any other swear word, delete it
                if (character.toLowerCase().contains(swearWord)) {
                    RequestBuffer.request(event.getMessage()::delete);
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

        // The user's threadconsumer
        ThreadConsumer consumer = Chad.getConsumer(event.getAuthor().getLongID());

        // If the user has 3 threads currently running, deny them
        if (!Chad.consumerRunThread(consumer))
            return;

        // If it's about to run, update statistics
        guild.commandSent();

        ChadVar.COMMANDS.forEach((key, val) -> {
            if (val.usesAliases()) {
                for (String alias : val.getCommandAliases()) {
                    if (alias.equalsIgnoreCase(commandString)) {
                        // if the command is developer only, and the user is NOT a developer, deny them access
                        if (val.getCommandCategory() == Category.DEVELOPER && !PermissionHandler.handle.userIsDeveloper(event.getAuthor())) {
                            new MessageHandler(event.getChannel(), event.getAuthor()).sendError("This command is Developer only!");
                            return;
                        }

                        // if the category is disabled
                        if (((ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES)).contains(val.getCommandCategory().toString().toLowerCase())) {
                            return;
                        }

                        // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                        if (PermissionHandler.handle.userNoPermission(key, event.getAuthor(), event.getGuild()) && !event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR)) {
                            new MessageHandler(event.getChannel(), event.getAuthor()).sendPresetError(MessageHandler.Messages.USER_NO_PERMISSION);
                            return;
                        }
                        Runnable thread = args.size() == 1 && args.get(0).equalsIgnoreCase("help") ? val.getCommandClass().help(event) : val.getCommandClass().run(event, args);

                        // add the command thread to the handler
                        Chad.runThread(thread, consumer);
                    }
                }
            }

            // If it doesn't use aliases, or none of the aliases were the command
            if (commandString.equalsIgnoreCase(key)) {
                // if the command is developer only, and the user is NOT a developer, deny them access
                if (val.getCommandCategory() == Category.DEVELOPER && !PermissionHandler.handle.userIsDeveloper(event.getAuthor())) {
                    new MessageHandler(event.getChannel(), event.getAuthor()).sendError("This command is Developer only!");
                    return;
                }

                // if the category is disabled
                if (((ArrayList<String>) guild.getObject(DataType.DISABLED_CATEGORIES)).contains(val.getCommandCategory().toString().toLowerCase())) {
                    return;
                }

                // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                if (PermissionHandler.handle.userNoPermission(key, event.getAuthor(), event.getGuild()) && !event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR)) {
                    new MessageHandler(event.getChannel(), event.getAuthor()).sendPresetError(MessageHandler.Messages.USER_NO_PERMISSION);
                    return;
                }

                Runnable thread = args.size() == 1 && args.get(0).equalsIgnoreCase("help") ? val.getCommandClass().help(event) : val.getCommandClass().run(event, args);

                // add the command thread to the handler
                Chad.runThread(thread, consumer);
            }
        });
    }
}
