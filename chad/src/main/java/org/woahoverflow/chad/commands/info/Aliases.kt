package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Gets the aliases for a command
 *
 * @author sho
 */
class Aliases : Command.Class {
    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["aliases <command>"] = "Gets all aliases for a command."
        return Command.helpCommand(st, "Aliases", e)
    }

    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)
            val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}aliases **command**")
                return@Runnable
            }

            val command = args[0]

            if (!ChadVar.COMMANDS.keys().toList().contains(command)) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}aliases **command**")
                return@Runnable
            }

            val commandData = ChadVar.COMMANDS[command]

            if (!commandData!!.usesAliases()) {
                messageHandler.sendError("There's no aliases for this command!")
                return@Runnable
            }

            val stringBuilder = StringBuilder()

            for (alias in commandData.cmdAliases!!) {
                stringBuilder.append("`$alias`, ")
            }

            messageHandler.sendEmbed(EmbedBuilder().withDesc("The command `${command.toLowerCase()}` has ${commandData.cmdAliases!!.size} alias(s).\n\n${stringBuilder.toString().removeSuffix(", ")}"))
        }
    }
}