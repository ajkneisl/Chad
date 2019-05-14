package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PermissionHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild.DataType
import org.woahoverflow.chad.framework.util.Util
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*
import java.util.regex.Pattern

/**
 * All Chad commands
 *
 * @author sho, codebasepw
 */
class Help : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val stringBuilder = StringBuilder()
        // Go through each category and add all it's commands to the help string
        for (category in Command.Category.values()) {
            // If the category is Nsfw and the channel isn't Nsfw, don't show.
            if (category === Command.Category.NSFW && !e.channel.isNSFW) continue

            // If the category is Admin and the user isn't an Admin, don't show.
            if (category === Command.Category.DEVELOPER && !PermissionHandler.isDeveloper(e.author)) continue

            // If the category is disabled
            if ((GuildHandler.getGuild(e.guild.longID).getObject(DataType.DISABLED_CATEGORIES) as ArrayList<*>).contains(category.toString().toLowerCase())) continue

            // The commands builder
            val commandsBuilder = StringBuilder()
            for ((key, meta) in ChadVar.COMMANDS) {
                // Gets the command's data

                // Makes sure the command is in the right area
                if (meta.commandCategory !== category)
                    continue

                // Makes sure the user has permission
                if (!PermissionHandler.hasPermission(key, e.author, e.guild))
                    continue

                // Adds the command to the builder
                val str = "`$key`, "
                commandsBuilder.append(str)
            }

            // Replaces the end and makes sure there's content
            if (commandsBuilder.isNotEmpty()) {
                stringBuilder.append("\n\n").append("**").append(Util.fixEnumString(category.toString().toLowerCase())).append("**").append(": \n").append(REGEX.matcher(commandsBuilder.toString()).replaceAll(""))
            }
        }

        // Adds a warning that you can only see the commands you have permission to
        stringBuilder.append("\n\nYou can only see the commands you have permission to!")

        val embedBuilder = EmbedBuilder()

        embedBuilder.withDesc("**Support Discord**: https://woahoverflow.org/discord\n**Invite Chad**: https://woahoverflow.org/chad/invite$stringBuilder")
        embedBuilder.withTitle("Chad's Commands")

        // Sends the message
        MessageHandler(e.channel, e.author).sendEmbed(embedBuilder)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["help"] = "Displays all commands Chad has to offer."
        Command.helpCommand(st, "Help", e)
    }

    companion object {
        private val REGEX = Pattern.compile(", $")
    }
}
