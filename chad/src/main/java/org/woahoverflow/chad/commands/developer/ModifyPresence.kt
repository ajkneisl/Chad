package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
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
    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

            // Checks if there's no arguments
            if (args.isEmpty()) {
                MessageHandler(e.channel, e.author).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "modpresence [rotate/static/add/view/status/activity/new presence]")
                return@Runnable
            }

            when (args[0]) {
                "rotate" -> {
                    ChadVar.rotatePresence = true
                    MessageHandler(e.channel, e.author).sendMessage("Enabled presence rotation.")
                }
                "static" -> {
                    ChadVar.rotatePresence = false
                    MessageHandler(e.channel, e.author).sendMessage("Disabled presence rotation.")
                }

                "add" -> {
                    // Removes the option argument
                    args.removeAt(0)

                    val sb = StringBuilder()
                    for (arg in args) sb.append("$arg ")

                    ChadVar.presenceRotation.add(sb.toString().trim { it <= ' ' })
                    MessageHandler(e.channel, e.author).sendMessage("Added `" + sb.toString().trim { it <= ' ' } + "` to rotation")
                }

                "view" -> {
                    val stringBuilder = StringBuilder()

                    for (s in ChadVar.presenceRotation) stringBuilder.append("`$s`, ")

                    MessageHandler(e.channel, e.author).sendMessage(stringBuilder.toString().substring(0, stringBuilder.toString().length - 2))
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
                            return@Runnable
                        }
                    }

                    e.client.changePresence(ChadVar.statusType, ChadVar.activityType, ChadVar.currentStatus)
                    MessageHandler(e.channel, e.author).sendMessage("Changed status type to `${ChadVar.statusType.name}`")
                    return@Runnable
                }

                "activity" -> {
                    // Removes the option argument
                    args.removeAt(0)

                    ChadVar.activityType = when (args[0].toLowerCase()) {
                        "playing" -> ActivityType.PLAYING
                        "listening" -> ActivityType.LISTENING
                        "watching" -> ActivityType.WATCHING
                        else -> {
                            MessageHandler(e.channel, e.author).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "modpresence activity [playing/listening/watching]")
                            return@Runnable
                        }
                    }

                    e.client.changePresence(ChadVar.statusType, ChadVar.activityType, ChadVar.currentStatus)
                    MessageHandler(e.channel, e.author).sendMessage("Changed activity type to `${ChadVar.activityType.name}`")
                    return@Runnable
                }

                else -> {
                    val sb = StringBuilder()
                    for (arg in args) sb.append("$arg ")

                    ChadVar.currentStatus = sb.toString().trim { it <= ' ' }
                    ChadInstance.cli.changePresence(ChadVar.statusType, ChadVar.activityType, ChadVar.currentStatus)
                    MessageHandler(e.channel, e.author).sendMessage("Changed presence to `${ChadVar.currentStatus}`.")
                }
            }
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["modpresence activity [playing/listening/watching]]"] = "Changes the bot's activity."
        st["modpresence [string]"] = "Changes the bots rich presence message."
        st["modpresence status [dnd/offline/online/idle]"] = "Changes the bots status."
        st["modpresence [static/rotate]"] = "Disables or enables the bot's presence rotation."
        return Command.helpCommand(st, "Modify Presence", e)
    }
}
