package org.woahoverflow.chad.commands.admin

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.coroutine.isUnit
import org.woahoverflow.chad.framework.handle.coroutine.request
import org.woahoverflow.chad.framework.handle.database.DatabaseManager
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
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
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Makes sure the arguments are empty
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "autorole [on/off/set]")
            return
        }

        when (args[0].toLowerCase()) {
            "on" -> {
                // Sets the value in the database
                DatabaseManager.GUILD_DATA.setObject(e.guild.longID, "role_on_join", true)

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
                DatabaseManager.GUILD_DATA.setObject(e.guild.longID, "role_on_join", false)

                // ReCaches the guild
                GuildHandler.refreshGuild(e.guild.longID)

                // Builds the embed and sends it
                val embedBuilder2 = EmbedBuilder()
                embedBuilder2.withTitle("Auto Role")
                embedBuilder2.withDesc("Auto Role disabled.")
                messageHandler.sendEmbed(embedBuilder2)
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

                    roles = request {
                        e.guild.getRolesByName(stringBuilder.toString().trim { it <= ' ' })
                    }.also {
                        if (it.isUnit() || it.result !is List<*>)
                            return messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
                    }.result as List<IRole>

                    if (roles.isNotEmpty())
                        break
                }

                // If there's no roles, return
                if (roles.isEmpty()) {
                    messageHandler.sendError("Invalid Role!")
                    return
                }

                // The selected role
                val newRole = roles[0]

                // Sets the role ID into the database
                DatabaseManager.GUILD_DATA.setObject(e.guild.longID, "join_role", newRole.stringID)

                // Builds the embed and sends it
                val embedBuilder3 = EmbedBuilder()
                embedBuilder3.withTitle("Auto Role")
                embedBuilder3.withDesc("New users will now automatically receive the role: " + newRole.name)
                messageHandler.sendEmbed(embedBuilder3)
            }
            else -> messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "autorole [on/off/set]")
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["autorole <on/off>"] = "Toggles automatic role assignment features."
        st["autorole set <role name>"] = "Sets role."
        Command.helpCommand(st, "Auto Role", e)
    }
}
