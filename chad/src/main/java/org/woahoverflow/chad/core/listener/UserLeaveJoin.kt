package org.woahoverflow.chad.core.listener

import org.bson.Document
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.handle.database.DatabaseManager
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player.DataType
import org.woahoverflow.chad.framework.util.Util
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent
import sx.blah.discord.handle.obj.*
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
        embedBuilder.withTitle("User Join : " + e.user.name).withFooterIcon(e.user.avatarURL)
        MessageHandler.sendLog(embedBuilder, guild)

        // If the guild has user join messages on, do that
        val g = GuildHandler.getGuild(e.guild.longID)
        if (g.getObject(Guild.DataType.JOIN_MESSAGE_ON) as Boolean) {
            val joinMsgCh = g.getObject(Guild.DataType.JOIN_MESSAGE_CHANNEL)

            if (joinMsgCh is Long) {
                val channel = RequestBuffer.request<IChannel> { guild.getChannelByID(joinMsgCh) }.get()

                if (channel != null && !channel.isDeleted) {
                    RequestBuffer.request<IMessage> {
                        channel.sendMessage(convert(g.getObject(Guild.DataType.JOIN_MESSAGE) as String, e.guild, e.user, e.guild.totalMemberCount + 1))
                    }
                }
            }
        }

        if (
            ChadInstance.cli.ourUser.getPermissionsForGuild(e.guild).contains(Permissions.MANAGE_ROLES)
            && g.getObject(Guild.DataType.ROLE_ON_JOIN) as Boolean
                ) {
            val joinRoleStringID = g.getObject(Guild.DataType.JOIN_ROLE)
            if (joinRoleStringID is Long) {
                val joinRole = e.guild.getRoleByID(joinRoleStringID)
                MessageHandler(e.guild.defaultChannel, e.user).sendError("There's an issue with the role in auto role!")

                if (joinRole.isDeleted || joinRole.isEveryoneRole) {
                    MessageHandler(e.guild.defaultChannel, e.user).sendError("There's an issue with the role in auto role!")
                    return
                }

                val botRoles = e.client.ourUser.getRolesForGuild(e.guild)

                var botPosition = 0
                for (role in botRoles) if (role.position > botPosition) botPosition = role.position

                if (joinRole.position > botPosition) {
                    MessageHandler(e.guild.defaultChannel, e.user).sendError("Chad doesn't have permission to assign the role in auto role!")
                    return
                }

                e.user.addRole(joinRole)
            }
        }
    }

    /**
     * The event when a user leaves
     *
     * @param e The event
     */
    @EventSubscriber
    fun userLeave(e: UserLeaveEvent) {
        val player = PlayerHandler.getPlayer(e.user.longID)

        var guildData = player.getObject(DataType.GUILD_DATA) as ArrayList<*>
        try {
            @Suppress("UNCHECKED_CAST")
            guildData = guildData as ArrayList<Long>
        } catch (e: ClassCastException) {
            ChadInstance.getLogger().error("Error with cast!", e)
            return
        }

        guildData.remove(e.guild.longID)

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

        // Logs the user's join
        val guild = e.guild
        val embedBuilder = EmbedBuilder()
        embedBuilder.withTitle("User Leave : " + e.user.name).withFooterIcon(e.user.avatarURL)
        MessageHandler.sendLog(embedBuilder, guild)

        // If the guild has user leave messages on, do that
        val g = GuildHandler.getGuild(e.guild.longID)
        if (g.getObject(Guild.DataType.LEAVE_MESSAGE_ON) as Boolean) {
            val leaveMsgCh = g.getObject(Guild.DataType.LEAVE_MESSAGE_CHANNEL)

            if (leaveMsgCh is Long) {
                val channel = RequestBuffer.request<IChannel> { guild.getChannelByID(leaveMsgCh) }.get()

                if (channel != null && !channel.isDeleted) {
                    RequestBuffer.request<IMessage> {
                        channel.sendMessage(convert(g.getObject(Guild.DataType.LEAVE_MESSAGE) as String, e.guild, e.user, e.guild.totalMemberCount - 1))
                    }
                }
            }
        }
    }

    private fun convert(msg: String, guild: IGuild, user: IUser, total: Int): String
        = GUILD_PATTERN.matcher(
                USER_PATTERN.matcher(
                        TOTAL_PATTERN.matcher(
                                FORMATTED_PATTERN.matcher(
                                        msg
                                ).replaceAll(Util.formatNumber(total))
                        ).replaceAll(total.toString())
                ).replaceAll(user.name)
        ).replaceAll(guild.name)


    companion object {
        private val TOTAL_PATTERN = Pattern.compile("&count&")
        private val FORMATTED_PATTERN = Pattern.compile("&formatted_count&")
        private val USER_PATTERN = Pattern.compile("&user&")
        private val GUILD_PATTERN = Pattern.compile("&guild&")
    }
}
