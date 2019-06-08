package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.framework.handle.LeaderboardHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*

/**
 * Gives a leaderboard.
 *
 * @author sho
 */
class Leaderboard: Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["leaderboard [money/xp]"] = "Get the leaderboard of the specific type."
        Command.helpCommand(st, "Leaderboard", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        if (args.size != 1) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "leaderboard [money/xp]", includePrefix = true)
            return
        }

        when (args[0].toLowerCase()) {
            "money" -> LeaderboardHandler.moneyLeaderBoard.getLeaderBoard().also { lb ->
                StringBuilder().apply {
                    for (i in 1..lb.size) {
                        append("**[$i]** `${lb[i]!!.user.name}`: `${lb[i]!!.bal}`\n")
                    }
                }.toString().removeSuffix("\n").also { msg ->
                    messageHandler.sendEmbed {
                        withDesc(msg)
                        withTitle("Money Leaderboard")
                    }
                }
            }

            "xp" -> LeaderboardHandler.xpLeaderBoard.getLeaderBoard().also { lb ->
                StringBuilder().apply {
                    for (i in 1..lb.size) {
                        append("**[$i]** `${lb[i]!!.user.name}`: `${lb[i]!!.xp}`\n")
                    }
                }.toString().removeSuffix("\n").also { msg ->
                    messageHandler.sendEmbed {
                        withDesc(msg)
                        withTitle("XP Leaderboard")
                    }
                }
            }

            else -> {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "leaderboard [money/xp]", includePrefix = true)
                return
            }
        }
    }
}