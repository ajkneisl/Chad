package org.woahoverflow.chad.commands.`fun`

import org.json.JSONObject
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap
import java.util.Objects

/**
 * Gets a random cat fact from an API
 *
 * @author sho
 */
class CatFact : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        // Gets the fact
        val fact = Objects.requireNonNull<JSONObject>(JsonHandler.read("https://catfact.ninja/fact")).getString("fact")

        // Sends the fact
        MessageHandler(e.channel, e.author).credit("catfact.ninja").sendEmbed(EmbedBuilder().withDesc(fact))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["catfact"] = "Gives you a random catfact."
        Command.helpCommand(st, "Cat Fact", e)
    }
}
