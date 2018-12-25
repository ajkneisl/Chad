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
        st["meme"] = "Get a meme from a varied amount of subreddits."
        return Command.helpCommand(st, "Meme", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val subreddit = Random().nextInt(6)

            when (subreddit) {
                0 -> {
                    sendHotPost(e, "blackpeopletwitter")
                }

                1 -> {
                    sendHotPost(e, "memes")
                }

                2 -> {
                    sendHotPost(e, "dankmemes")
                }

                3 -> {
                    sendHotPost(e, "me_irl")
                }

                4 -> {
                    sendHotPost(e, "wholesomememes")
                }

                5 -> {
                    sendHotPost(e, "facepalm")
                }

                else -> sendHotPost(e, "memes")
            }
        }
    }

    private fun sendHotPost(e: MessageReceivedEvent, subreddit: String) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val subredditJson: JSONObject?
        var post: JSONObject?
        try {
            // Gets post
            subredditJson = JsonHandler.handle.read("https://reddit.com/r/$subreddit/hot.json")

            if (subredditJson == null) {
                messageHandler.sendError("Invalid Subreddit")
                return
            }

            if (subredditJson.getJSONObject("data").getJSONArray("children").isEmpty) {
                messageHandler.sendError("Invalid Subreddit")
                return
            }

            var index = Random().nextInt(subredditJson.getJSONObject("data").getJSONArray("children").length())
            post = subredditJson.getJSONObject("data")
                    .getJSONArray("children")
                    .getJSONObject(index)
                    .getJSONObject("data")


            // Makes sure the post isn't stickied
            while (post!!.getBoolean("stickied")) {
                index++
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