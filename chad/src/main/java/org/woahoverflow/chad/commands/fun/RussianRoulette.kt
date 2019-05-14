package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IReaction
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer
import sx.blah.discord.util.RequestBuilder

import java.security.SecureRandom
import java.util.HashMap
import java.util.concurrent.TimeUnit

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
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Assigns the user to the mentioned user
        val unFinalUser: IUser
        if (!e.message.mentions.isEmpty())
            unFinalUser = e.message.mentions[0]
        else {
            messageHandler.sendPresetError(MessageHandler.Messages.NO_MENTIONS, prefix + "rrl [@user]")
            return
        }

        // If the user equals themselves or chad, deny
        if (unFinalUser == e.author || unFinalUser == e.client.ourUser) {
            messageHandler.sendError("You can't play with that person!")
            return
        }

        // Makes the user final

        // Sends the invitation message
        val acceptMessage = RequestBuffer.request<IMessage> { e.channel.sendMessage("Do you accept `" + e.author.name + "`'s challenge, `" + unFinalUser.name + "`?") }.get()

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
                messageHandler.sendError('`'.toString() + unFinalUser.name + "` didn't respond in time!")
                return
            }

            // Sleeps a second so it doesn't go so fast
            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }

            // Increases the timeout value
            timeout++

            // Gets both reactions
            val yReaction = RequestBuffer.request<IReaction> { acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE")) }.get()
            val nReaction = RequestBuffer.request<IReaction> { acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3")) }.get()

            // Checks if the user reacted to the Y
            if (yReaction.getUserReacted(unFinalUser))
                reacted = false

            // Checks if the user reacted with the N
            if (nReaction.getUserReacted(unFinalUser)) {
                messageHandler.sendError("User Denied!")
                return
            }
        }

        // Calculates the winner with two random numbers
        var win: IUser? = null
        var loser: IUser? = null
        val r1 = SecureRandom().nextInt(100)
        val r2 = SecureRandom().nextInt(100)
        if (r1 > r2) {
            win = e.author
            loser = unFinalUser
        }
        if (r2 > r1) {
            win = unFinalUser
            loser = e.author
        }

        // Makes sure the users aren't null
        if (win == null || loser == null) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }

        // Sends the message
        messageHandler.sendMessage('`'.toString() + win!!.name + "` is the winner! \n`" + loser!!.name + "`\uD83D\uDD2B")
    }
}
