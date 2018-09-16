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
        String[] argArray = e.getMessage().getContent().split(" ");

        if (argArray.length == 0)
            return;

        if (!argArray[0].startsWith("sho!"))
            return;

        String commandString = argArray[0].substring(4).toLowerCase();

        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0);

        switch (commandString)
        {
            case "tictactoe":
                // do stuff
                break;
        }
    }
}
