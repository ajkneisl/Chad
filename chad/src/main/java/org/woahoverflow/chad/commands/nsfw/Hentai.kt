package org.woahoverflow.chad.commands.nsfw

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PostType
import org.woahoverflow.chad.framework.handle.getPost
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Sends a NSFW Hentai image, utilizing Reddit
 *
 * @author sho
 */
class Hentai : Command.Class {
    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["hentai"] = "Gets hentai from a random hentai-related subreddit."
        return Command.helpCommand(st, "Hentai", e)
    }

    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)

            // Makes sure the channel is NSFW
            if (!e.channel.isNSFW) {
                messageHandler.sendPresetError(MessageHandler.Messages.CHANNEL_NOT_NSFW)
                return@Runnable
            }

            // Picks a subreddit out of the list, and sends about it
            val subreddits = arrayListOf(
                    "hentai",
                    "ecchi",
                    "thick_hentai",
                    "rule34",
                    "futanari",
                    "WesternHentai",
                    "pantsu",
                    "hentaibondage",
                    "MonsterGirl",
                    "yuri",
                    "Naruto_Hentai",
                    "HQHentai",
                    "yaoi",
                    "traphentai",
                    "thighhighhentai",
                    "OppaiLove",
                    "uncensoredhentai"
            )

            val post = getPost(subreddits, PostType.HOT)!!.getJSONObject("data")

            val embedBuilder = EmbedBuilder()

            embedBuilder.withUrl("https://reddit.com" + post.getString("permalink"))
            embedBuilder.withTitle(post.getString("title"))
            embedBuilder.withDesc("**Vote**: ${post.getLong("ups")} / **Comments**: ${post.getLong("num_comments")}")
            embedBuilder.withImage(post.getString("url"))

            messageHandler.credit(post.getString("subreddit_name_prefixed")).sendEmbed(embedBuilder)
        }
    }
}