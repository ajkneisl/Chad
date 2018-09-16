package com.jhobot.handle;

import com.jhobot.commands.info.GuildInfo;
import com.jhobot.commands.info.Jho;
import com.jhobot.commands.info.Steam;
import com.jhobot.commands.info.UserInfo;
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
                break;
            case "guildinfo":
                GuildInfo guildinfo = new GuildInfo();
                if (guildinfo.botHasPermission(e))
                {
                    if (!guildinfo.userHasPermission(e))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        guildinfo.helpCommand(e);
                        return;
                    }

                    guildinfo.onRequest(e, argsList);

                }
                else {
                    return;
                }
                break;
            case "userinfo":
                UserInfo userinfo = new UserInfo();
                if (userinfo.botHasPermission(e))
                {
                    if (!userinfo.userHasPermission(e))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        userinfo.helpCommand(e);
                        return;
                    }

                    userinfo.onRequest(e, argsList);

                }
                else {
                    return;
                }
                break;
            case "steam":
                Steam steam = new Steam();
                if (steam.botHasPermission(e))
                {
                    if (!steam.userHasPermission(e))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        steam.helpCommand(e);
                        return;
                    }

                    steam.onRequest(e, argsList);

                }
                else {
                    return;
                }
                break;
        }
    }
}
