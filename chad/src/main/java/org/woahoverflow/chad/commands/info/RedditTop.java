package org.woahoverflow.chad.commands.info;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.Reddit;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Gets a top post from a subreddit
 *
 * @author sho, codebasepw
 */
public class RedditTop implements Command.Class {
    @Override
    public final Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            IMessage message = Objects.requireNonNull(messageHandler.sendMessage("Loading..."));
            String prefix = (String) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            // If there's no arguments
            if (args.isEmpty()) {
                message.delete();
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "rtop [subreddit name]");
                return;
            }

            JSONObject post = Reddit.getPost(args.get(0), Reddit.PostType.NEW);

            if (post == null) {
                message.delete();
                messageHandler.sendError("Invalid Subreddit!");
                return;
            }

            post = post.getJSONObject("data");

            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.withUrl("https://reddit.com" + post.getString("permalink"));
            embedBuilder.withTitle(post.getString("title"));
            embedBuilder.withDesc(String.format("**Vote**: %s / **Comments**: %s", post.getLong("ups"), post.getLong("num_comments")));
            embedBuilder.withImage(post.getString("url"));

            message.delete();
            messageHandler.credit(post.getString("subreddit_name_prefixed")).sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("rtop [subreddit]", "Displays the hottest post from a subreddit.");
        return Command.helpCommand(st, "Reddit Top", e);
    }
}
