package com.jhobot.handle.commands;

import com.jhobot.JhoBot;
import com.jhobot.commands.fun.*;
import com.jhobot.commands.function.Logging;
import com.jhobot.commands.function.Prefix;
import com.jhobot.commands.info.*;
import com.jhobot.commands.punishments.Ban;
import com.jhobot.commands.punishments.Kick;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Commands {
    public static void call(MessageReceivedEvent e)
    {

        // Gets the message, then splits all the different parts with a space.
        String[] argArray = e.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        // If the prefix isn't jho! it returns
        if (!argArray[0].startsWith(JhoBot.db.getString(e.getGuild(), "prefix")))
            return;

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(JhoBot.db.getString(e.getGuild(), "prefix").length()).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);

        // command runner
        HashMap<String, Command> hash = new HashMap<>();

        hash.put("userinfo", new UserInfo());
        hash.put("kick", new Kick());
        hash.put("ban", new Ban());
        hash.put("updatelog", new UpdateLog());
        hash.put("steam", new Steam());
        hash.put("jho", new Jho());
        hash.put("guildinfo", new GuildInfo());
        hash.put("bugreport", new BugReport());
        hash.put("prefix", new Prefix());
        hash.put("logging", new Logging());
        hash.put("random", new Random());
        hash.put("pe", new PhotoEditor());
        hash.put("8ball", new EightBall());
        hash.put("catgallery", new CatGallery());
        hash.put("catfact", new CatFact());
        hash.put("help", new Help());
        hash.put("rrl", new RussianRoulette());
        hash.forEach((k, v) -> {
            if (commandString.equalsIgnoreCase(k))
            {
                if (args.size() == 1 && args.get(0).equalsIgnoreCase("help"))
                    JhoBot.exec.execute(v.help(e, args));
                else
                    JhoBot.exec.execute(v.run(e, args));
            }
        });
    }
}
