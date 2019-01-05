package org.woahoverflow.chad.commands.community

import org.woahoverflow.chad.framework.Util
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Player
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Gets a user's Chad profile
 *
 * @author sho
 */
class Profile : Command.Class {
    override fun help(e: MessageReceivedEvent?): Runnable {
        val st = HashMap<String, String>()
        st["profile"] = "View your Chad profile."
        st["profile <@user>"] = "View another user's Chad profile."
        return Command.helpCommand(st, "Profile", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val user: IUser = if (e.message.mentions.isEmpty()) {
                e.author
            } else {
                e.message.mentions[0]
            }

            // The user's player instance
            val userPlayer = PlayerHandler.handle.getPlayer(user.longID)

            // The title and description for the embed builder
            var title: String = user.name + " " + if (userPlayer.getObject(Player.DataType.PROFILE_TITLE) as String == "none") {""} else {userPlayer.getObject(Player.DataType.PROFILE_TITLE) as String}
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

            MessageHandler(e.channel, e.author).sendEmbed(
                    EmbedBuilder()
                            .withImage(user.avatarURL)
                            .withDesc(description)
                            .withTitle(title)
            )
        }
    }
}