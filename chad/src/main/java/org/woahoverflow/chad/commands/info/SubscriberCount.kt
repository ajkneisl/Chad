package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.youtube.YouTubeHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*

/**
 * Gets the subscriber count for YouTuber(s)
 *
 * @author sho
 */
class SubscriberCount : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "subcount [channel name]", includePrefix = true)
            return
        }

        // Puts two YouTube channels in a VS format
        if (args.size == 3 && args[0].equals("vs", ignoreCase = true)) {
            // Both channels from arguments 1 & 2
            val channels = try {
                Pair(YouTubeHandler.getYoutubeChannel(args[1]), YouTubeHandler.getYoutubeChannel(args[2])).also {
                    if (it.first == null || it.second == null) {
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid YouTube Channel(s)!"))
                        return
                    }
                }
            } catch (ex: Exception) {
                messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid YouTube Channel(s)!"))
                return
            }

            if (channels.first!!.userId == channels.second!!.userId) {
                messageHandler.sendEmbed(EmbedBuilder().withDesc("You cannot compare the same channel!"))
                return
            }

            // Both channels subscriber count
            val channelOneSubscriberCount = channels.first!!.subscriberCount
            val channelTwoSubscriberCount = channels.second!!.subscriberCount

            // Makes sure the difference isn't negative, by not putting smaller channel - the larger channel
            val difference = if (channelOneSubscriberCount - channelTwoSubscriberCount > 0)
                channelOneSubscriberCount - channelTwoSubscriberCount
            else
                channelTwoSubscriberCount - channelOneSubscriberCount

            // Puts the amount from 10000 to 10,000
            val formatter = DecimalFormat("#,###")

            // The difference string
            val formattedString = if (channelOneSubscriberCount > channelTwoSubscriberCount)
                "`${channels.first!!.username}` has `${formatter.format(difference)}` more subscribers than `${channels.second!!.username}`!"
            else
                "${channels.second!!.username}` has `${formatter.format(difference)}` more subscribers than `${channels.first!!.username}`!"

            messageHandler.sendEmbed { withDesc(
                    "**${channels.first!!.username}** : `${formatter.format(channels.first!!.subscriberCount)}`\n"
                            + "**${channels.second!!.username}** : `${formatter.format(channels.second!!.subscriberCount)}`\n\n" + formattedString
            ) }

            return
        }

        // The selected channel
        val channel = YouTubeHandler.getYoutubeChannel(args[0]) ?: run {
            MessageHandler(e.channel, e.author).sendEmbed(EmbedBuilder().withDesc("Invalid YouTube channel!"))
            return
        }

        // Formats the message & sends
        val formatter = DecimalFormat("#,###")
        val channelDetails = "**Name** : `${channel.username}`" +
                "\n**Subscriber Count** : `${formatter.format(channel.subscriberCount)}`" +
                "\n**View Count** : `${formatter.format(channel.viewCount)}`" +
                "\n**Video Count** : `${formatter.format(channel.videoCount)}`" +
                "\n**Channel Link** : ${channel.userUrl}"

        messageHandler.sendEmbed { withDesc(channelDetails).withImage(channel.userIconUrl) }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["subcount [youtuber name]"] = "Gets the profile of a YouTuber."
        st["subcount vs [youtuber name] [2nd youtuber name]"] = "Compares two YouTuber's subscriber counts."
        Command.helpCommand(st, "Subscriber Count", e)
    }
}
