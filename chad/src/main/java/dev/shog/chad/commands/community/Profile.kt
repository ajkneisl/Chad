package dev.shog.chad.commands.community

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.PermissionHandler
import dev.shog.chad.framework.handle.PlayerHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import dev.shog.chad.framework.obj.Player
import dev.shog.chad.framework.util.Util
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Gets a user's Chad profile
 *
 * @author sho
 */
class Profile : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["profile"] = "View your Chad profile."
        st["profile [@user]"] = "View another user's Chad profile."
        Command.helpCommand(st, "Profile", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

        if (args.isNotEmpty()) {
            when (args[0].toLowerCase()) {
                "desc" -> {
                    if (args.size >= 2) {
                        val sb = StringBuilder()
                        args.removeAt(0)

                        for (arg in args) sb.append("$arg ")
                        val final = sb.toString().removeSuffix(" ")

                        if (final.length > 240) {
                            messageHandler.sendError("Your description must be shorter than 240 characters!")
                            return
                        }

                        PlayerHandler.getPlayer(e.author.longID).setObject(Player.DataType.PROFILE_DESCRIPTION, final)

                        messageHandler.sendMessage("Changed your description to `$final`.")
                        return
                    } else {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}profile desc [new description]")
                        return
                    }
                }

                "title" -> {
                    if (!PermissionHandler.isDeveloper(e.author)) {
                        messageHandler.sendPresetError(MessageHandler.Messages.USER_NOT_DEVELOPER)
                        return
                    }

                    if (args.size >= 2) {
                        val sb = StringBuilder()
                        args.removeAt(0)

                        for (arg in args) sb.append("$arg ")
                        val final = sb.toString().removeSuffix(" ")

                        if (final.length > 64) {
                            messageHandler.sendError("Your title must be shorter than 64 characters!")
                            return
                        }

                        PlayerHandler.getPlayer(e.author.longID).setObject(Player.DataType.PROFILE_TITLE, final)

                        messageHandler.sendMessage("Changed your title to `$final`.")
                        return
                    } else {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}profile title [new title]")
                        return
                    }
                }

                "other" -> {
                    if (!PermissionHandler.isDeveloper(e.author)) {
                        messageHandler.sendPresetError(MessageHandler.Messages.USER_NOT_DEVELOPER)
                        return
                    }

                    args.removeAt(0) // Removes the "other"

                    if (args.size < 3) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}profile other [@user] [title/description] [new title/description]")
                        return
                    }

                    if (e.message.mentions.isEmpty()) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}profile other [@user] [title/description] [new title/description]")
                        return
                    } else if (!args[0].contains(e.message.mentions[0].stringID, true)) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}profile other [@user] [title/description] [new title/description]")
                        return
                    }

                    val user = e.message.mentions[0]
                    args.removeAt(0) // Removes the user mention

                    when (args[0].toLowerCase()) {
                        "title" -> {
                            args.removeAt(0) // Removes "title"

                            val sb = StringBuilder()
                            args.removeAt(0)

                            for (arg in args) sb.append("$arg ")
                            val final = sb.toString().removeSuffix(" ")

                            if (final.length > 64) {
                                messageHandler.sendError("Your title must be shorter than 64 characters!")
                                return
                            }

                            PlayerHandler.getPlayer(user.longID).setObject(Player.DataType.PROFILE_TITLE, final)

                            messageHandler.sendMessage("Changed `${user.name}`'s title to `$final`.")
                            return
                        }

                        "desc" -> {
                            args.removeAt(0) // Removes "desc"

                            val sb = StringBuilder()

                            for (arg in args) sb.append("$arg ")
                            val final = sb.toString().removeSuffix(" ")

                            if (final.length > 240) {
                                messageHandler.sendError("Your description must be shorter than 240 characters!")
                                return
                            }

                            PlayerHandler.getPlayer(user.longID).setObject(Player.DataType.PROFILE_DESCRIPTION, final)

                            messageHandler.sendMessage("Changed `${user.name}`'s description to `$final`.")
                            return
                        }

                        else -> {
                            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}profile other [@user] [title/description] [new title/description]")
                            return
                        }
                    }
                }
            }
        }

        val user: IUser = if (e.message.mentions.isEmpty()) {
            e.author
        } else {
            e.message.mentions[0]
        }

        // The user's player instance
        val userPlayer = PlayerHandler.getPlayer(user.longID)

        // The title and description for the embed builder
        var title: String = user.name + if (userPlayer.getObject(Player.DataType.PROFILE_TITLE) as String == "none") {" "} else {": " + userPlayer.getObject(Player.DataType.PROFILE_TITLE) as String}
        var description = ""

        val upvote: Long = userPlayer.getObject(Player.DataType.PROFILE_UPVOTE) as Long
        val downvote: Long = userPlayer.getObject(Player.DataType.PROFILE_DOWNVOTE) as Long
        val vote: Long = upvote-downvote

        title += " :  $vote (+$upvote | -$downvote)"

        // If the user's vote is negative
        if (0 > vote) {
            description += "\n**Warning**: This user has a bad reputation!"
        }

        // The player's chosen description
        val profileDescription: String = userPlayer.getObject(Player.DataType.PROFILE_DESCRIPTION) as String
        description += "\n\n$profileDescription"

        val marryData: String = userPlayer.getObject(Player.DataType.MARRY_DATA) as String
        if (!marryData.equals("none&none", true)) {
            val splitMarryData = marryData.split("&")
            if (Util.guildExists(e.client, splitMarryData[1].toLong())) {
                if (e.client.getGuildByID(splitMarryData[1].toLong()).getUserByID(splitMarryData[0].toLong()) != null) {
                    description += "\n\nMarried to ${e.client.getGuildByID(splitMarryData[1].toLong()).getUserByID(splitMarryData[0].toLong())}"
                }
            }
        }

        MessageHandler(e.channel, e.author).sendEmbed {
           withImage(user.avatarURL)
            withDesc(description)
            withTitle(title)
        }
    }
}