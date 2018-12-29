package org.woahoverflow.chad.commands.`fun`

import org.json.JSONException
import org.json.JSONObject
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.HashMap
import java.util.Random

/**
 * Gets a meme from a randomly selected subreddit
 * (don't get offended by any of the subreddits or their contents, as it's not my material)
 *
 * @author sho
 * @since 0.7.0
 */
class Meme : Command.Class {
    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["meme"] = "Get a meme from random subreddits."
        return Command.helpCommand(st, "Meme", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            // Picks a subreddit out of the list, and sends about it
            val subreddits = listOf("blackpeopletwitter", "memes", "dankmemes", "me_irl", "2meirl4meirl", "cursedimages", "wholesomememes", "pewdiepiesubmissions", "terriblefacebookmemes", "memeeconomy")
            val subreddit = Random().nextInt(subreddits.size)
            sendHotPost(e, subreddits[subreddit])
        }
    }

    private fun sendHotPost(e: MessageReceivedEvent, subreddit: String) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val subredditJson: JSONObject?
        var post: JSONObject?
        try {
            // Gets post
            subredditJson = JsonHandler.handle.read("https://reddit.com/r/$subreddit/hot.json")

            // If it's not found
            if (subredditJson == null) {
                messageHandler.sendError("Invalid Subreddit")
                return
            }

            // If there's no posts in the subreddit
            if (subredditJson.getJSONObject("data").getJSONArray("children").isEmpty) {
                messageHandler.sendError("Invalid Subreddit")
                return
            }

            // Gets a random post in the subreddit
            var index = Random().nextInt(subredditJson.getJSONObject("data").getJSONArray("children").length())

            // The amount of posts parsed through
            var parsed = 0

            // Gets the subreddit
            parsed++
            post = subredditJson.getJSONObject("data")
                    .getJSONArray("children")
                    .getJSONObject(index)
                    .getJSONObject("data")


            // Makes sure the post isn't stickied or if the post is NSFW
            while (post!!.getBoolean("stickied") || (post.getBoolean("over_18") && !e.channel.isNSFW)) {

                if (subredditJson.getJSONObject("data").getJSONArray("children").length() == parsed) {
                    messageHandler.sendError("Failed to find a post!")
                    return
                }

                index = Random().nextInt(subredditJson.getJSONObject("data").getJSONArray("children").length())
                parsed++
                post = subredditJson
                        .getJSONObject("data")
                        .getJSONArray("children")
                        .getJSONObject(index)
                        .getJSONObject("data")
            }
        } catch (e1: JSONException) {
            e1.printStackTrace()
            messageHandler.sendError("Invalid Subreddit")
            return
        } catch (e1: RuntimeException) {
            messageHandler.sendError("Invalid Subreddit")
            return
        }

        val embedBuilder = EmbedBuilder()
        embedBuilder.withTitle(post.getString("title"))
        embedBuilder.withDesc(post.getString("author"))
        embedBuilder.appendField("Score", post.getInt("score").toString(), true)
        embedBuilder.appendField("Comments", Integer.toString(post.getInt("num_comments")), true)
        embedBuilder.withImage(post.getString("url"))
        embedBuilder.withUrl("https://reddit.com" + post.getString("permalink"))
        messageHandler.credit("r/$subreddit").sendEmbed(embedBuilder)
    }
}