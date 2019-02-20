package org.woahoverflow.chad.commands.nsfw

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.util.Reddit
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
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
            val subreddits = listOf(
                    "hentai",
                    "animemilfs",
                    "ecchi",
                    "thick_hentai",
                    "rule34",
                    "futanari",
                    "HENTAI_GIF",
                    "WesternHentai",
                    "pantsu",
                    "hentaibondage",
                    "MonsterGirl", "yuri",
                    "Naruto_Hentai",
                    "HQHentai",
                    "yaoi",
                    "traphentai",
                    "thighhighhentai",
                    "OppaiLove",
                    "uncensoredhentai"
            )
            val subreddit = Random().nextInt(subreddits.size)
            Reddit().sendHotPost(e, subreddits[subreddit])
        }
    }
}