package org.woahoverflow.chad.commands

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.Reddit
import org.woahoverflow.chad.framework.handle.coroutine.request
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.HashMap

/**
 * Sends a random meme to a channel
 *
 * @type Fun
 * @author sho
 */
class Meme : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["meme"] = "Gets a random meme"

        Command.helpCommand(st, "Meme", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        send(arrayListOf(
                "blackpeopletwitter",
                "memes",
                "dankmemes",
                "me_irl",
                "2meirl4meirl",
                "cursedimages",
                "wholesomememes",
                "pewdiepiesubmissions",
                "terriblefacebookmemes",
                "memeeconomy")
                , e, requiresNsfw = false)
    }
}
/**
 * Sends pornographic content to a channel
 *
 * @type NSFW
 * @author sho
 */
class Porn : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        send(arrayListOf(
                "beachgirls",
                "collegensfw",
                "LegalTeens",
                "RealGirls",
                "NSFW_Japan",
                "SexyButNotPorn"
        ), e)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["porn"] = "Gets Porn"
        Command.helpCommand(st, "Porn", e)
    }
}

/**
 * Sends hentai related content to a channel
 *
 * @type NSFW
 * @author sho
 */
class Hentai : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["hentai"] = "Gets Hentai from a random hentai-related subreddit."
        Command.helpCommand(st, "Hentai", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        send(arrayListOf("hentai",
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
                "uncensoredhentai"), e)
    }
}

/**
 * Sends a Reddit post to a channel.
 *
 * @param arrayList The different subreddits to choose from.
 * @param e The message event from the command.
 */
private fun send(arrayList: ArrayList<String>, e: MessageEvent, requiresNsfw: Boolean = true) {
    MessageHandler(e.channel, e.author).also { handle ->
        if (e.message.content.contains("rfall")) {
            handle.sendMessage("Refreshing all...").also {
                Reddit.getPost(arrayList, Reddit.PostType.HOT)
                if (it != null) request { it.edit("Complete!") }
                return
            }
        }

        handle.sendMessage("Loading...").also { msg ->
            if (msg == null) throw IllegalArgumentException("REQ_NULL")
            if (!e.channel.isNSFW && requiresNsfw) {
                request { msg.edit(MessageHandler.Messages.CHANNEL_NOT_NSFW.message) }
                return
            }

            handle.sendEmbed {
                val post = Reddit.getPost(arrayList, Reddit.PostType.HOT)!!.getJSONObject("data")
                withUrl("https://reddit.com" + post.getString("permalink"))
                withTitle(post.getString("title"))
                withDesc("**Vote**: ${post.getLong("ups")} / **Comments**: ${post.getLong("num_comments")}")
                withImage(post.getString("url"))

                request { msg.delete() }
                handle.credit(post.getString("subreddit_name_prefixed"))
            }
        }
    }
}