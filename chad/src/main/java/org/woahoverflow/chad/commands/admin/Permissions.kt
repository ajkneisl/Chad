package org.woahoverflow.chad.commands.admin

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PermissionHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Command.Class
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer

import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Pattern

/**
 * Give or revoke command permissions
 *
 * @author sho, codebasepw
 */
class Permissions : Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Accesses the permissions to a specific role
        if (args.size >= 3 && args[0].equals("role", ignoreCase = true)) {
            // Removes the base argument, in this case "role", so it can get the role name
            args.removeAt(0)

            // Assign variables
            val stringBuilder = StringBuilder()
            var role: IRole? = null
            var i = 0

            // Builds the role name
            for (s in args) {
                // Adds the amount of arguments used in the role name so they can be removed later
                i++

                // Appends the string
                stringBuilder.append(s).append(' ')

                // Requests the roles from the guild
                val rolesList = RequestBuffer.request<List<IRole>> { e.guild.roles }
                        .get()

                // Checks if any of the roles equal
                for (rol in rolesList)
                    if (rol.name.equals(stringBuilder.toString().trim { it <= ' ' }, ignoreCase = true))
                        role = rol // If a role was found, assign it to the variable

                // If the role was assigned, break out of the loop
                if (role != null) break
            }

            // Make sure there's enough arguments for the rest
            if (args.size == i) {
                messageHandler.sendError("Invalid Role!")
                return
            }

            // Makes sure role isn't null
            if (role == null) {
                messageHandler.sendError("Invalid Role!")
                return
            }

            // Removes the amount of arguments that the role name used
            var i1 = 0
            while (i > i1) {
                i1++
                args.removeAt(0)
            }

            // Gets the option
            val option = args[0]

            // Isolates the next option(s)
            args.removeAt(0)

            // The guild's instance
            val guild = GuildHandler.getGuild(e.guild.longID)

            when (option.toLowerCase()) {
                "add" -> {
                    // The add can only add 1 command
                    if (args.size != 1) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "perms role [role name] add [command name]")
                        return
                    }

                    // Adds it to the database and gets the result
                    val add = guild.addPermissionToRole(role!!.longID, args[0].toLowerCase())

                    // If the result was 0 (good) return the amount, if not return the correct error.
                    if (add == 0)
                        messageHandler.sendEmbed(
                                EmbedBuilder()
                                        .withDesc("Added `" + args[0].toLowerCase() + "` command to role `" + role.name + "`.")
                                        .withTitle("Permissions"))
                    else
                        messageHandler.sendError(PermissionHandler.parseErrorCode(add))
                    return
                }
                "remove" -> {
                    // The remove can only remove 1 command
                    if (args.size != 1) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "perms role [role name] remove [command name]")
                        return
                    }

                    // Removes it from the database and gets the result
                    val rem = guild.removePermissionFromRole(role!!.longID, args[0].toLowerCase())

                    // If the result was 0 (good) return the amount, if not return the correct error.
                    if (rem == 0)
                        messageHandler.sendEmbed(EmbedBuilder()
                                .withDesc("Removed `" + args[0].toLowerCase() + "` command from role `" + role.name + "`.")
                                .withTitle("Permissions"))
                    else
                        messageHandler.sendError(PermissionHandler.parseErrorCode(rem))
                    return
                }
                "view" -> {
                    // Gets the permissions to a role
                    val ar = guild.getRolePermissions(role!!.longID)

                    // Checks if there's no permissions
                    if (ar.isEmpty()) {
                        messageHandler.sendError("There's no permissions in this role!")
                        return
                    }

                    // Creates an embed builder and applies the title
                    val embedBuilder = EmbedBuilder()
                    embedBuilder.withTitle("Viewing Permissions for `" + role.name + '`'.toString())

                    // Builds all the permissions
                    val stringBuilder2 = StringBuilder()
                    ar.forEach { v -> stringBuilder2.append(", ").append(v) }

                    // Replaces the first ',' and sends.
                    embedBuilder.withDesc(
                            COMMA.matcher(stringBuilder2.toString().trim { it <= ' ' }).replaceFirst(""))
                    messageHandler.sendEmbed(embedBuilder)
                    return
                }
                else -> {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "perms role [role name] [add/remove/view]")
                    return
                }
            }
        }
        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "perms role [role name] [add/remove/view]")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["perm role [role name] add [command]"] = "Adds a Chad command to a Discord role."
        st["perm role [role name] remove [command]"] = "Removes a Chad command to a Discord role."
        st["perm role [role name] view"] = "Displays all Chad commands tied to that Discord role."
        Command.helpCommand(st, "Permissions", e)
    }

    companion object {
        private val COMMA = Pattern.compile(",")
    }
}

