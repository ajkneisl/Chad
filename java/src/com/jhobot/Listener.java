package com.jhobot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Listener
{
    @EventSubscriber
    public void messageRecieved(MessageReceivedEvent e)
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
            case "tictactoe":
                // do stuff
                break;
        }
    }
}
