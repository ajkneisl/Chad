package org.woahoverflow.chad.framework.handle

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.obj.Command.Category
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.Permissions
import java.util.stream.Stream

/**
 * Handles permissions within Chad
 *
 * @author sho, codebasepw
 */
object PermissionHandler {
    /**
     * Checks if a user is a developer
     *
     * @param user The user
     * @return If they're a verified developer
     */
    @JvmStatic
    fun userIsDeveloper(user: IUser): Boolean {
        return ChadVar.DEVELOPERS.contains(user.longID)
    }

    /**
     * Checks if a user has permission for a command
     *
     * @param command The command
     * @param user The user to check
     * @param guild The guild in which it's happening
     * @return If the user doesn't have permission to use it
     */
    @JvmStatic
    fun userNoPermission(command: String, user: IUser, guild: IGuild): Boolean {
        val guildInstance = GuildHandler.getGuild(guild.longID)

        val meta = ChadVar.COMMANDS[command]

        // developers should always have permission for developer commands
        if (meta!!.commandCategory == Category.DEVELOPER && userIsDeveloper(user))
            return false

        // All users should have access to these categories
        if (Stream.of(Category.FUN, Category.INFO, Category.NSFW, Category.GAMBLING, Category.MUSIC, Category.COMMUNITY).anyMatch { category -> meta.commandCategory == category })
            return false

        // If the user is Administrator, they should have all guild related permissions
        return if (user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) false else user.getRolesForGuild(guild).stream().noneMatch { role -> guildInstance.getRolePermissions(role.longID).contains(command) }
    }

    /**
     * Turns the error code into a string
     *
     * @param i The error code
     * @return The parsed string
     */
    @JvmStatic
    fun parseErrorCode(i: Int): String {
        if (i == 1)
            return "This role already has this command!"
        if (i == 2)
            return "This role doesn't have that command!"
        if (i == 3)
            return "That command doesn't exist!"
        return if (i == 4) "This role doesn't have any commands!" else "I don't know how you got here.\nPlease report the used command to our forums at https://woahoverflow.org"
    }
}
