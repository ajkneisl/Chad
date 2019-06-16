package dev.shog.chad.commands.developer

import dev.shog.chad.core.ChadVar
import dev.shog.chad.core.getClient
import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.PresenceHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.ActivityType
import sx.blah.discord.handle.obj.StatusType
import java.util.*

/**
 * Modify Chad's discord presence
 *
 * @author sho, codebasepw
 */
class ModifyPresence : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Checks if there's no arguments
        if (args.isEmpty()) {
            MessageHandler(e.channel, e.author).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "modpresence [rotate/static/add/view/status/activity/new presence]")
            return
        }

        when (args[0]) {
            "refresh" -> {
                PresenceHandler.refreshPresences()
                MessageHandler(e.channel, e.author).sendMessage("Refreshed presences.")
            }

            "rotate" -> {
                ChadVar.rotatePresence = true
                MessageHandler(e.channel, e.author).sendMessage("Enabled presence rotation.")
            }

            "static" -> {
                ChadVar.rotatePresence = false
                MessageHandler(e.channel, e.author).sendMessage("Disabled presence rotation.")
            }

            "view" -> {
                val stringBuilder = StringBuilder()

                for (s in PresenceHandler.presences) stringBuilder.append("Activity Type: `${s.activityType}`, Status Type: `${s.statusType}`, Status: `${s.status}`\n")

                MessageHandler(e.channel, e.author).sendMessage(stringBuilder.removeSuffix("\n").toString())
            }

            "random" -> {
                PresenceHandler.randomPresence()

                MessageHandler(e.channel, e.author).sendMessage("Changed to random presence!")
            }

            "status" -> {
                // Removes the option argument
                args.removeAt(0)

                ChadVar.statusType = when (args[0].toLowerCase()) {
                    "idle" -> StatusType.IDLE
                    "online" -> StatusType.ONLINE
                    "offline" -> StatusType.INVISIBLE
                    "dnd" -> StatusType.DND
                    else -> {
                        MessageHandler(e.channel, e.author).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "modpresence status [dnd/offline/online/idle]")
                        return
                    }
                }

                e.client.changePresence(ChadVar.statusType, ChadVar.activityType, ChadVar.currentStatus)
                MessageHandler(e.channel, e.author).sendMessage("Changed status type to `${ChadVar.statusType.name}`")
                return
            }

            "activity" -> {
                // Removes the option argument
                args.removeAt(0)

                ChadVar.activityType = when (args[0].toLowerCase()) {
                    "playing" -> ActivityType.PLAYING
                    "listening" -> ActivityType.LISTENING
                    "watching" -> ActivityType.WATCHING
                    "streaming" -> ActivityType.STREAMING
                    else -> {
                        MessageHandler(e.channel, e.author).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "modpresence activity [playing/listening/watching]")
                        return
                    }
                }

                e.client.changePresence(ChadVar.statusType, ChadVar.activityType, ChadVar.currentStatus)
                MessageHandler(e.channel, e.author).sendMessage("Changed activity type to `${ChadVar.activityType.name}`")
                return
            }

            else -> {
                val sb = StringBuilder()
                for (arg in args) sb.append("$arg ")

                ChadVar.currentStatus = sb.toString().trim { it <= ' ' }
                getClient().changePresence(ChadVar.statusType, ChadVar.activityType, ChadVar.currentStatus)
                MessageHandler(e.channel, e.author).sendMessage("Changed presence to `${ChadVar.currentStatus}`.")
            }
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["modpresence activity [playing/listening/watching]]"] = "Changes the bot's activity."
        st["modpresence [string]"] = "Changes the bots rich presence message."
        st["modpresence status [dnd/offline/online/idle]"] = "Changes the bots status."
        st["modpresence [static/rotate]"] = "Disables or enables the bot's presence rotation."
        Command.helpCommand(st, "Modify Presence", e)
    }
}
