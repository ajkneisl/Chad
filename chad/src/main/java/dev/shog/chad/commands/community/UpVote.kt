package dev.shog.chad.commands.community

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.PlayerHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import dev.shog.chad.framework.obj.Player.DataType
import org.json.JSONArray
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * UpVote a player's profile
 *
 * @author sho
 */
class UpVote : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        if (e.message.mentions.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "upvote [@user]")
            return
        }

        // Target
        val target = e.message.mentions[0]

        // Makes sure they're not upvoting themselves
        if (target == e.author) {
            messageHandler.sendError("You can't vote on that user!")
            return
        }

        // The author's player instance
        val author = PlayerHandler.getPlayer(e.author.longID)

        val voteData = JSONArray(author.getObject(DataType.VOTE_DATA))

        if (voteData.contains(target.longID)) {
            messageHandler.sendError("You've already voted on this user!")
            return
        }

        // Add the voted user to the array
        voteData.put(target.longID)

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
                DataType.PROFILE_UPVOTE, targetPlayerUpvote + 1L
        )

        messageHandler.sendEmbed(EmbedBuilder().withDesc(
                "You upvoted `" + target.name + "`!\n"
                        + "Their vote is now `" + (targetPlayerUpvote + 1L - targetPlayerDownvote) + "`!"
        ))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["upvote [@user]"] = "Upvotes a user's Chad profile."
        Command.helpCommand(st, "UpVote", e)
    }
}
