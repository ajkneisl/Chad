package org.woahoverflow.chad.commands.developer

import kotlinx.coroutines.delay
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.ActivityType
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.StatusType
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import sx.blah.discord.util.RequestBuilder

import java.util.HashMap
import java.util.concurrent.TimeUnit

/**
 * Shuts down the bot via discord
 *
 * @author sho, codebasepw
 */
class Shutdown : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        // Requests to send the confirmation message then gets it
        val confirm = RequestBuffer.request<IMessage> { e.channel.sendMessage("Are you sure you want to do this?") }.get()

        // The emojis used in the message
        val yes = ReactionEmoji.of("\uD83C\uDDFE")
        val no = ReactionEmoji.of("\uD83C\uDDF3")

        // Adds both reactions
        val builder = RequestBuilder(e.client)
        builder.shouldBufferRequests(true)
        builder.doAction {
            confirm.addReaction(yes)
            true
        }.andThen {
            confirm.addReaction(no)
            true
        }.execute()

        var userReacted = false
        var timeout = 0
        while (!userReacted) {
            delay(1000L)

            // If the user's taken more than 10 seconds
            if (timeout >= 10) {
                messageHandler.sendError("You didn't react fast enough!")
                return
            }

            // Add another second
            timeout++

            // If they've accepted
            if (confirm.getReactionByEmoji(yes).getUserReacted(e.author))
                userReacted = true

            // If they've denied
            if (confirm.getReactionByEmoji(no).getUserReacted(e.author)) {
                messageHandler.sendError("Cancelled shutdown!")
                return
            }
        }

        // Deletes the confirmation message
        RequestBuffer.request { confirm.delete() }

        // Warns that the bot is shutting down
        MessageHandler(e.channel, e.author).sendEmbed(EmbedBuilder().withDesc("Chad is shutting down in 10 seconds..."))

        // Warns within the UI
        ChadInstance.getLogger().warn("Shutting down in 10 seconds...")

        // Updates the presence
        ChadInstance.cli.changePresence(StatusType.DND, ActivityType.PLAYING, "Shutting down...")

        delay(10000L)

        // Exits
        ChadInstance.cli.logout()
        System.exit(0)
    }

    override suspend fun help(e: MessageEvent) {
        val hash = HashMap<String, String>()
        hash["shutdown"] = "Shuts the bot down."
        Command.helpCommand(hash, "Shutdown", e)
    }
}
