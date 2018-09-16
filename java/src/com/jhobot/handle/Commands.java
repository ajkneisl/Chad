package com.jhobot.handle;

import com.jhobot.commands.info.Jho;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands
{
    public static void call(MessageReceivedEvent e)
    {
        // Gets the message, then splits all the different parts with a space.
        String[] argArray = e.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        // If the prefix isn't jho! it returns
        if (!argArray[0].startsWith("jho!"))
            return;

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(4).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0);

        // switch for commands
        switch (commandString)
        {
            case "jho":
                Jho jho = new Jho();
                if (jho.botHasPermission(e))
                {
                    if (!jho.userHasPermission(e))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        jho.helpCommand(e);
                        return;
                    }

                    jho.onRequest(e, argsList);

                }
                else {
                    return;
                }
        }
    }
}
