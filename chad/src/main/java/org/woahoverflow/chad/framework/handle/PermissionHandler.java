package org.woahoverflow.chad.framework.handle;

import java.util.Objects;
import java.util.stream.Stream;
import org.bson.Document;
import org.woahoverflow.chad.commands.function.Permissions;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Command.Category;
import org.woahoverflow.chad.framework.Command.Class;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

public class PermissionHandler
{
    public static final PermissionHandler handle = new PermissionHandler();
    // check if the user is in the list of developers
    public boolean userIsDeveloper(IUser user) {
        return ChadVar.GLOBAL_PERMISSIONS.get(user.getStringID()) == PermissionHandler.Levels.SYSTEM_ADMINISTRATOR;
    }

    // check if the user has permission for the specified command
    public boolean userHasPermission(String command, IUser user, IGuild g)
    {
        Class cmd = ChadVar.COMMANDS.get(command).getCommandClass();

        if (cmd == null)
        {
            return false; // return false if the command doesnt exist
        }

        // system admins can set their own permissions :) (for testing tho don't worry)
        if (cmd instanceof Permissions && userIsDeveloper(user)) {
            return true;
        }

        Command.Data meta = ChadVar.COMMANDS.get(command);
        // developers should always have permission for developer commands
        if (meta.getCommandCategory() == Category.ADMIN && userIsDeveloper(user)) {
            return true;
        }

        // all users should have access to commands in the fun and info commandCategory
        if (Stream.of(Category.FUN, Category.INFO, Category.NSFW, Category.MONEY).anyMatch(category -> meta.getCommandCategory() == category))
        {
            return true;
        }

        // loop through the users roles, if the role has permission for the command, return true
        // return false if none of the users roles have permission for the command
        return user.getRolesForGuild(g).stream()
            .filter(r -> DatabaseHandler.handle.getArray(g, r.getStringID()) != null)
            .anyMatch(r -> Objects
                .requireNonNull(DatabaseHandler.handle.getArray(g, r.getStringID())).contains(command));
    }

    public enum Levels
    {
        SYSTEM_ADMINISTRATOR
    }

    // grants the specified role access to the specified command in the guild the role belongs to
    public int addCommandToRole(IRole role, String command) throws IndexOutOfBoundsException
    {
        if (!parseCommand(command)) {
            return 0;
        }

        Document cachedDocument = Chad.getGuild(role.getGuild()).getDocument();

        if (cachedDocument == null) {
            return 1;
        }

        @SuppressWarnings("all")
        ArrayList<String> arr = (ArrayList<String>) cachedDocument.get(role.getStringID());

        if (arr == null || arr.isEmpty())
        {
            ArrayList<String> ar = new ArrayList<>();
            ar.add(command);
            DatabaseHandler.handle.set(role.getGuild(), role.getStringID(), ar);
            Chad.getGuild(role.getGuild()).cache();
            return 6;
        }
        if (arr.contains(command)) {
            return 2;
        }
        ArrayList<String> ar = arr;
        ar.add(command);
        DatabaseHandler.handle.set(role.getGuild(), role.getStringID(), ar);
        Chad.getGuild(role.getGuild()).cache();
        return 6;
    }

    // wadya get if you turn #addCommandToRole() upside down?
    public int removeCommandFromRole(IRole role, String command)
    {
        if (!parseCommand(command)) {
            return 0;
        }

        if (DatabaseHandler.handle.getArray(role.getGuild(), role.getStringID()) == null) {
            return 4;
        }

        Document get = Chad.getGuild(role.getGuild()).getDocument();

        if (get == null) {
            return 1;
        }

        ArrayList<String> ar = DatabaseHandler.handle.getArray(role.getGuild(), role.getStringID());

        if (ar == null)
        {
            return 1;
        }

        ar.remove(command);
        DatabaseHandler.handle.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
        Chad.getGuild(role.getGuild()).cache();
        return 6;

    }

    // this method is poorly named as it doesnt actually parse the command. i/ think it checks to see if its a valid command /shrug
    private static boolean parseCommand(String arg)
    {
        return ChadVar.COMMANDS.containsKey(arg.toLowerCase());
    }

    // this is only used for a select few commands, but it has its moments.
    public String parseErrorCode(int i)
    {
        if (i == 1)
        {
            return "An internal error has ocurred";
        }
        if (i == 2)
        {
            return "Command is already entered!";
        }
        if (i == 0)
        {
            return "Invalid Command!";
        }
        if (i == 4)
        {
            return "There's nothing to remove!";
        }
        return "An internal error has occurred!";
    }
}
