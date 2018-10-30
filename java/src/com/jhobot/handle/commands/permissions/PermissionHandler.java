package com.jhobot.handle.commands.permissions;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.commands.CommandData;
import com.jhobot.handle.commands.Command;
import org.bson.Document;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "CanBeFinal"})
public class PermissionHandler
{
    private ArrayList<String> CMD; // arraylists are simpler, shut up
    public PermissionHandler()
    {
        this.CMD = new ArrayList<>();
        ChadVar.COMMANDS.forEach((k, v) -> {
            this.CMD.add(k);
        });
    }


    public boolean userIsDeveloper(IUser user) {
        return ChadVar.GLOBAL_PERMISSIONS.get(user.getStringID()) == PermissionLevels.SYSTEM_ADMINISTRATOR;
    }

    public boolean userHasPermission(String command, IUser user, IGuild g)
    {
        Command cmd = ChadVar.COMMANDS.get(command).commandClass;

        if (cmd == null)
        {
            return false; // just in case
        }

        CommandData meta = ChadVar.COMMANDS.get(command);
        if (meta.isDevOnly && userIsDeveloper(user))
            return true;

        for (IRole r : user.getRolesForGuild(g))
        {
            if (ChadVar.DATABASE_HANDLER.getArray(g, r.getStringID()) != null)
            {
                if (ChadVar.DATABASE_HANDLER.getArray(g, r.getStringID()).contains(command))
                    return true;
            }
        }
        return false;
    }

    public int addCommandToRole(IRole role, String command) throws IndexOutOfBoundsException
    {
        if (!parseCommand(command))
            return 0;


        Document get = ChadVar.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();
        if (get == null)
            return 1;
        if (ChadVar.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()) == null || ChadVar.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).isEmpty())
        {
            ArrayList<String> ar = new ArrayList<>();
            ar.add(command);
            ChadVar.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
            return 6;
        }
        else {
            if (ChadVar.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()).contains(command))
                return 2;
            ArrayList<String> ar = ChadVar.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID());
            ar.add(command);
            ChadVar.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
            return 6;
        }
    }

    public int removeCommandFromRole(IRole role, String command)
    {
        if (!parseCommand(command))
            return 0;

        if (ChadVar.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID()) == null)
            return 4;
        else {
            Document get = ChadVar.DATABASE_HANDLER.getCollection().find(new Document("guildid", role.getGuild().getStringID())).first();

            if (get == null)
                return 1;

            ArrayList<String> ar = ChadVar.DATABASE_HANDLER.getArray(role.getGuild(), role.getStringID());
            ar.remove(command);
            ChadVar.DATABASE_HANDLER.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
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
