package org.woahoverflow.chad.commands.community

import kotlinx.coroutines.delay
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player.DataType
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IReaction
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import sx.blah.discord.util.RequestBuilder
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Marry a user
 *
 * @see DivorcePlayer
 *
 * @author sho
 */
class MarryPlayer : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // If they didn't mention anyone
        if (e.message.mentions.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "marry [@user]")
            return
        }

        // The other person
        val otherPerson = e.message.mentions[0]

        // Make sure they're not marrying Chad or themselves
        if (otherPerson == e.author || otherPerson == e.client.ourUser) {
            messageHandler.sendError("You can't marry that person!")
            return
        }

        // The author's player instance
        val player = PlayerHandler.getPlayer(e.author.longID)

        // Player's marry data, in format `player_id&guild_id`
        val playerMarryData = (player.getObject(DataType.MARRY_DATA) as String).split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // The other person's marry data
        val otherPlayerMarryData = (PlayerHandler.getPlayer(otherPerson.longID).getObject(DataType.MARRY_DATA) as String).split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // Makes sure it's just the username and the guild id
        if (otherPlayerMarryData.size != 2) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }

        // If they're already married
        if (!(otherPlayerMarryData[0].equals("none", ignoreCase = true) && otherPlayerMarryData[1].equals("none", ignoreCase = true))) {
            messageHandler.sendError(otherPerson.name + " is already married!")
            return
        }

        // Makes sure it's just the username and the guild id
        if (playerMarryData.size != 2) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }

        // If they're already married
        if (!(playerMarryData[0].equals("none", ignoreCase = true) && playerMarryData[1].equals("none", ignoreCase = true))) {
            messageHandler.sendError("You're already married!")
            return
        }

        // Sends the invitation message
        val acceptMessage = RequestBuffer
                .request<IMessage> { e.channel.sendMessage("Will you marry `" + e.author.name + "`, `" + otherPerson.name + "`?") }.get()

        // Creates a request buffer and reacts with the Y and N emojis
        val rb = RequestBuilder(e.client)
        rb.shouldBufferRequests(true)
        rb.doAction {
            acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")) // Y
            true
        }.andThen {
            acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")) // N
            true
        }.execute() // Executes

        // Assigns variables
        var reacted = true
        var timeout = 0

        while (reacted) {
            // If it's been 10 seconds, exit
            if (timeout == 10) {
                messageHandler.sendError(otherPerson.name + " didn't respond in time!")
                return
            }

            // Sleeps a second so it doesn't go so fast
            delay(1000L)

            // Increases the timeout value
            timeout++

            // Gets both reactions
            val yReaction = RequestBuffer.request<IReaction> { acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE")) }.get()
            val nReaction = RequestBuffer.request<IReaction> { acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3")) }.get()

            // Checks if the user reacted to the Y
            if (yReaction.getUserReacted(otherPerson))
                reacted = false

            // Checks if the user reacted with the N
            if (nReaction.getUserReacted(otherPerson)) {
                messageHandler.sendError("User denied!")
                return
            }
        }

        // Sets the new marriage data
        player.setObject(DataType.MARRY_DATA, otherPerson.stringID + '&'.toString() + e.guild.stringID)
        PlayerHandler.getPlayer(otherPerson.longID).setObject(DataType.MARRY_DATA, e.author.stringID + '&'.toString() + e.guild.stringID)

        messageHandler.sendEmbed(EmbedBuilder().withDesc("Congratulations `" + otherPerson.name + "` and `" + e.author.name + "` are now married!"))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["marry [@user]"] = "Request to marry a user."
        Command.helpCommand(st, "Marry", e)
    }
}
