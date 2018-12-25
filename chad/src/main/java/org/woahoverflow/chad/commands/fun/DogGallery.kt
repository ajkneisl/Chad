package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.HashMap

/**
 * Sends pictures of dogs :)
 *
 * @author sho
 * @since 0.7.1
 */
class DogGallery : Command.Class {
    override fun help(e: MessageReceivedEvent?): Runnable {
        val st = HashMap<String, String>()
        st["doggallery"] = "Get a picture of a dog."
        return Command.helpCommand(st, "Dog Gallery", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)

            // The embed builder
            val embedBuilder = EmbedBuilder()

            // The API we use for our dog images :)
            val url = "https://api.thedogapi.com/v1/images/search?size=full"

            val response = JsonHandler.handle.readArray(url)

            embedBuilder.withImage(
                    response.getJSONObject(0).getString("url")
            )

            if (response.getJSONObject(0).getJSONArray("breeds").length() != 0) {
                val dog = response.getJSONObject(0).getJSONArray("breeds").getJSONObject(0)
                val desc = "**Breed** ${dog.getString("name")}" +
                        "\n**Life Span** ${dog.getString("life_span")}" +
                        "\n**Temperament** ${dog.getString("temperament")}"

                embedBuilder.withDesc(desc)
            }

            messageHandler.credit("thedogapi.com").sendEmbed(embedBuilder)
        }
    }
}