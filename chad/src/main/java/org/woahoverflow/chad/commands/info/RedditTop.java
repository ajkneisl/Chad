package org.woahoverflow.chad.commands.info;

import org.json.JSONException;
import org.json.JSONObject;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.ui.ChadError;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Gets a top post from a subreddit
 *
 * @author sho, codebasepw
 */
public class RedditTop implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            String prefix = (String) GuildHandler.handle.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            // If there's no arguments
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "rtop **subreddit name**");
                return;
            }

            // Gets a hot post from the selected sub-reddit
            JSONObject post;
            try {
                // Gets post
                JSONObject redditJson = JsonHandler.handle
                    .read("https://reddit.com/r/" + args.get(0) + "/hot.json");

                if (redditJson == null) {
                    messageHandler.sendError("Invalid Subreddit");
                    return;
                }

                if (redditJson
                    .getJSONObject("data")
                    .getJSONArray("children").isEmpty()) {
                    messageHandler.sendError("Invalid Subreddit");
                    return;
                }

                int index = 0;
                post = redditJson.getJSONObject("data")
                    .getJSONArray("children")
                    .getJSONObject(index)
                    .getJSONObject("data");


                // Makes sure the post isn't stickied
                while (post.getBoolean("stickied")) {
                    index++;
                    post = redditJson
                        .getJSONObject("data")
                        .getJSONArray("children")
                        .getJSONObject(index)
                        .getJSONObject("data");
                }
            } catch (JSONException e1) {
                ChadError.throwError("Error with RedditTop in guild " + e.getGuild().getStringID(), e1);
                return;
            } catch (RuntimeException e1) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendError("Invalid subreddit.");
                return;
            }

            // If the post is over 18 and the channel isn't Nsfw, deny.
            if (post.getBoolean("over_18") && !e.getChannel().isNSFW()) {
                messageHandler.sendPresetError(MessageHandler.Messages.CHANNEL_NOT_NSFW);
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle(post.getString("title"));
            embedBuilder.withDesc(post.getString("author"));
            embedBuilder.appendField("Score", post.getInt("score") + " (" + post.getInt("ups") + '/'
                + post.getInt("downs") + ')', true);
            embedBuilder.appendField("Comments", Integer.toString(post.getInt("num_comments")), true);
            embedBuilder.withImage(post.getString("url"));
            embedBuilder.withUrl("https://reddit.com" + post.getString("permalink"));
            messageHandler.sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("rtop <subreddit>", "Displays the hottest post from a subreddit.");
        return Command.helpCommand(st, "Reddit Top", e);
    }
}
