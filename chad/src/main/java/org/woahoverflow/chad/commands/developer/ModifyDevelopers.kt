package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Temporarily modifies users with developer role
 *
 * @author sho
 */
class ModifyDevelopers : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["moddev [add/view/remove] [id]"] = "Modifies users with developer role."
        Command.helpCommand(st, "Modify Developers", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID)

        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddev [add/view/remove]")
            return
        }

        when (args[0].toLowerCase()) {
            "add" -> {
                if (args.size != 2) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddev add [id]")
                    return
                }

                val id: Long

                try {
                    id = args[1].toLong()
                } catch (ex: NumberFormatException) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[1])
                    return
                }

                if (ChadVar.DEVELOPERS.contains(id) || ChadVar.ORIGINAL_DEVELOPERS.contains(id)) {
                    messageHandler.sendError("That user is already a developer!")
                    return
                }

                ChadVar.DEVELOPERS.add(id)

                messageHandler.sendEmbed(EmbedBuilder().withDesc("Added user `$id` temporarily to developer role."))
                return
            }

            "remove" -> {
                if (args.size != 2) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddev remove [id]")
                    return
                }

                val id: Long

                try {
                    id = args[1].toLong()
                } catch (ex: NumberFormatException) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[1])
                    return
                }

                if (!ChadVar.DEVELOPERS.contains(id)) {
                    messageHandler.sendError("That user isn't a developer!")
                    return
                } else if (ChadVar.ORIGINAL_DEVELOPERS.contains(id)) {
                    messageHandler.sendError("You aren't allowed to remove that person!")
                    return
                }

                ChadVar.DEVELOPERS.remove(id)

                messageHandler.sendEmbed(EmbedBuilder().withDesc("Removed user `$id` temporarily from developer role."))
                return
            }

            "view" -> {
                val stringBuilder = StringBuilder()

                for (id in ChadVar.DEVELOPERS) stringBuilder.append("$id, ")
                for (id in ChadVar.ORIGINAL_DEVELOPERS) stringBuilder.append("[O] $id, ")

                messageHandler.sendEmbed(EmbedBuilder().withDesc(stringBuilder.toString().removeSuffix(", ")))
            }
        }
    }
}