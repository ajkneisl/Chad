package org.woahoverflow.chad.commands.nsfw

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.Reddit
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Sends a NSFW picture utilizing Reddit
 *
 * @author sho,
 */
class Porn : Command.Class {
    override fun run(e: MessageEvent, args: List<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)

            // Checks if channel is Nsfw
            if (!e.channel.isNSFW) {
                messageHandler.sendPresetError(MessageHandler.Messages.CHANNEL_NOT_NSFW)
                return@Runnable
            }

            // Picks a subreddit out of the list, and sends about it
            val subreddits = arrayListOf(
                    "beachgirls",
                    "collegensfw",
                    "LegalTeens",
                    "RealGirls",
                    "NSFW_Japan",
                    "SexyButNotPorn"
            )

            val post = Reddit.getPost(subreddits, Reddit.PostType.HOT)!!.getJSONObject("data")

            val embedBuilder = EmbedBuilder()

            embedBuilder.withUrl("https://reddit.com" + post.getString("permalink"))
            embedBuilder.withTitle(post.getString("title"))
            embedBuilder.withDesc("**Vote**: ${post.getLong("ups")} / **Comments**: ${post.getLong("num_comments")}")
            embedBuilder.withImage(post.getString("url"))

            messageHandler.credit(post.getString("subreddit_name_prefixed")).sendEmbed(embedBuilder)
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["porn"] = "Gets Porn"
        return Command.helpCommand(st, "Porn", e)
    }
}
