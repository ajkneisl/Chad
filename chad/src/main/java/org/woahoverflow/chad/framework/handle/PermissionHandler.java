package org.woahoverflow.chad.framework.handle;

import java.util.stream.Stream;
import org.bson.Document;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Command.Category;
import org.woahoverflow.chad.framework.Command.Class;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Handles permissions within Chad
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class PermissionHandler
{

    /**
     * The global handle for the Permission Handler
     */
    public static final PermissionHandler handle = new PermissionHandler();

    /**
     * Checks if a user is a developer
     *
     * @param user The user
     * @return If they're a verified developer
     */
    public boolean userIsDeveloper(IUser user) {
        return ChadVar.DEVELOPERS.contains(user.getLongID());
    }

    /**
     * Checks if a user has permission for a command
     *
     * @param command The command
     * @param user The user to check
     * @param guild The guild in which it's happening
     * @return If the user has permission to perform it
     */
    @SuppressWarnings("unchecked")
    public boolean userHasPermission(String command, IUser user, IGuild guild)
    {
        Class cmd = ChadVar.COMMANDS.get(command).getCommandClass();

        if (cmd == null)
            return false; // return false if the command doesnt exist

        Command.Data meta = ChadVar.COMMANDS.get(command);

        // developers should always have permission for developer commands
        if (meta.getCommandCategory() == Category.DEVELOPER && userIsDeveloper(user))
            return true;

        // All users should have access to these categories
        if (Stream.of(Category.FUN, Category.INFO, Category.NSFW, Category.MONEY, Category.MUSIC).anyMatch(category -> meta.getCommandCategory() == category))
            return true;

        // If the user is Administrator, they should have all guild related permissions
        if (user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR))
            return true;

        // loop through the users roles, if the role has permission for the command, return true
        // return false if none of the users roles have permission for the command
        return user.getRolesForGuild(guild).stream()
            .filter(r -> Chad.getGuild(guild.getLongID()).getDocument().get(r.getStringID()) != null)
            .anyMatch(r -> ((ArrayList<String>) Chad.getGuild(guild.getLongID()).getDocument().get(r.getStringID())).contains(command));
    }

    /**
     * Add a command to a role
     *
     * @param role The role to add to
     * @param command The command to add
     * @return The return code
     */
    public int addCommandToRole(IRole role, String command)
    {
        if (!parseCommand(command))
            return 0;

        Document cachedDocument = Chad.getGuild(role.getGuild().getLongID()).getDocument();

        if (cachedDocument == null)
            return 1;

        @SuppressWarnings("all")
        ArrayList<String> arr = (ArrayList<String>) cachedDocument.get(role.getStringID());

        if (arr == null || arr.isEmpty())
        {
            ArrayList<String> ar = new ArrayList<>();
            ar.add(command);
            DatabaseHandler.handle.set(role.getGuild(), role.getStringID(), ar);
            Chad.getGuild(role.getGuild().getLongID()).cache();
            return 6;
        }
        if (arr.contains(command))
            return 2;
        ArrayList<String> ar = arr;
        ar.add(command);
        DatabaseHandler.handle.set(role.getGuild(), role.getStringID(), ar);
        Chad.getGuild(role.getGuild().getLongID()).cache();
        return 6;
    }

    /**
     * Removes a command from a role
     *
     * @param role The role to remove from
     * @param command The command to remove
     * @return The return code
     */
    public int removeCommandFromRole(IRole role, String command)
    {
        if (!parseCommand(command))
            return 0;

        if (DatabaseHandler.handle.getArray(role.getGuild(), role.getStringID()) == null)
            return 4;

        Document get = Chad.getGuild(role.getGuild().getLongID()).getDocument();

        if (get == null)
            return 1;

        ArrayList<String> ar = DatabaseHandler.handle.getArray(role.getGuild(), role.getStringID());

        if (ar == null)
            return 1;

        ar.remove(command);
        DatabaseHandler.handle.getCollection().updateOne(get, new Document("$set", new Document(role.getStringID(), ar)));
        Chad.getGuild(role.getGuild().getLongID()).cache();
        return 6;

    }

    /**
     * Checks if a command exists
     *
     * @param arg The command
     * @return If the command exists
     */
    private static boolean parseCommand(String arg)
    {
        return ChadVar.COMMANDS.containsKey(arg.toLowerCase());
    }

    /**
     * Turns the error code into a string
     *
     * @param i The error code
     * @return The parsed string
     */
    public String parseErrorCode(int i)
    {
        if (i == 1)
            return "An internal error has ocurred";
        if (i == 2)
            return "Command is already entered!";
        if (i == 0)
            return "Invalid Command!";
        if (i == 4)
            return "There's nothing to remove!";
        return "An internal error has occurred!";
    }
}
