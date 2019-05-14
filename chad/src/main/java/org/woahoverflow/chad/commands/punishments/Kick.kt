package org.woahoverflow.chad.commands.punishments

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.PermissionUtils

import java.util.HashMap
import java.util.regex.Pattern

/**
 * Moderator tool to kick a user
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
class Kick : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        val guild = GuildHandler.getGuild(e.guild.longID)

        // Checks if the bot has permission to kick
        if (!e.client.ourUser.getPermissionsForGuild(e.guild).contains(Permissions.KICK)) {
            messageHandler.sendPresetError(MessageHandler.Messages.BOT_NO_PERMISSION)
            return
        }

        // Forms user from author's mentions
        val user: IUser
        val reason: MutableList<String>
        if (!e.message.mentions.isEmpty() && args[0].contains(e.message.mentions[0].stringID)) {
            user = e.message.mentions[0]
            args.removeAt(0)
            reason = args
        } else {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_USER)
            return
        }

        // Checks if the user action upon has administrator
        if (user.getPermissionsForGuild(e.guild).contains(Permissions.ADMINISTRATOR)) {
            messageHandler.sendPresetError(MessageHandler.Messages.BOT_NO_PERMISSION)
            return
        }

        // Checks if bot has hierarchical permissions
        if (!PermissionUtils.hasHierarchicalPermissions(e.channel, e.client.ourUser, user, Permissions.KICK)) {
            messageHandler.sendPresetError(MessageHandler.Messages.BOT_NO_PERMISSION)
            return
        }

        // Checks if user has hierarchical permissions
        if (!PermissionUtils.hasHierarchicalPermissions(e.channel, e.client.ourUser, user, Permissions.KICK)) {
            messageHandler.sendPresetError(MessageHandler.Messages.BOT_NO_PERMISSION)
            return
        }

        // Builds reason
        val builtReason = StringBuilder()
        if (!reason.isEmpty())
            for (s in reason)
                builtReason.append(s).append(' ')
        else
            builtReason.append("no reason")

        // Checks if kick message is enabled
        if (guild.getObject(DataType.KICK_MESSAGE_ON) as Boolean) {
            // Gets the message from the cache
            val message = guild.getObject(Guild.DataType.KICK_MESSAGE) as String

            // If the message isn't null, continue
            var formattedMessage = GUILD_PATTERN.matcher(message).replaceAll(e.guild.name) // replaces &guild& with guild's name
            formattedMessage = USER_PATTERN.matcher(formattedMessage).replaceAll(user.name) // replaces &user& with user's name
            formattedMessage = REASON_PATTERN.matcher(formattedMessage).replaceAll(builtReason.toString().trim { it <= ' ' }) // replaces &reason& with the reason

            // If the user isn't bot, send the message.
            if (!user.isBot)
                MessageBuilder(e.client).withChannel(e.client.getOrCreatePMChannel(user)).withContent(formattedMessage).build()
        }

        // If there's no reason, continue with "no reason"
        if (reason.isEmpty()) {
            e.guild.kickUser(user)
            reason.add("None")
            messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully kicked " + user.name + " for no reason."))
            MessageHandler.sendPunishLog("Kick", user, e.author, e.guild, reason)
            return
        }

        // Kicks the user.
        e.guild.kickUser(user)
        messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully kicked " + user.name + " for " + builtReason.toString().trim { it <= ' ' } + '.'.toString()))
        MessageHandler.sendPunishLog("Kick", user, e.author, e.guild, reason)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["kick [@user]"] = "Kicks a user with no reason."
        st["kick [@user] [reason]"] = "Kicks a user with a specified reason."
        Command.helpCommand(st, "Kick", e)
    }

    companion object {
        // Patterns for the message forming
        private val GUILD_PATTERN = Pattern.compile("&guild&")
        private val USER_PATTERN = Pattern.compile("&user&")
        private val REASON_PATTERN = Pattern.compile("&reason&")
    }
}
