package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.LeaderboardHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Gives a leaderboard..
 */
class Leaderboard: Command.Class {
    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["leaderboard [money/xp/world/total]"] = "Get the leaderboard of the specific type."
        return Command.helpCommand(st, "Leaderboard", e)
    }

    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)
            val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

            if (args.size != 1) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}leaderboard [money/xp/total]")
                return@Runnable
            }

            when (args[0].toLowerCase()) {
                "money" -> {
                    val leaderboard = LeaderboardHandler.moneyLeaderBoard.getLeaderBoard()
                    val sb = StringBuilder()

                    for (i in 1..leaderboard.size) sb.append("**[$i]** `${leaderboard[i]!!.user.name}`: `${leaderboard[i]!!.bal}`\n")

                    messageHandler.sendEmbed(EmbedBuilder().withTitle("Money Leaderboard").withDesc(sb.toString().removeSuffix("\n")))

                    return@Runnable
                }

                "xp" -> {
                    val leaderboard = LeaderboardHandler.xpLeaderBoard.getLeaderBoard()
                    val sb = StringBuilder()

                    for (i in 1..leaderboard.size) sb.append("**[$i]** `${leaderboard[i]!!.user.name}`: `${leaderboard[i]!!.xp}`\n")

                    messageHandler.sendEmbed(EmbedBuilder().withTitle("XP Leaderboard").withDesc(sb.toString().removeSuffix("\n")))
                }

                else -> {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}leaderboard [money/xp/total]")
                    return@Runnable
                }
            }
        }
    }

}