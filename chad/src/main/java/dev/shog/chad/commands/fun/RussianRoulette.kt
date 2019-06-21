package dev.shog.chad.commands.`fun`

import kotlinx.coroutines.delay
import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.coroutine.asIMessage
import dev.shog.chad.framework.handle.coroutine.asIReaction
import dev.shog.chad.framework.handle.coroutine.request
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuilder
import java.security.SecureRandom
import java.util.*
import kotlin.random.Random

/**
 * Play russian roulette with another user
 *
 * @author sho, codebasepw
 */
class RussianRoulette : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["rrl [user/@user]"] = "Plays russian roulette with a selected user."
        Command.helpCommand(st, "Russian Roulette", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        // Assigns the user to the mentioned user
        val unFinalUser: IUser = if (e.message.mentions.isNotEmpty())
            e.message.mentions[0]
        else {
            messageHandler.sendPresetError(MessageHandler.Messages.NO_MENTIONS, "rrl [@user]", includePrefix = true)
            return
        }

        // If the user equals themselves or chad, deny
        if (unFinalUser == e.author || unFinalUser == e.client.ourUser) {
            messageHandler.sendError("You can't play with that person!")
            return
        }

        // Sends the invitation message
        val acceptMessage = request {
            e.channel.sendMessage("Do you accept `" + e.author.name + "`'s challenge, `" + unFinalUser.name + "`?")
        }.asIMessage()

        // Creates a request buffer and reacts with the Y and N emojis
        RequestBuilder(e.client)
                .apply { shouldBufferRequests(true) }
                .doAction {
                    acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")) // Y
                    true
                }.andThen {
                    acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")) // N
                    true
                }.execute()

        // Assigns variables
        var reacted = true
        var timeout = 0

        while (reacted) {
            // If it's been 10 seconds, exit
            if (timeout == 10) {
                messageHandler.sendError("`${unFinalUser.name}` didn't respond in time!")
                return
            }

            delay(1000L)
            timeout++

            // Gets both reactions
            val yReaction = request { acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE")) }.asIReaction()
            val nReaction = request { acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3")) }.asIReaction()

            if (yReaction.getUserReacted(unFinalUser)) reacted = false
            if (nReaction.getUserReacted(unFinalUser)) {
                messageHandler.sendError("User Denied!")
                return
            }
        }

        // Calculates the winner with two random numbers
        var win: IUser? = null
        var loser: IUser? = null
        val r1 = Random.nextInt(100)
        val r2 = Random.nextInt(100)

        when {
            r1 > r2 -> {
                win = e.author
                loser = unFinalUser
            }

            r2 > r1 -> {
                win = unFinalUser
                loser = e.author
            }

            else -> {
                messageHandler.sendMessage("The revolver had no bullets... Tie!")
            }
        }

        // Makes sure the users aren't null
        if (win == null || loser == null) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }

        // Sends the message
        messageHandler.sendMessage("`${win.name}` is the winner! \n`" + loser.name + "`\uD83D\uDD2B")
    }
}
