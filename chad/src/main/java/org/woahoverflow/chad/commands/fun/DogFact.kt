package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Gets a dog fact from an API
 *
 * @author sho
 */
class DogFact : Command.Class {
    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["dogfact"] = "Gets a dog fact."
        return Command.helpCommand(st, "Dog Fact", e)
    }

    override fun run(e: MessageEvent, args: MutableList<String>?): Runnable {
        return Runnable {
            // Gets the fact
            val fact = JsonHandler.read("https://dog-api.kinduff.com/api/facts")!!.getJSONArray("facts").getString(0)

            // Sends the fact
            MessageHandler(e.channel, e.author).credit("dog-api.kinduff.com").sendEmbed(EmbedBuilder().withDesc(fact))

            // i don't even know how i could comprehend something so complicated like this
        }
    }
}