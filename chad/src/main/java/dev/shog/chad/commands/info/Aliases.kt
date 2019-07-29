package dev.shog.chad.commands.info

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Gets the aliases for a command
 *
 * @author sho
 */
class Aliases : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["aliases [command]"] = "Gets all aliases for a command."
        Command.helpCommand(st, "Aliases", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}aliases [command]")
            return
        }

        val command = args[0]

        if (!Command.COMMANDS.keys().toList().contains(command)) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}aliases [command]")
            return
        }

        val commandData = Command.COMMANDS[command]

        if (!commandData!!.usesAliases()) {
            messageHandler.sendError("There's no aliases for this command!")
            return
        }

        val stringBuilder = StringBuilder()

        for (alias in commandData.cmdAliases!!) {
            stringBuilder.append("`$alias`, ")
        }

        messageHandler.sendEmbed(EmbedBuilder().withDesc("The command `${command.toLowerCase()}` has ${commandData.cmdAliases!!.size} alias(s).\n\n${stringBuilder.toString().removeSuffix(", ")}"))
    }
}