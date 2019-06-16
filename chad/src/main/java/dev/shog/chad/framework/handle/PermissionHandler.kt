package dev.shog.chad.framework.handle

import dev.shog.chad.core.ChadVar
import dev.shog.chad.framework.obj.Command.Category
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
    fun isDeveloper(user: IUser): Boolean = ChadVar.DEVELOPERS.contains(user.longID)

    /**
     * Checks if a user has permission for a command
     */
    @JvmStatic
    fun hasPermission(command: String, user: IUser, guild: IGuild): Boolean {
        val guildInstance = GuildHandler.getGuild(guild.longID)
        val meta = ChadVar.COMMANDS[command]

        return when {
            meta!!.commandCategory == Category.DEVELOPER && isDeveloper(user) -> true

            Stream.of(Category.FUN, Category.INFO, Category.NSFW, Category.GAMBLING, Category.MUSIC, Category.COMMUNITY).anyMatch { category -> meta.commandCategory == category } -> true

            else -> {
                if (user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) return true


                for (role in user.getRolesForGuild(guild)) {
                    if (guildInstance.getRolePermissions(role.longID).contains(command)) return true
                }

                false
            }
        }
    }

    /**
     * Turns the error code into a string
     *
     * @param i The error code
     * @return The parsed string
     */
    @JvmStatic
    fun parseErrorCode(i: Int): String {
        return when (i) {
            1 -> "This role already has this command!"
            2 -> "This role doesn't have that command!"
            3 -> "That command doesn't exist!"
            4 -> "This role doesn't have any commands!"
            else -> "There was an internal error, please try again later."
        }
    }
}
