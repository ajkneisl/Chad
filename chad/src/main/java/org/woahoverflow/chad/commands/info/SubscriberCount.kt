package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.youtube.YouTubeHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.text.DecimalFormat
import java.util.*

/**
 * Gets the subscriber count for YouTuber(s)
 *
 * @author sho
 */
class SubscriberCount : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        if (args.isEmpty()) {
            val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String
            MessageHandler(e.channel, e.author).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "subcount [channel name]")
        }

        // Puts two YouTube channels in a VS format
        if (args.size == 3 && args[0].equals("vs", ignoreCase = true)) {
            // Both channels from arguments 1 & 2
            val channelOne = YouTubeHandler.getYoutubeChannel(args[1])
            val channelTwo = YouTubeHandler.getYoutubeChannel(args[2])

            // If either couldn't be found
            if (channelOne == null || channelTwo == null) {
                MessageHandler(e.channel, e.author).sendEmbed(EmbedBuilder().withDesc("Invalid YouTube Channel(s)!"))
                return
            }

            // Both channels subscriber count
            val channelOneSubscriberCount = channelOne.subscriberCount
            val channelTwoSubscriberCount = channelTwo.subscriberCount

            // Makes sure the difference isn't negative, by not putting smaller channel - the larger channel
            val difference = if (channelOneSubscriberCount - channelTwoSubscriberCount > 0)
                channelOneSubscriberCount - channelTwoSubscriberCount
            else
                channelTwoSubscriberCount - channelOneSubscriberCount

            // Puts the amount from 10000 to 10,000
            val formatter = DecimalFormat("#,###")

            // The difference string
            var formattedString = ""
            formattedString += if (channelOneSubscriberCount > channelTwoSubscriberCount)
                '`'.toString() + channelOne.username + "` has `" + formatter.format(difference) + "` more subscribers than `" + channelTwo.username + "`!"
            else
                '`'.toString() + channelTwo.username + "` has `" + formatter.format(difference) + "` more subscribers than `" + channelOne.userUrl + "`!"

            MessageHandler(e.channel, e.author).sendEmbed(EmbedBuilder().withDesc(
                    "**" + channelOne.username + "** : `" + formatter.format(channelOne.subscriberCount) + "`\n"
                            + "**" + channelTwo.username + "** : `" + formatter.format(channelTwo.subscriberCount) + "`\n\n" + formattedString
            ))

            return
        }

        // The selected channel
        val channel = YouTubeHandler.getYoutubeChannel(args[0])

        // If the channel doesn't exist
        if (channel == null) {
            MessageHandler(e.channel, e.author).sendEmbed(EmbedBuilder().withDesc("Invalid YouTube channel!"))
            return
        }

        // Formats the message & sends
        val formatter = DecimalFormat("#,###")
        val channelDetails = "**Name** : " + channel.username +
                "\n**Subscriber Count** : " + formatter.format(channel.subscriberCount) +
                "\n**View Count** : " + formatter.format(channel.viewCount) +
                "\n**Video Count** : " + formatter.format(channel.videoCount) +
                "\n**Channel Link** : " + channel.userUrl

        MessageHandler(e.channel, e.author).sendEmbed(EmbedBuilder().withDesc(channelDetails).withImage(channel.userIconUrl))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["subcount [youtuber name]"] = "Gets a profile of a YouTuber."
        st["subcount vs [youtuber name] [2nd youtuber name]"] = "Compares two YouTuber's subscriber counts."
        Command.helpCommand(st, "Subscriber Count", e)
    }
}
