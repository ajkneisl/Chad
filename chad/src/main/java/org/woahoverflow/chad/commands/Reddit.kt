package org.woahoverflow.chad.commands

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.Reddit
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
                , e, false)
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
    val message = MessageHandler(e.channel, e.author).sendMessage("Loading...")!!
    val messageHandler = MessageHandler(e.channel, e.author)

    // Checks if channel is Nsfw
    if (!e.channel.isNSFW && requiresNsfw) {
        RequestBuffer.request { message.edit(MessageHandler.Messages.CHANNEL_NOT_NSFW.message) }
        return
    }

    val post = Reddit.getPost(arrayList, Reddit.PostType.HOT)!!.getJSONObject("data")

    val embedBuilder = EmbedBuilder()

    embedBuilder.withUrl("https://reddit.com" + post.getString("permalink"))
    embedBuilder.withTitle(post.getString("title"))
    embedBuilder.withDesc("**Vote**: ${post.getLong("ups")} / **Comments**: ${post.getLong("num_comments")}")
    embedBuilder.withImage(post.getString("url"))

    message.delete()
    messageHandler.credit(post.getString("subreddit_name_prefixed")).sendEmbed(embedBuilder)
}