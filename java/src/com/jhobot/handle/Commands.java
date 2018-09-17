package com.jhobot.handle;

import com.jhobot.commands.fun.EightBall;
import com.jhobot.commands.function.Prefix;
import com.jhobot.commands.info.GuildInfo;
import com.jhobot.commands.info.Jho;
import com.jhobot.commands.info.Steam;
import com.jhobot.commands.info.UserInfo;
import com.jhobot.commands.punishments.Ban;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands
{
    public static void call(MessageReceivedEvent e)
    {
        // database instance, so it doesn't create multiple and slow everything down
        DB db = new DB(JSON.get("uri_link"));

        // Gets the message, then splits all the different parts with a space.
        String[] argArray = e.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        // If the prefix isn't jho! it returns
        if (!argArray[0].startsWith(db.getString(e.getGuild(), "prefix")))
            return;

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(db.getString(e.getGuild(), "prefix").length()).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0);

        // switch for commands
        switch (commandString)
        {
            case "jho":
                Jho jho = new Jho();
                if (jho.botHasPermission(e, db))
                {
                    if (!jho.userHasPermission(e, db))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        jho.helpCommand(e, db);
                        return;
                    }

                    jho.onRequest(e, argsList, db);

                }
                else {
                    return;
                }
                break;
            case "guildinfo":
                GuildInfo guildinfo = new GuildInfo();
                if (guildinfo.botHasPermission(e, db))
                {
                    if (!guildinfo.userHasPermission(e, db))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        guildinfo.helpCommand(e, db);
                        return;
                    }

                    guildinfo.onRequest(e, argsList, db);

                }
                else {
                    return;
                }
                break;
            case "userinfo":
                UserInfo userinfo = new UserInfo();
                if (userinfo.botHasPermission(e, db))
                {
                    if (!userinfo.userHasPermission(e, db))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        userinfo.helpCommand(e, db);
                        return;
                    }

                    userinfo.onRequest(e, argsList, db);

                }
                else {
                    return;
                }
                break;
            case "steam":
                Steam steam = new Steam();
                if (steam.botHasPermission(e, db))
                {
                    if (!steam.userHasPermission(e, db))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        steam.helpCommand(e, db);
                        return;
                    }

                    steam.onRequest(e, argsList, db);

                }
                else {
                    return;
                }
                break;
            case "prefix":
                Prefix prefix = new Prefix();
                if (prefix.botHasPermission(e, db))
                {
                    if (!prefix.userHasPermission(e, db))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        prefix.helpCommand(e, db);
                        return;
                    }

                    prefix.onRequest(e, argsList, db);

                }
                else {
                    return;
                }
                break;
            case "ban":
                Ban ban = new Ban();
                if (ban.botHasPermission(e, db))
                {
                    if (!ban.userHasPermission(e, db))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        ban.helpCommand(e, db);
                        return;
                    }

                    ban.onRequest(e, argsList, db);

                }
                else {
                    return;
                }
                break;
            case "8ball":
                EightBall ball = new EightBall();
                if (ball.botHasPermission(e, db))
                {
                    if (!ball.userHasPermission(e, db))
                    {
                        new Messages(e.getChannel()).sendError("You don't have permissions for this!");
                        return;
                    }

                    if (argsList.size() == 1 && argsList.get(0).equalsIgnoreCase("help"))
                    {
                        ball.helpCommand(e, db);
                        return;
                    }

                    ball.onRequest(e, argsList, db);

                }
                else {
                    return;
                }
                break;
        }
    }
}
