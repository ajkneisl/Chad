package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Temporarily modifies users with developer role
 *
 * @author sho
 */
class ModifyDevelopers : Command.Class {
    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["moddev <add/view/remove> [id]"] = "Modifies users with developer role."
        return Command.helpCommand(st, "Modify Developers", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)
            val prefix = GuildHandler.handle.getGuild(e.guild.longID)

            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddev **add/view/remove**")
                return@Runnable
            }

            when (args[0].toLowerCase()) {
                "add" -> {
                    if (args.size != 2) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddev add **id**")
                        return@Runnable
                    }

                    val id: Long

                    try {
                        id = args[1].toLong()
                    } catch (ex: NumberFormatException) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[1])
                        return@Runnable
                    }

                    if (ChadVar.DEVELOPERS.contains(id)) {
                        messageHandler.sendError("That user is already a developer!")
                        return@Runnable
                    }

                    ChadVar.DEVELOPERS.add(id)

                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Added user `$id` temporarily to developer role."))
                    return@Runnable
                }

                "remove" -> {
                    if (args.size != 2) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddev remove **id**")
                        return@Runnable
                    }

                    val id: Long

                    try {
                        id = args[1].toLong()
                    } catch (ex: NumberFormatException) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[1])
                        return@Runnable
                    }

                    if (!ChadVar.DEVELOPERS.contains(id)) {
                        messageHandler.sendError("That user isn't a developer!")
                        return@Runnable
                    }

                    ChadVar.DEVELOPERS.remove(id)

                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Removed user `$id` temporarily from developer role."))
                    return@Runnable
                }

                "view" -> {
                    val stringBuilder = StringBuilder()

                    for (id in ChadVar.DEVELOPERS) {
                        stringBuilder.append("$id, ")
                    }

                    messageHandler.sendEmbed(EmbedBuilder().withDesc(stringBuilder.toString().removeSuffix(", ")))
                }
            }
        }
    }
}