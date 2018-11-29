package org.woahoverflow.chad.core.listener;

import java.util.regex.Pattern;
import org.woahoverflow.chad.handle.CachingHandler;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.core.ChadVar;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import sx.blah.discord.util.RequestBuffer;

public final class MessageRecieved
{

    public static final Pattern COMPILE = Pattern.compile("&user&");

    @SuppressWarnings("unused")
    @EventSubscriber
    public void messageReceived(MessageReceivedEvent e)
    {
        // Gets the message, then splits all the different parts with a space.
        String[] argArray = e.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0) {
            return;
        }

        String prefix = CachingHandler.getGuild(e.getGuild()).getDoc().getString("prefix");

        // Makes sure the words aren't swears :) (if enabled)
        if (CachingHandler.getGuild(e.getGuild()).getDoc().getBoolean("stop_swear"))
        {
            // Gets the message from the cache :)
            String msg = CachingHandler.getGuild(e.getGuild()).getDoc().getString("swear_message");
            msg = msg != null ? COMPILE.matcher(msg).replaceAll(e.getAuthor().getName()) : "No Swearing!";
            for (String s : argArray) {
                if (ChadVar.swearWords.contains(s.toLowerCase())) {
                    new MessageHandler(e.getChannel()).send(msg, "Swearing");
                    RequestBuffer.request(() -> e.getMessage().delete());
                    return;
                }
            }
        }

        // If the prefix isn't the correct prefix it returns
        if (!argArray[0].startsWith(prefix.toLowerCase())) {
            return;
        }

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(prefix.length()).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);

        // If the user has 3 threads currently running, deny them
        if (!ChadVar.threadDevice.canRun(e.getAuthor())) {
            return;
        }

        ChadVar.COMMANDS.forEach((key, val) -> {
            if (commandString.equalsIgnoreCase(key))
            {

                // if the command is developer only, and the user is NOT a developer, deny them access
                if (val.isDeveloperOnly && !ChadVar.permissionDevice.userIsDeveloper(e.getAuthor()))
                {
                    new MessageHandler(e.getChannel()).sendError("This command is developer only!");
                    return;
                }

                // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                if (!ChadVar.permissionDevice.userHasPermission(key, e.getAuthor(), e.getGuild()) && !e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
                {
                    new MessageHandler(e.getChannel()).sendError("You don't have permission for this command!");
                    return;
                }

                Future<?> thread = args.size() == 1 && args.get(0).equalsIgnoreCase("help") ? ChadVar.EXECUTOR_POOL.submit(val.commandClass.help(e)) : ChadVar.EXECUTOR_POOL.submit(val.commandClass.run(e, args));
                // add the command thread to the handler
                ChadVar.threadDevice.addThread(thread, e.getAuthor());
            }
        });
    }
}
