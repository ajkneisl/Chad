package org.woahoverflow.chad.commands.admin

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Command.Category
import org.woahoverflow.chad.framework.obj.Guild.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Modify the guild's settings
 *
 * @author sho
 */
class GuildSettings : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val guild = GuildHandler.getGuild(e.guild.longID)
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = guild.getObject(DataType.PREFIX) as String

        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "guildsettings **category/clearstats/stats**")
            return
        }

        when (args[0].toLowerCase()) {
            // Clears the statistics for the guild
            "clearstats" -> {
                guild.clearStatistics()

                messageHandler.sendEmbed(EmbedBuilder().withDesc("Cleared guild's statistics!"))

                return
            }
            "category" -> {
                // Arguments : cmd category <category> <off/on>
                if (args.size != 3) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "guildsettings category [category name] [on/off]")
                    return
                }

                // Makes sure the user inputted on or off
                if (!(args[2].equals("off", ignoreCase = true) || args[2].equals("on", ignoreCase = true))) {
                    messageHandler.sendError("Please use **on** or **off**!")
                    return
                }

                var category: Category? = null

                // Makes sure the category suggested is an actual category
                for (ct in Command.Category.values()) {
                    if (args[1].equals(ct.toString(), ignoreCase = true)) {
                        category = ct
                    }
                }

                // Makes sure a category was actually found
                if (category == null) {
                    messageHandler.sendError("Invalid Category!")
                    return
                }

                // Turns the on/off to a boolean
                val bool = !args[2].equals("off", ignoreCase = true)

                if (bool && !(guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
                    messageHandler.sendError("That category isn't disabled!")
                    return
                }

                if (!bool && (guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
                    messageHandler.sendError("That category is already disabled!")
                    return
                }

                if (bool && (guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
                    val disabled = guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>

                    disabled.remove(category.toString().toLowerCase())

                    guild.setObject(DataType.DISABLED_CATEGORIES, disabled)

                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Enabled category `" + category.toString().toLowerCase() + "`!"))
                    return
                }

                if (!bool && !(guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
                    val disabled = guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>

                    disabled.add(category.toString().toLowerCase())

                    guild.setObject(DataType.DISABLED_CATEGORIES, disabled)

                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Disabled category `" + category.toString().toLowerCase() + "`!"))
                    return
                }
                return
            }
            "stats" -> {
                messageHandler.sendEmbed(EmbedBuilder().withDesc("There's `" + guild.getObject(
                        DataType.MESSAGES_SENT) + "` messages sent.\nThere's `" + guild.getObject(DataType.COMMANDS_SENT) + "` commands sent."))
                return
            }

            else -> messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "guildsettings [category/clearstats/stats]")
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["guildsettings clearstats"] = "Clears your guild's statistics."
        st["guildsettings stats"] = "Gets your guild's statistics."
        st["guildsettings category <category name> <on/off>"] = "Enables or disables a category."
        Command.helpCommand(st, "Guild Settings", e)
    }
}
