package org.woahoverflow.chad.framework.handle;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Category;
import org.woahoverflow.chad.framework.obj.Command.Class;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.stream.Stream;

/**
 * Handles permissions within Chad
 *
 * @author sho, codebasepw
 */
public class PermissionHandler {
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
     * @return If the user doesn't have permission to use it
     */
    public boolean userNoPermission(String command, IUser user, IGuild guild) {
        Class cmd = ChadVar.COMMANDS.get(command).getCommandClass();

        Guild guildInstance = GuildHandler.handle.getGuild(guild.getLongID());

        if (cmd == null)
            return true; // return false if the command doesnt exist

        Command.Data meta = ChadVar.COMMANDS.get(command);

        // developers should always have permission for developer commands
        if (meta.getCommandCategory() == Category.DEVELOPER && userIsDeveloper(user))
            return false;

        // All users should have access to these categories
        if (Stream.of(Category.FUN, Category.INFO, Category.NSFW, Category.GAMBLING, Category.MUSIC, Category.COMMUNITY).anyMatch(category -> meta.getCommandCategory() == category))
            return false;

        // If the user is Administrator, they should have all guild related permissions
        if (user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR))
            return false;

        // loop through the users roles, if the role has permission for the command, return true
        // return false if none of the users roles have permission for the command

        return user.getRolesForGuild(guild).stream().noneMatch(role -> guildInstance.getRolePermissions(role.getLongID()).contains(command));
    }

    /**
     * Turns the error code into a string
     *
     * @param i The error code
     * @return The parsed string
     */
    public String parseErrorCode(int i) {
        if (i == 1)
            return "This role already has this command!";
        if (i == 2)
            return "This role doesn't have that command!";
        if (i == 3)
            return "That command doesn't exist!";
        if (i == 4)
            return "This role doesn't have any commands!";
        return "I don't know how you got here.\nPlease report the used command to our forums at https://woahoverflow.org";
    }
}
