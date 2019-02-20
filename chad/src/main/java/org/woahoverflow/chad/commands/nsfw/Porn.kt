package org.woahoverflow.chad.commands.nsfw

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.util.Reddit
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
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
            val subreddits = listOf(
                    "gonewild",
                    "workgonewild",
                    "gonewild30plus",
                    "CoffeeGoneWild",
                    "GoneWildStories",
                    "GoneWildCurvy",
                    "GWNerdy",
                    "braww",
                    "beachgirls",
                    "cumsluts",
                    "girlsinyogapants",
                    "tight_shorts",
                    "TightShorts",
                    "YogaPants",
                    "TightShirts",
                    "AthleticWearPorn",
                    "ToplessInJeans",
                    "Upskirt",
                    "collegesluts",
                    "CollegeAmateurs",
                    "collegensfw",
                    "LegalTeens",
                    "RealGirls",
                    "bikinis",
                    "bikinibridge",
                    "AsianHotties",
                    "WomenOfColor",
                    "NSFW_Japan",
                    "Unashamed",
                    "BeautifulTitsAndAss",
                    "FuckMarryOrKill",
                    "sexy",
                    "SexyButNotPorn"
            )
            val subreddit = Random().nextInt(subreddits.size)
            Reddit().sendHotPost(e, subreddits[subreddit])
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["porn"] = "Gets Porn"
        return Command.helpCommand(st, "Porn", e)
    }
}
