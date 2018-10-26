package com.jhobot.handle.ui;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class StatsHandler
{
    public static HashMap<String, String> getStats(IDiscordClient cli)
    {
        HashMap<String, String> hashmap = new HashMap<>();

        List<IGuild> guilds = RequestBuffer.request(cli::getGuilds).get();

        int total = 0;
        int guild = 0;
        int bots = 0;
        int users = 0;
        int biggestGuildAmount = 0;
        String biggestGuildName = "";
        for (IGuild g : guilds)
        {
            if (g.getUsers().size() > biggestGuildAmount)
            {
                biggestGuildName = g.getName();
                biggestGuildAmount = g.getUsers().size();
            }
            total += g.getUsers().size();
            for (IUser u : g.getUsers())
            {
                if (u.isBot())
                    bots++;
                else
                    users++;
            }
            guild++;
        }

        hashmap.put("avgGuildSize", Integer.toString(total/guild));
        hashmap.put("biggestGuild", biggestGuildName + "("+biggestGuildAmount+")");
        hashmap.put("botToPlayer", Integer.toString(bots)+"/"+Integer.toString(users));
        hashmap.put("guildAmount", Integer.toString(guilds.size()));
        return hashmap;
    }
}
