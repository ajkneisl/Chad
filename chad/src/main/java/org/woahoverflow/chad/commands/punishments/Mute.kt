package org.woahoverflow.chad.commands.punishments

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.coroutine.asIChannel
import org.woahoverflow.chad.framework.handle.coroutine.asIUser
import org.woahoverflow.chad.framework.handle.coroutine.request
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.Permissions
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

/**
 * Mutes a user in the specified channel.
 *
 * @author codebasepw, sho
 */
class Mute : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val channel = e.message.channel

        if (e.message.mentions.isEmpty()) {
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.NO_MENTIONS)
                return
            }

            when (args[0].toLowerCase()) {
                "list" -> {
                    val sb = StringBuilder()

                    sb.append("**Muted Users**")

                    for (pair in MUTED_USERS) {
                        val ch = request {
                            e.guild.getChannelByID(pair.key)
                        }.asIChannel()

                        val muted = pair.value

                        if (ch.isDeleted) continue

                        sb.append("\n`${ch.name}`: ```")

                        for (mutedUser in muted) {
                            val userName = request {
                                e.guild.getUserByID(mutedUser)
                            }.asIUser()

                            sb.append("$userName, ")
                        }

                        sb.removeSuffix(", ")
                    }

                    sb.append("```")
                }
            }
        }

        for (user in e.message.mentions) {
            if (!MUTED_USERS.containsKey(e.channel.longID)) MUTED_USERS[channel.longID] = ArrayList()
            val ch = MUTED_USERS[e.channel.longID]!!

            when {
                ch.contains(user.longID) -> {
                    if (!e.channel.getModifiedPermissions(user).contains(Permissions.SEND_MESSAGES))
                        channel.overrideUserPermissions(user, EnumSet.of(Permissions.SEND_MESSAGES), null)
                }

                // Assume they've been muted by a previous instance of Chad
                !e.channel.getModifiedPermissions(user).contains(Permissions.SEND_MESSAGES) -> {
                    val override = channel.getModifiedPermissions(user)
                    override.add(Permissions.SEND_MESSAGES)
                    channel.overrideUserPermissions(user, EnumSet.of(Permissions.SEND_MESSAGES), null)
                }

                else -> {
                    ch.add(user.longID)

                    val override = channel.getModifiedPermissions(user)
                    override.remove(Permissions.SEND_MESSAGES)

                    channel.overrideUserPermissions(user, null, EnumSet.of(Permissions.SEND_MESSAGES))
                }
            }
        }

        messageHandler.sendMessage("Modified `${e.message.mentions.size}` users.")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["mute [@user]"] = "Mutes a user in the current channel."
        Command.helpCommand(st, "Mute", e)
    }

    companion object {
        private val MUTED_USERS = ConcurrentHashMap<Long, ArrayList<Long>>()
    }
}
