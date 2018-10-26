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
    public static Map<String, PermissionLevels> GLOBAL_PERMISSIONS = new HashMap<>();
    private PermissionHandler()
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

        if (cmd == null)
        {
            return false; // just in case
        }

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

    public int addCommandToRole(IRole role, String command) throws IndexOutOfBoundsException
    {
        if (!parseCommand(command))
            return 0;

        System.out.println("for logging purposes, does it get past parsing?");


        Document get = ChadBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();
        if (get == null)
            return 1;
        if (ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()) == null || ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).isEmpty())
        {
            ArrayList<String> ar = new ArrayList<>();
            ar.add(command);
            ChadBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
            return 6;
        }
        else {
            if (ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).contains(command))
                return 2;
            ArrayList<String> ar = ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID());
            ar.add(command);
            ChadBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
            return 6;
        }
    }

    public int removeCommandFromRole(IRole role, String command)
    {
        if (!parseCommand(command))
            return 0;

        if (ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()) == null)
            return 4;
        else {
            Document get = ChadBot.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return 1;

            ArrayList<String> ar = ChadBot.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID());
            ar.remove(command);
            ChadBot.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
            return 6;
        }

    }

    private boolean parseCommand(String arg)
    {
        return this.CMD.contains(arg.toLowerCase());
    }

    public String parseErrorCode(int i)
    {
        if (i == 1)
        {
            return "An internal error has occurred!";
        }
        else if (i == 2)
        {
            return "Command is already entered!";
        }
        else if (i == 0)
        {
            return "Invalid Command!";
        }
        else if (i == 4)
        {
            return "There's nothing to remove!";
        }
        return "An internal error has occurred!";
    }
}
