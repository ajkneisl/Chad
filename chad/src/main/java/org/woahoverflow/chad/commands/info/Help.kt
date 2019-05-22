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
            if (
                    when {
                        category === Command.Category.NSFW && !e.channel.isNSFW -> true
                        category === Command.Category.DEVELOPER && !PermissionHandler.isDeveloper(e.author) -> true
                        (GuildHandler.getGuild(e.guild.longID).getObject(DataType.DISABLED_CATEGORIES) as ArrayList<*>).contains(category.toString().toLowerCase()) -> true
                        else -> false
                    }
            ) continue


            // The commands builder
            val commandsBuilder = StringBuilder()
            for ((key, meta) in ChadVar.COMMANDS) {
                if (
                        when {
                            meta.commandCategory !== category -> true
                            !PermissionHandler.hasPermission(key, e.author, e.guild) -> true
                            else -> false
                        }
                ) continue

                commandsBuilder.append("`$key`, ")
            }

            // Replaces the end and makes sure there's content
            if (commandsBuilder.isNotEmpty()) {
                stringBuilder.append("\n\n**${Util.fixEnumString(category.toString().toLowerCase())}**: \n ${REGEX.matcher(commandsBuilder.toString()).replaceAll("")}")
            }
        }

        stringBuilder.append("\n\nYou can only see the commands you have permission to!")

        MessageHandler(e.channel, e.author).sendEmbed {
            withDesc("**Support Discord**: https://woahoverflow.org/discord\n**Invite Chad**: https://woahoverflow.org/chad/invite$stringBuilder")
            withTitle("Chad's Commands")
        }
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
