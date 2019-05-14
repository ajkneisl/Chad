package org.woahoverflow.chad.commands.admin

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap

/**
 * Filters through swears
 *
 * @author sho, codebasepw
 */
class Swearing : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        val guild = GuildHandler.getGuild(e.guild.longID)

        // if there's no arguments, give statistics
        if (args.isEmpty()) {
            // creates an embed builder and applies values
            val embedBuilder = EmbedBuilder()
            embedBuilder.withTitle("Swear Filter")

            val status = if (guild.getObject(DataType.SWEAR_FILTER) as Boolean) "enabled" else "disabled"

            embedBuilder.withDesc("Swearing in this guild is `$status`.")

            // send
            messageHandler.sendEmbed(embedBuilder)
            return
        }

        if (args.size == 1 && args[0].equals("on", ignoreCase = true) || args[0].equals("off", ignoreCase = true)) {
            // actual boolean value
            val toggle = args[0].equals("on", ignoreCase = true)

            // good looking value
            val toggleString = if (toggle) "enabled" else "disabled"

            // sets in database
            GuildHandler.getGuild(e.guild.longID).setObject(DataType.SWEAR_FILTER, toggle)

            // the message
            val message = if (toggle)
                "Swear filtering has been `$toggleString`.\n\nKeep in mind that the swear filter isn't always accurate.\nSome words may be blocked due to having a swear word in them,\n or some may be unblocked due to having an odd combination of different letters.\nIf you find a word that shouldn't/should be blocked, please tell us."
            else
            "Swear filtering has been `$toggleString`."

            // sends message
            messageHandler.sendEmbed(EmbedBuilder().withDesc(message))
            return
        }

        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, GuildHandler.getGuild(e.guild.longID).getObject(DataType.PREFIX).toString() + "swearfilter [on/off]")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["swearfilter [on/off]"] = "Toggles the swear filter."
        st["swearfilter"] = "Gets the status of the swear filter."
        Command.helpCommand(st, "Swear Filter", e)
    }
}
