package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player
import org.woahoverflow.chad.framework.obj.Player.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder

import java.util.ArrayList
import java.util.HashMap

/**
 * Downvotes a player's profile
 *
 * @author sho
 */
class DownVote : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String //

        if (e.message.mentions.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.NO_MENTIONS, prefix + "downvote [@user]")
            return
        }

        // Target
        val target = e.message.mentions[0]

        // Makes sure they're not downvoting themselves
        if (target == e.author) {
            messageHandler.sendError("You can't vote on that user!")
            return
        }

        // The author's player instance
        val author = PlayerHandler.getPlayer(e.author.longID)

        val voteData = author.getObject(DataType.VOTE_DATA) ?: return

        try {
            voteData as ArrayList<Long>
        } catch (ex: Exception) { return }

        if (voteData.contains(target.longID)) {
            messageHandler.sendError("You've already voted on this user!")
            return
        }

        // Add the voted user to the array
        voteData.add(target.longID)

        // ReSet it
        author.setObject(DataType.VOTE_DATA, voteData)

        // The target user's player instance
        val targetPlayer = PlayerHandler.getPlayer(target.longID)

        // The target user's upvote amount
        val targetPlayerUpvote = targetPlayer.getObject(DataType.PROFILE_UPVOTE) as Long

        // The target user's downvote amount
        val targetPlayerDownvote = targetPlayer.getObject(DataType.PROFILE_DOWNVOTE) as Long

        // Update values
        targetPlayer.setObject(
                DataType.PROFILE_DOWNVOTE, targetPlayerDownvote + 1L
        )

        messageHandler.sendEmbed(EmbedBuilder().withDesc(
                "You downvoted `" + target.name + "`!\n"
                        + "Their vote is now `" + (targetPlayerUpvote - (targetPlayerDownvote + 1L)) + "`!"
        ))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["downvote [@user]"] = "Downvotes a user's Chad profile."
        Command.helpCommand(st, "DownVote", e)
    }
}
