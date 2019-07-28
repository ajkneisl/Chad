package dev.shog.chad.commands.admin

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate
import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.coroutine.asIRoleList
import dev.shog.chad.framework.handle.coroutine.request
import dev.shog.chad.framework.handle.database.DatabaseManager
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import dev.shog.chad.framework.util.createMessageHandler
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Automatically adds a role to a user when they join a server
 *
 * @author codebasepw
 */
class AutoRole : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = e.createMessageHandler()

        // Makes sure the arguments are empty
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "autorole [on/off/set]", includePrefix = true)
            return
        }

        when (args[0].toLowerCase()) {
            "on" -> {
                // Sets the value in the database
                DatabaseManager.GUILD_DATA.setObjects(
                        e.guild.longID,
                        AttributeUpdate("role_on_join").put(true)
                )

                // ReCaches the guild
                GuildHandler.refreshGuild(e.guild.longID)

                // Builds the embed and sends it
                val embedBuilder = EmbedBuilder()
                embedBuilder.withTitle("Auto Role")
                embedBuilder.withDesc("Auto Role enabled.")
                messageHandler.sendEmbed(embedBuilder)
            }
            "off" -> {
                // Sets the value in the database
                DatabaseManager.GUILD_DATA.setObjects(
                        e.guild.longID,
                        AttributeUpdate("role_on_join").put(false)
                )

                // ReCaches the guild
                GuildHandler.refreshGuild(e.guild.longID)

                // Builds the embed and sends it
                messageHandler.sendEmbed {
                    withThumbnail("Auto Role")
                    withDesc("Auto Role disabled.")
                }
            }
            "set" -> {
                // Isolates the role text
                args.removeAt(0)

                // Set variables
                val stringBuilder = StringBuilder()
                var roles: List<IRole> = ArrayList()

                // Gets roles with the text said
                for (s in args) {
                    stringBuilder.append("$s ")

                    roles = request { e.guild.getRolesByName(stringBuilder.toString().trim { it <= ' ' }) }.asIRoleList()

                    if (roles.isNotEmpty()) break
                }

                // If there's no roles, return
                if (roles.isEmpty()) {
                    messageHandler.sendError("Invalid Role!")
                    return
                }

                // The selected role
                val newRole = roles[0]

                // Sets the role ID into the database
                DatabaseManager.GUILD_DATA.setObjects(
                        e.guild.longID,
                        AttributeUpdate("join_role").put(newRole.stringID)
                )

                // Builds the embed and sends it
                messageHandler.sendEmbed {
                    withTitle("Auto Role")
                    withDesc("New users will now automatically receive the role: " + newRole.name)
                }
            }

            else -> messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "autorole [on/off/set]", includePrefix = true)
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["autorole <on/off>"] = "Toggles automatic role assignment features."
        st["autorole set <role name>"] = "Sets role."
        Command.helpCommand(st, "Auto Role", e)
    }
}
