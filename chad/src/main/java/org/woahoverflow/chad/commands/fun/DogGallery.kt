package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Sends pictures of dogs :)
 *
 * @author sho
 */
class DogGallery : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["doggallery"] = "Get a picture of a dog."
        Command.helpCommand(st, "Dog Gallery", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        messageHandler
                .credit("thedogapi.com")
                .sendEmbed {
                    val request = JsonHandler.readArray("https://api.thedogapi.com/v1/images/search?size=full")!!

                    withImage(request.getJSONObject(0).getString("url"))

                    if (!request.getJSONObject(0).getJSONArray("breeds").isEmpty) {
                        val dog = request.getJSONObject(0).getJSONArray("breeds").getJSONObject(0)

                        val desc = "Their temperaments are ${dog.getString("temperament").toLowerCase()}. They live to around ${dog.getString("life_span")} years old."

                        withTitle(dog.getString("name"))
                        withDesc(desc)
                    }
                }
    }
}