package org.woahoverflow.chad.commands.info

import org.json.JSONObject
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.Reddit
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap
import java.util.Objects

/**
 * Gets a top post from a subreddit
 *
 * @author sho, codebasepw
 */
class RedditTop : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val message = Objects.requireNonNull<IMessage>(messageHandler.sendMessage("Loading..."))
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // If there's no arguments
        if (args.isEmpty()) {
            message.delete()
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "rtop [subreddit name]")
            return
        }

        var post = Reddit.getPost(args[0], Reddit.PostType.NEW)

        if (post == null) {
            message.delete()
            messageHandler.sendError("Invalid Subreddit!")
            return
        }

        post = post!!.getJSONObject("data")

        val embedBuilder = EmbedBuilder()

        embedBuilder.withUrl("https://reddit.com" + post!!.getString("permalink"))
        embedBuilder.withTitle(post.getString("title"))
        embedBuilder.withDesc(String.format("**Vote**: %s / **Comments**: %s", post.getLong("ups"), post.getLong("num_comments")))
        embedBuilder.withImage(post.getString("url"))

        message.delete()
        messageHandler.credit(post.getString("subreddit_name_prefixed")).sendEmbed(embedBuilder)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["rtop [subreddit]"] = "Displays the hottest post from a subreddit."
        Command.helpCommand(st, "Reddit Top", e)
    }
}
