package org.woahoverflow.chad.core.listener

import org.bson.Document
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.handle.database.DatabaseManager
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player.DataType
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*
import java.util.regex.Pattern

/**
 * User joining and Leaving events
 *
 * @author sho, codebasepw
 */
class UserLeaveJoin {

    /**
     * The event when a user joins
     *
     * @param e The event
     */
    @EventSubscriber
    fun userJoin(e: UserJoinEvent) {
        // Add it to the user's data set
        val player = PlayerHandler.getPlayer(e.user.longID)

        var guildData = player.getObject(DataType.GUILD_DATA) as ArrayList<*>
        try {
            @Suppress("UNCHECKED_CAST")
            guildData = guildData as ArrayList<Long>
        } catch (e: ClassCastException) {
            ChadInstance.getLogger().error("Error with cast!", e)
            return
        }

        if (!guildData.contains(e.guild.longID)) guildData.add(e.guild.longID)
        player.setObject(DataType.GUILD_DATA, guildData)

        // Logs the user's join
        val guild = e.guild
        val embedBuilder = EmbedBuilder()

        // Builds the embed
        embedBuilder.withTitle("User Join : " + e.user.name).withFooterIcon(e.user.avatarURL)

        // Sends the log
        MessageHandler.sendLog(embedBuilder, guild)

        // If the guild has user join messages on, do that
        val g = GuildHandler.getGuild(e.guild.longID)
        if (g.getObject(Guild.DataType.JOIN_MESSAGE_ON) as Boolean) {
            // Gets the join message channel
            val joinMsgCh = g.getObject(Guild.DataType.JOIN_MESSAGE_CHANNEL) as String

            // Makes sure they actually assigned a channel
            if (!joinMsgCh.equals("none", ignoreCase = true)) {
                val id: Long
                try {
                    id = joinMsgCh.toLong()
                } catch (throwaway: NumberFormatException) {
                    // Throws error in the UI
                    ChadInstance.getLogger().error("Guild " + guild.name + " has an invalid join message channel!")
                    return
                }

                // Gets the channel
                val channel = RequestBuffer.request<IChannel> { guild.getChannelByID(id) }.get()

                // Makes sure the channel isn't deleted
                if (!channel.isDeleted) {
                    // Gets the message, makes sure it isn't null, then sends
                    var msg = g.getObject(Guild.DataType.JOIN_MESSAGE) as String
                    msg = GUILD_PATTERN.matcher(USER_PATTERN.matcher(msg).replaceAll(e.user.name)).replaceAll(e.guild.name)
                    val finalMsg = msg
                    RequestBuffer.request<IMessage> { channel.sendMessage(finalMsg) }
                }
            }
        }

        // does the bot have MANAGE_ROLES?
        if (!ChadInstance.cli.ourUser.getPermissionsForGuild(e.guild).contains(Permissions.MANAGE_ROLES)) {
            MessageHandler(e.guild.defaultChannel, e.user).sendError("Auto role failed!\nChad needs the permission `MANAGE_ROLES`!")
            return
        }

        // you probably shouldn't put code below this comment
        val joinRoleStringID = g.getObject(Guild.DataType.JOIN_ROLE) as String
        if (!joinRoleStringID.equals("none", ignoreCase = true)) {
            val joinRoleID = java.lang.Long.parseLong(joinRoleStringID)
            val botRoles = ChadInstance.cli.ourUser.getRolesForGuild(e.guild)
            val joinRole = e.guild.getRoleByID(joinRoleID)

            // get the bots highest role position in the guild
            var botPosition = 0
            for (role in botRoles)
                if (role.position > botPosition)
                    botPosition = role.position

            // can the bot assign the user the configured role?
            if (joinRole.position > botPosition) {
                MessageHandler(e.guild.defaultChannel, e.user).sendError("Auto role assignment failed; Bot isn't allowed to assign the role.")
                return
            }

            // is the role @everyone?
            if (joinRole.isEveryoneRole) {
                MessageHandler(e.guild.defaultChannel, e.user).sendError("Auto role assignment failed; Misconfigured role.")
                return
            }

            // assign the role
            if (g.getObject(Guild.DataType.ROLE_ON_JOIN) as Boolean)
                if (joinRoleStringID != "none")
                    e.user.addRole(joinRole)
        }
    }

    /**
     * The event when a user leaves
     *
     * @param e The event
     */
    @EventSubscriber
    fun userLeave(e: UserLeaveEvent) {
        // Remove it from the user's data set
        val player = PlayerHandler.getPlayer(e.user.longID)

        var guildData = player.getObject(DataType.GUILD_DATA) as ArrayList<*>
        try {
            @Suppress("UNCHECKED_CAST")
            guildData = guildData as ArrayList<Long>
        } catch (e: ClassCastException) {
            ChadInstance.getLogger().error("Error with cast!", e)
            return
        }

        // Remove the guild that was left
        guildData.remove(e.guild.longID)

        val g = GuildHandler.getGuild(e.guild.longID)

        if (guildData.isEmpty()) {
            // If it's the last guild that they're in with Chad, remove theirs
            val col = DatabaseManager.USER_DATA.collection
            val document = col.find(Document("id", e.user.longID)).first()

            if (document != null)
                col.deleteOne(document)
        } else {
            player.setObject(
                    DataType.GUILD_DATA,
                    guildData
            )
        }

        // Log if the user leaves
        val guild = e.guild
        val embedBuilder = EmbedBuilder()

        // Builds the embed
        embedBuilder.withTitle("User Leave : " + e.user.name)
                .withFooterIcon(e.user.avatarURL)

        // Sends the log
        MessageHandler.sendLog(embedBuilder, guild)

        // If the guild has user leave messages on, do that
        if (g.getObject(Guild.DataType.LEAVE_MESSAGE_ON) as Boolean) {
            // Gets the leave message channel
            val leaveMsgCh = g.getObject(Guild.DataType.LEAVE_MESSAGE_CHANNEL) as String

            // Makes sure they actually assigned a channel
            if (!leaveMsgCh.equals("none", ignoreCase = true)) {
                val id: Long
                try {
                    id = leaveMsgCh.toLong()
                } catch (throwaway: NumberFormatException) {
                    // Throws error in the UI
                    ChadInstance.getLogger().error("Guild " + guild.name + " has an invalid leave message channel!")
                    return
                }

                // Gets the channel
                val channel = RequestBuffer.request<IChannel> { guild.getChannelByID(id) }.get()

                // Makes sure the channel isn't deleted
                if (!channel.isDeleted) {
                    // Gets the message, makes sure it isn't null, then sends
                    var msg = g.getObject(Guild.DataType.LEAVE_MESSAGE) as String
                    msg = GUILD_PATTERN.matcher(USER_PATTERN.matcher(Objects.requireNonNull(msg)).replaceAll(e.user.name)).replaceAll(e.guild.name)
                    val finalMsg = msg
                    RequestBuffer.request<IMessage> { channel.sendMessage(finalMsg) }
                }
            }
        }
    }

    companion object {
        private val USER_PATTERN = Pattern.compile("&user&")
        private val GUILD_PATTERN = Pattern.compile("&guild&")
    }
}
