package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.util.Reddit
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*
import java.util.Random

/**
 * Gets a meme from a randomly selected subreddit
 * (don't get offended by any of the subreddits or their contents, as it's not my material)
 *
 * @author sho
 */
class Meme : Command.Class {
    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["meme"] = "Get a meme from random subreddits."
        return Command.helpCommand(st, "Meme", e)
    }

    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            // Picks a subreddit out of the list, and sends about it
            val subreddits = listOf("blackpeopletwitter", "memes", "dankmemes", "me_irl", "2meirl4meirl", "cursedimages", "wholesomememes", "pewdiepiesubmissions", "terriblefacebookmemes", "memeeconomy")
            val subreddit = Random().nextInt(subreddits.size)
            Reddit().sendHotPost(e, subreddits[subreddit])
        }
    }
}