package org.woahoverflow.chad.commands.info;

import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.YouTubeHandler;
import org.woahoverflow.chad.framework.handle.YouTubeHandler.Channel;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Gets the subscriber count for YouTuber(s)
 *
 * @author sho
 */
public class SubscriberCount implements Command.Class {
    @Override
    public Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            // Puts two YouTube channels in a VS format
            if (args.size() == 3 && args.get(0).equalsIgnoreCase("vs")) {
                // Both channels from arguments 1 & 2
                Channel channelOne = YouTubeHandler.getYoutubeChannel(args.get(1));
                Channel channelTwo = YouTubeHandler.getYoutubeChannel(args.get(2));

                // If either couldn't be found
                if (channelOne == null || channelTwo == null) {
                    new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc("Invalid YouTube Channel(s)!"));
                    return;
                }

                // Both channels subscriber count
                long channelOneSubscriberCount = channelOne.subscriberCount;
                long channelTwoSubscriberCount = channelTwo.subscriberCount;

                // Makes sure the difference isn't negative, by not putting smaller channel - the larger channel
                long difference = channelOneSubscriberCount - channelTwoSubscriberCount > 0 ?
                    channelOneSubscriberCount - channelTwoSubscriberCount
                    : channelTwoSubscriberCount - channelOneSubscriberCount;

                // Puts the amount from 10000 to 10,000
                DecimalFormat formatter = new DecimalFormat("#,###");

                // The difference string
                String formattedString = "";
                formattedString += channelOneSubscriberCount > channelTwoSubscriberCount ? '`' + channelOne.username + "` has `"+formatter.format(difference)+"` more subscribers than `" + channelTwo.username + "`!" :
                    '`' + channelTwo.username + "` has `"+formatter.format(difference)+"` more subscribers than `" + channelOne.username + "`!";

                new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc(
                    "**" + channelOne.username + "** : `" + formatter.format(channelOne.subscriberCount) + "`\n"
                    +"**" + channelTwo.username + "** : `" + formatter.format(channelTwo.subscriberCount) + "`\n\n" + formattedString
                ));

                return;
            }

            // The selected channel
            Channel channel = YouTubeHandler.getYoutubeChannel(args.get(0));

            // If the channel doesn't exist
            if (channel == null) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc("Invalid YouTube channel!"));
                return;
            }

            // Formats the message & sends
            DecimalFormat formatter = new DecimalFormat("#,###");
            String channelDetails =
                    "**Name** : " + channel.username +
                    "\n**Subscriber Count** : " + formatter.format(channel.subscriberCount) +
                    "\n**View Count** : " + formatter.format(channel.viewCount) +
                    "\n**Video Count** : " + formatter.format(channel.videoCount) +
                    "\n**Channel Link** : " + channel.userUrl;

            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc(channelDetails).withImage(channel.userIconUrl));
        };
    }

    @Override
    public Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("subcount <youtuber name>", "Gets a profile of a YouTuber.");
        st.put("subcount vs <youtuber name> <2nd youtuber name>", "Compares two YouTuber's subscriber counts.");
        return Command.helpCommand(st, "Subscriber Count", e);
    }
}
