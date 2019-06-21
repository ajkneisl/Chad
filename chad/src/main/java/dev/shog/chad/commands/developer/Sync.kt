package dev.shog.chad.commands.developer

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.LeaderboardHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.coroutine.asIMessage
import dev.shog.chad.framework.handle.coroutine.request
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * Ability to sync with other applications through Discord
 *
 * @author sho
 */
class Sync : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val hash = HashMap<String, String>()
        hash["sync [type]"] = "Syncs with other applications."
        Command.helpCommand(hash, "Sync", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        if (args.size == 0) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}sync [leaderboard]")
            return
        }

        when (args[0].toLowerCase()) {
            "leaderboard" -> {
                val message = request {
                    e.channel.sendMessage("Syncing leaderboards...")
                }.asIMessage()

                val ref = LeaderboardHandler.refreshLeaderboard(LeaderboardHandler.LeaderboardType.MONEY)
                val ref2 = LeaderboardHandler.refreshLeaderboard(LeaderboardHandler.LeaderboardType.XP)

                RequestBuffer.request {
                    message.edit("Synced the money leaderboard with `${ref.am}` users in `${ref.time}`ms" +
                            "\nSynced the XP leaderboard with `${ref2.am}` users in `${ref.time}`ms")
                }
            }

            else -> {
                messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid sync option!"))

                return
            }
        }
    }
}