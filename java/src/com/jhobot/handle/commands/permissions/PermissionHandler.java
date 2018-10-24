package com.jhobot.handle.commands.permissions;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.commands.Command;
import org.bson.BsonArray;
import org.bson.Document;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionHandler
{
    public static PermissionHandler HANDLER = new PermissionHandler();
    private ArrayList<String> CMD;
    public PermissionHandler()
    {
        this.CMD = new ArrayList<>();
        String[] cm = {"catfact", "catgallery", "eightball", "photoeditor", "random", "russianroulette", "logging", "im", "prefix", "guildinfo", "help", "redditnew", "reddittop", "steam", "updatelog", "userinfo", "kick", "ban"};
        this.CMD.addAll(Arrays.asList(cm));
    }

    public boolean userHasPermission(String command, IUser user, IGuild g)
    {
        for (IRole r : user.getRolesForGuild(g))
        {
            if (JhoBot.DATABASE_HANDLER.getArray(g, r.getStringID()) != null)
            {
                if (JhoBot.DATABASE_HANDLER.getArray(g, r.getStringID()).contains(command))
                    return true;
            }
        }
        return false;
    }

    public boolean addCommandToRole(IRole role, String... commands)
    {
        if (!parseCommands(commands))
            return false;

        if (JhoBot.DATABASE_HANDLER.getString(role.getGuild(), role.getStringID()) == null)
        {
            Document get = JhoBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return false;

            ArrayList<String> ar = new ArrayList<>(Arrays.asList(commands));
            JhoBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), Arrays.asList(commands))));
            return true;
        }
        else {
            Document get = JhoBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return false;

            JhoBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), JhoBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).addAll(Arrays.asList(commands)))));
            return true;
        }
    }

    public boolean removeCommandFromRole(IRole role, String... commands)
    {
        if (!parseCommands(commands))
            return false;

        if (JhoBot.DATABASE_HANDLER.getString(role.getGuild(), role.getStringID()) == null)
            return false;
        else {
            Document get = JhoBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return false;

            JhoBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), JhoBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).removeAll(Arrays.asList(commands)))));
            return true;
        }

    }

    private boolean parseCommands(String... args)
    {
        List<Boolean> l = new ArrayList<>();
        for (int i = 0; i < args.length; i++)
        {
            if (this.CMD.contains(args[i].toLowerCase()))
                l.set(i, true);
            else
                l.set(i, false);
        }
        return !l.contains(false);
    }
}
