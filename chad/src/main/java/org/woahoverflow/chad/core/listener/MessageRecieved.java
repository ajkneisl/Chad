package org.woahoverflow.chad.core.listener;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.core.ChadVar;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

public class MessageRecieved
{
    @SuppressWarnings("unused")
    @EventSubscriber
    public void messageRecieved(MessageReceivedEvent e)
    {
        // Gets the message, then splits all the different parts with a space.
        String[] argArray = e.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        String prefix = ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc().getString("prefix");

        // If the prefix isn't jho! it returns
        if (!argArray[0].startsWith(prefix))
            return;

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(prefix.length()).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);

        // If the user
        if (!ChadVar.THREAD_DEVICE.allowThread(e.getAuthor()))
            return;

        ChadVar.COMMANDS.forEach((k, v) -> {
            if (commandString.equalsIgnoreCase(k))
            {
                Future<?> thread;

                // if the command is developer only, and the user is NOT a developer, deny them access
                if (v.isDevOnly && !ChadVar.PERMISSION_DEVICE.userIsDeveloper(e.getAuthor()))
                {
                    new MessageHandler(e.getChannel()).sendError("This command is developer only!");
                    return;
                }

                // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
                if (!ChadVar.PERMISSION_DEVICE.userHasPermission(k, e.getAuthor(), e.getGuild()) && !e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
                {
                    new MessageHandler(e.getChannel()).sendError("You don't have permission for this command!");
                    return;
                }

                // if there is only 1 argument, and its equal to "help", show the commands help information
                if (args.size() == 1 && args.get(0).equalsIgnoreCase("help"))
                    thread = ChadVar.EXECUTOR_POOL.submit(v.commandClass.help(e));
                else // otherwise, run the command
                    thread = ChadVar.EXECUTOR_POOL.submit(v.commandClass.run(e, args));
                // add the command thread to the handler
                ChadVar.THREAD_DEVICE.addThread(thread, e.getAuthor());
            }
        });
    }
}
