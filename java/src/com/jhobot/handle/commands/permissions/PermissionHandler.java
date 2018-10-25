package com.jhobot.handle.commands.permissions;

import com.jhobot.core.ChadBot;
import com.jhobot.core.Listener;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.PermissionLevels;
import org.bson.Document;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;

public class PermissionHandler
{
    public static PermissionHandler HANDLER = new PermissionHandler();
    private ArrayList<String> CMD;
    public static Map<String, PermissionLevels> GLOBAL_PERMISSIONS = new HashMap<String, PermissionLevels>();
    public PermissionHandler()
    {
        this.CMD = new ArrayList<>();
        String[] cm = {"catfact", "catgallery", "eightball", "photoeditor", "random", "russianroulette", "logging", "im", "prefix", "guildinfo", "help", "redditnew", "reddittop", "steam", "updatelog", "userinfo", "kick", "ban"};
        this.CMD.addAll(Arrays.asList(cm));
    }

    public boolean userIsDeveloper(IUser user) {
        return GLOBAL_PERMISSIONS.get(user.getStringID()) == PermissionLevels.SYSTEM_ADMINISTRATOR;
    }

    public boolean userHasPermission(String command, IUser user, IGuild g)
    {
        Command cmd = Listener.hash.get(command);

        if (cmd.level().equals(PermissionLevels.SYSTEM_ADMINISTRATOR) && userIsDeveloper(user))
            return true;

        for (IRole r : user.getRolesForGuild(g))
        {
            if (ChadBot.DATABASE_HANDLER.getArray(g, r.getStringID()) != null)
            {
                if (ChadBot.DATABASE_HANDLER.getArray(g, r.getStringID()).contains(command))
                    return true;
            }
        }
        return false;
    }

    public boolean addCommandToRole(IRole role, String... commands)
    {
        if (!parseCommands(commands))
            return false;

        if (ChadBot.DATABASE_HANDLER.getString(role.getGuild(), role.getStringID()) == null)
        {
            Document get = ChadBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return false;

            ArrayList<String> ar = new ArrayList<>(Arrays.asList(commands));
            ChadBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), Arrays.asList(commands))));
            return true;
        }
        else {
            Document get = ChadBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return false;

            ChadBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).addAll(Arrays.asList(commands)))));
            return true;
        }
    }

    public boolean removeCommandFromRole(IRole role, String... commands)
    {
        if (!parseCommands(commands))
            return false;

        if (ChadBot.DATABASE_HANDLER.getString(role.getGuild(), role.getStringID()) == null)
            return false;
        else {
            Document get = ChadBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return false;

            ChadBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).removeAll(Arrays.asList(commands)))));
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