package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Modify values from the database
 *
 * @author sho
 */
class ModifyDatabase : Command.Class {
    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["moddb <guild/user> <id> <data type> <new value>"] = "Modifies a database entry."
        return Command.helpCommand(st, "Modify Database", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)
            val prefix = GuildHandler.handle.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

            if (args.isEmpty() || args.size < 4) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddb **guild/user** **id** **object** **new value**")
                return@Runnable
            }

            when (args[0].toLowerCase()) {
                // If they're modifying values for a guild
                "guild" -> {
                    // Checks if the inputted value is a valid long
                    try {
                        args[1].toLong()
                    } catch (ex: NumberFormatException) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[0])
                        return@Runnable
                    }

                    val id = args[1].toLong()

                    // Checks if the guild is cached, if not deny
                    if (!GuildHandler.handle.guildExists(id)) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[0])
                        return@Runnable
                    }

                    val guild = GuildHandler.handle.getGuild(id)

                    var datatype: Guild.DataType? = null

                    for (dt in Guild.DataType.values()) {
                        if (dt.toString().equals(args[2], true)) {
                            datatype = dt
                        }
                    }

                    if (datatype == null) {
                        messageHandler.sendError("Invalid Type!")
                        return@Runnable
                    }

                    if (args[3].endsWith("!long")) {
                        val new: Long

                        try {
                            new = args[3].removeSuffix("!long").toLong()
                        } catch (ex: NumberFormatException) {
                            messageHandler.sendError("Invalid Long!")
                            return@Runnable
                        }

                        guild.setObject(datatype, new)
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully changed `${datatype.toString().toLowerCase()}` to `$new` in guild `$id`."))
                        return@Runnable
                    }

                    if (args[3].endsWith("!int")) {
                        val new: Int

                        try {
                            new = args[3].removeSuffix("!int").toInt()
                        } catch (ex: NumberFormatException) {
                            messageHandler.sendError("Invalid Integer!")
                            return@Runnable
                        }

                        guild.setObject(datatype, new)
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully changed `${datatype.toString().toLowerCase()}` to `$new` in guild `$id`."))
                        return@Runnable
                    }

                    args.removeAt(0) // Removes guild
                    args.removeAt(0) // Removes id
                    args.removeAt(0) // Removes data type

                    val stringBuilder = StringBuilder()
                    for (string in args) {
                        stringBuilder.append("$string ")
                    }

                    guild.setObject(datatype, stringBuilder.toString().trim())
                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully changed `${datatype.toString().toLowerCase()}` to `${stringBuilder.toString().trim()}` in guild `$id`."))
                }

                // If they're modifying values for a user
                "user" -> {
                    // Checks if the inputted value is a valid long
                    try {
                        args[1].toLong()
                    } catch (ex: NumberFormatException) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[0])
                        return@Runnable
                    }

                    val id = args[1].toLong()

                    // Checks if the player is cached, if not deny
                    if (!PlayerHandler.handle.playerExists(id)) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ID, args[0])
                        return@Runnable
                    }

                    val player = PlayerHandler.handle.getPlayer(id)

                    var datatype: Player.DataType? = null

                    for (dt in Player.DataType.values()) {
                        if (dt.toString().equals(args[2], true)) {
                            datatype = dt
                        }
                    }

                    if (datatype == null) {
                        messageHandler.sendError("Invalid Type!")
                        return@Runnable
                    }

                    if (args[3].endsWith("!long")) {
                        val new: Long

                        try {
                            new = args[3].removeSuffix("!long").toLong()
                        } catch (ex: NumberFormatException) {
                            messageHandler.sendError("Invalid Long!")
                            return@Runnable
                        }

                        player.setObject(datatype, new)
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully changed `${datatype.toString().toLowerCase()}` to `$new` on player `$id`."))
                        return@Runnable
                    }

                    if (args[3].endsWith("!int")) {
                        val new: Int

                        try {
                            new = args[3].removeSuffix("!int").toInt()
                        } catch (ex: NumberFormatException) {
                            messageHandler.sendError("Invalid Integer!")
                            return@Runnable
                        }

                        player.setObject(datatype, new)
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully changed `${datatype.toString().toLowerCase()}` to `$new` on player `$id`."))
                        return@Runnable
                    }

                    args.removeAt(0) // Removes player
                    args.removeAt(0) // Removes id
                    args.removeAt(0) // Removes data type

                    val stringBuilder = StringBuilder()
                    for (string in args) {
                        stringBuilder.append("$string ")
                    }

                    player.setObject(datatype, stringBuilder.toString().trim())
                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully changed `${datatype.toString().toLowerCase()}` to `${stringBuilder.toString().trim()}` in guild `$id`."))
                }

                else -> {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}moddb **guild/user** **id** **object** **new value**")
                    return@Runnable
                }
            }
        }
    }
}