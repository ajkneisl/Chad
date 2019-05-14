package org.woahoverflow.chad.commands.`fun`

import org.json.JSONArray
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.security.SecureRandom
import java.util.HashMap
import java.util.Objects

/**
 * Shake the 8ball
 *
 * @author sho
 */
class EightBall : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        // Makes sure they asked a question
        if (args.isEmpty()) {
            messageHandler.sendError("You didn't ask a question!")
            return
        }

        // Gets the answers from the cdn
        val answers = Objects.requireNonNull<JSONArray>(JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json"))

        // Sends the answer
        messageHandler.sendEmbed(
                EmbedBuilder().withDesc(
                        answers.getString(SecureRandom().nextInt(Objects.requireNonNull(answers).length()))
                )
        )
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["8ball [question]"] = "The eight ball always answers your best questions."
        Command.helpCommand(st, "Eight Ball", e)
    }
}
