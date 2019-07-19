package dev.shog.chad.commands.info

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.PermissionHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*
import java.util.regex.Pattern

/**
 * All Chad commands
 *
 * @author sho
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
                        (GuildHandler.getGuild(e.guild.longID).getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase()) -> true
                        else -> false
                    }
            ) continue


            // The commands builder
            val commandsBuilder = StringBuilder()
            for ((key, meta) in Command.COMMANDS) {
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
                stringBuilder.append("\n\n**${category.toString().toLowerCase().capitalize()}**: \n ${REGEX.matcher(commandsBuilder.toString()).replaceAll("")}")
            }
        }

        stringBuilder.append("\n\nYou can only see the commands you have permission to!")

        MessageHandler(e.channel, e.author).sendEmbed {
            withDesc("**Support Discord**: https://shog.dev/discord\n**Invite Chad**: https://shog.dev/chad/invite$stringBuilder")
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
