package com.jhobot.handle.commands;

import com.jhobot.commands.fun.*;
import com.jhobot.commands.function.Logging;
import com.jhobot.commands.function.Prefix;
import com.jhobot.commands.info.*;
import com.jhobot.commands.punishments.Ban;
import com.jhobot.commands.punishments.Kick;
import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands {
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
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);

        // command runner

        switch (commandString)
        {
            case "catfact":
                runCommand(db, e, args, new CatFact());
                break;
            case "catgallery":
                runCommand(db, e, args, new CatGallery());
                break;
            case "8ball":
                runCommand(db, e, args, new EightBall());
                break;
            case "random":
                runCommand(db, e, args, new Random());
                break;
            case "logging":
                runCommand(db, e, args, new Logging());
                break;
            case "prefix":
                runCommand(db, e, args, new Prefix());
                break;
            case "guildinfo":
                runCommand(db, e, args, new GuildInfo());
                break;
            case "jho":
                runCommand(db, e, args, new Jho());
                break;
            case "steam":
                runCommand(db, e, args, new Steam());
                break;
            case "updatelog":
                runCommand(db, e, args, new UpdateLog());
                break;
            case "userinfo":
                runCommand(db, e, args, new UserInfo());
                break;
            case "ban":
                runCommand(db, e, args, new Ban());
                break;
            case "kick":
                runCommand(db, e, args, new Kick());
                break;
            case "pe":
                runCommand(db, e, args, new PhotoEditor());
                break;
        }
    }

    private static void runCommand(DB db, MessageReceivedEvent e, List<String> args, CommandClass c)
    {
        Messages m = new Messages(e.getChannel());
        if (c.botHasPermission(e, db))
        {
            if (!c.botHasPermission(e, db))
            {
                m.sendError("You don't have permissions for this!");
                return;
            }

            if (args.size() == 1 && args.get(0).equalsIgnoreCase("help"))
            {
                c.helpCommand(e, db);
                return;
            }

            c.onRequest(e, args, db);
        }
    }
}
