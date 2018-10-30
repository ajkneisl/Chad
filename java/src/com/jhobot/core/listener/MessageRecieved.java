package com.jhobot.core.listener;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.ThreadCountHandler;
import com.jhobot.handle.commands.CommandData;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

public class MessageRecieved
{
    private HashMap<String, CommandData> COMMANDS = ChadVar.COMMANDS;

    @EventSubscriber
    public void messageRecieved(MessageReceivedEvent e)
    {
        // Gets the message, then splits all the different parts with a space.
        String[] argArray = e.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        String prefix = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "prefix"); // to prevent multiple requests

        // If the prefix isn't jho! it returns
        if (!argArray[0].startsWith(prefix))
            return;

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(prefix.length()).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);

        // If the user
        if (!ChadVar.THREAD_HANDLER.allowThread(e.getAuthor()))
            return;

        COMMANDS.forEach((k, v) -> {
            if (commandString.equalsIgnoreCase(k)) // comment for commit
            {
                Future<?> thread;


                if (v.isDevOnly && !ChadVar.PERMISSION_HANDLER.userIsDeveloper(e.getAuthor()))
                {
                    new MessageHandler(e.getChannel()).sendError("You don't have permission for this!");
                    return;
                }

                if (!ChadVar.PERMISSION_HANDLER.userHasPermission(k, e.getAuthor(), e.getGuild()) && !e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
                {
                    new MessageHandler(e.getChannel()).sendError("You don't have permission for this!");
                    return;
                }
                if (args.size() == 1 && args.get(0).equalsIgnoreCase("help"))
                    thread = ChadVar.EXECUTOR_POOL.submit(v.commandClass.help(e, args));
                else
                    thread = ChadVar.EXECUTOR_POOL.submit(v.commandClass.run(e, args));
                ChadVar.THREAD_HANDLER.addThread(thread, e.getAuthor());
            }
        });
    }
}
