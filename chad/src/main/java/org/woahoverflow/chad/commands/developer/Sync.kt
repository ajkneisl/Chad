package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.LeaderboardHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.coroutine.request
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.sync.sync
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IMessage
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
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}sync [website]")
            return
        }

        when (args[0].toLowerCase()) {
            "website" -> {
                sync(e.client)

                messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully synced with website!"))

                return
            }

            "leaderboard" -> {
                val message = request {
                    e.channel.sendMessage("Syncing leaderboards...")
                }.also {
                    if (it.result !is IMessage) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
                        return
                    }
                }.result as IMessage

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