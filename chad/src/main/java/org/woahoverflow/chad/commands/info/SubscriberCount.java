package org.woahoverflow.chad.commands.info;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.youtube.YouTubeChannel;
import org.woahoverflow.chad.framework.handle.youtube.YouTubeHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
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
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            if (args.isEmpty()) {
                String prefix = ((String) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX));
                new MessageHandler(e.getChannel(), e.getAuthor()).sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "subcount <channel name>");
            }

            // Puts two YouTube channels in a VS format
            if (args.size() == 3 && args.get(0).equalsIgnoreCase("vs")) {
                // Both channels from arguments 1 & 2
                YouTubeChannel channelOne = YouTubeHandler.getYoutubeChannel(args.get(1));
                YouTubeChannel channelTwo = YouTubeHandler.getYoutubeChannel(args.get(2));

                // If either couldn't be found
                if (channelOne == null || channelTwo == null) {
                    new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc("Invalid YouTube Channel(s)!"));
                    return;
                }

                // Both channels subscriber count
                long channelOneSubscriberCount = channelOne.getSubscriberCount();
                long channelTwoSubscriberCount = channelTwo.getSubscriberCount();

                // Makes sure the difference isn't negative, by not putting smaller channel - the larger channel
                long difference = channelOneSubscriberCount - channelTwoSubscriberCount > 0 ?
                    channelOneSubscriberCount - channelTwoSubscriberCount
                    : channelTwoSubscriberCount - channelOneSubscriberCount;

                // Puts the amount from 10000 to 10,000
                DecimalFormat formatter = new DecimalFormat("#,###");

                // The difference string
                String formattedString = "";
                formattedString += channelOneSubscriberCount > channelTwoSubscriberCount ? '`' + channelOne.getUsername() + "` has `"+formatter.format(difference)+"` more subscribers than `" + channelTwo.getUsername() + "`!" :
                    '`' + channelTwo.getUsername() + "` has `"+formatter.format(difference)+"` more subscribers than `" + channelOne.getUserUrl() + "`!";

                new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc(
                    "**" + channelOne.getUsername() + "** : `" + formatter.format(channelOne.getSubscriberCount()) + "`\n"
                    +"**" + channelTwo.getUsername() + "** : `" + formatter.format(channelTwo.getSubscriberCount()) + "`\n\n" + formattedString
                ));

                return;
            }

            // The selected channel
            YouTubeChannel channel = YouTubeHandler.getYoutubeChannel(args.get(0));

            // If the channel doesn't exist
            if (channel == null) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc("Invalid YouTube channel!"));
                return;
            }

            // Formats the message & sends
            DecimalFormat formatter = new DecimalFormat("#,###");
            String channelDetails =
                    "**Name** : " + channel.getUsername() +
                    "\n**Subscriber Count** : " + formatter.format(channel.getSubscriberCount()) +
                    "\n**View Count** : " + formatter.format(channel.getViewCount()) +
                    "\n**Video Count** : " + formatter.format(channel.getVideoCount()) +
                    "\n**Channel Link** : " + channel.getUserUrl();

            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(new EmbedBuilder().withDesc(channelDetails).withImage(channel.getUserIconUrl()));
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("subcount <youtuber name>", "Gets a profile of a YouTuber.");
        st.put("subcount vs <youtuber name> <2nd youtuber name>", "Compares two YouTuber's subscriber counts.");
        return Command.helpCommand(st, "Subscriber Count", e);
    }
}
