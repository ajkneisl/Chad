package dev.shog.chad.commands

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.PermissionHandler
import dev.shog.chad.framework.handle.Reddit
import dev.shog.chad.framework.handle.coroutine.request
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.util.createMessageHandler
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.HashMap

/**
 * Sends a new post from a selected Sub Reddit
 *
 * @type Info
 * @author sho
 */
class RedditNew : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["rnew [subreddit]"] = "Displays the most recent post from a subreddit."

        Command.helpCommand(st, "Reddit New", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        if (args.isEmpty()) {
            e.createMessageHandler().sendError("Please include the name of the subreddit!")
            return
        }

        send(arrayListOf(args[0]), e, requiresNsfw = false, postType = Reddit.PostType.NEW)
    }
}

/**
 * Sends a new post from a selected Sub Reddit
 *
 * @type Info
 * @author sho
 */
class RedditTop : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["rtop [subreddit]"] = "Displays the top post from a subreddit."

        Command.helpCommand(st, "Reddit Top", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        if (args.isEmpty()) {
            e.createMessageHandler().sendError("Please include the name of the subreddit!")
            return
        }

        send(arrayListOf(args[0]), e, requiresNsfw = false, postType = Reddit.PostType.TOP)
    }
}

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
private fun send(arrayList: ArrayList<String>, e: MessageEvent, requiresNsfw: Boolean = true, postType: Reddit.PostType = Reddit.PostType.HOT) {
    e.createMessageHandler().also { handle ->
        if (e.message.content.contains("rfall") && PermissionHandler.isDeveloper(e.author)) {
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

            val post = Reddit.getPost(arrayList, postType, saveAll = false)?.getJSONObject("data").also {
                if (it == null) {
                    request { msg.edit("Invalid subreddit!") }
                    return
                }
            }!!

            handle.sendEmbed {
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