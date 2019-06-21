package dev.shog.chad.commands.admin

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Command.Category
import dev.shog.chad.framework.obj.Guild.DataType
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

        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "guildsettings [category/clearstats/stats]", includePrefix = true)
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
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "guildsettings category [category name] [on/off]", includePrefix = true)
                    return
                }

                // Makes sure the user inputted on or off
                val toggle = when (args[2].toLowerCase()) {
                    "off" -> false
                    "on" -> true
                    else -> {
                        messageHandler.sendError("Please use **on** or **off**!")
                        return
                    }
                }

                var category: Category? = null
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

                if (toggle && !(guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
                    messageHandler.sendError("That category isn't disabled!")
                    return
                }

                if (!toggle && (guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
                    messageHandler.sendError("That category is already disabled!")
                    return
                }

                if (toggle && (guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
                    val disabled = guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>

                    disabled.remove(category.toString().toLowerCase())

                    guild.setObject(DataType.DISABLED_CATEGORIES, disabled)

                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Enabled category `" + category.toString().toLowerCase() + "`!"))
                    return
                }

                if (!toggle && !(guild.getObject(DataType.DISABLED_CATEGORIES) as ArrayList<String>).contains(category.toString().toLowerCase())) {
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

            else -> messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "guildsettings [category/clearstats/stats]", includePrefix = true)
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
