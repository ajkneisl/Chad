package org.woahoverflow.chad.commands.`fun`

import org.json.JSONArray
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap
import java.util.Objects

/**
 * Gets random cat pictures from an API
 *
 * @author sho
 */
class CatGallery : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        // The embed builder
        val embedBuilder = EmbedBuilder()

        // The API we use for our cat images :)
        val url = "https://api.thecatapi.com/v1/images/search?size=full"

        embedBuilder.withImage(
                Objects.requireNonNull<JSONArray>(JsonHandler.readArray(url)).getJSONObject(0).getString("url")
        )

        messageHandler.credit("thecatapi.com").sendEmbed(embedBuilder)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["catgallery"] = "Gives you a random cat picture."
        Command.helpCommand(st, "Cat Gallery", e)
    }
}
