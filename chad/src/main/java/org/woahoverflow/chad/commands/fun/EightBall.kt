package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*

/**
 * Shake the 8ball
 *
 * @author sho
 */
class EightBall : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        MessageHandler(e.channel, e.author).also {
            if (args.isEmpty()) {
                it.sendError("You didn't ask a question!")
                return
            }
        }.sendEmbed { withDesc(ChadVar.eightBallResults.random()) }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["8ball [question]"] = "The eight ball always answers your best questions."
        Command.helpCommand(st, "Eight Ball", e)
    }
}
