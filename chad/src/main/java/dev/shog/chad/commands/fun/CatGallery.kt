package dev.shog.chad.commands.`fun`

import org.json.JSONArray
import dev.shog.chad.framework.handle.JsonHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
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

        messageHandler
                .credit("thecatapi.com")
                .sendEmbed {
            val data = JsonHandler.readArray("https://api.thecatapi.com/v1/images/search?size=full")!!.getJSONObject(0)


            withImage(data.getString("url"))
            if (data.getJSONArray("breeds").isEmpty) {
                return@sendEmbed
            } else { // Not all of requests return a specific breed.
                val breed = data.getJSONArray("breeds").getJSONObject(0)!!

                withTitle(breed.getString("name"))
                withDesc(breed.getString("description"))
            }
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["catgallery"] = "Gives you a random cat picture."
        Command.helpCommand(st, "Cat Gallery", e)
    }
}
