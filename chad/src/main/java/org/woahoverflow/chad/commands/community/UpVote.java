package org.woahoverflow.chad.commands.community;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * UpVote a player's profile
 *
 * @author sho
 */
@SuppressWarnings("unchecked")
public class UpVote implements Command.Class {
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            String prefix =(String) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            if (e.getMessage().getMentions().isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "upvote **@user**");
                return;
            }

            // Target
            IUser target = e.getMessage().getMentions().get(0);

            // Makes sure they're not upvoting themselves
            if (target.equals(e.getAuthor())) {
                messageHandler.sendError("You can't vote on that user!");
                return;
            }

            // The author's player instance
            Player author = PlayerHandler.getPlayer(e.getAuthor().getLongID());

            ArrayList<Long> voteData = (ArrayList<Long>) author.getObject(DataType.VOTE_DATA);

            if (voteData.contains(target.getLongID())) {
                messageHandler.sendError("You've already voted on this user!");
                return;
            }

            // Add the voted user to the array
            voteData.add(target.getLongID());

            // ReSet it
            author.setObject(DataType.VOTE_DATA, voteData);

            // The target user's player instance
            Player targetPlayer = PlayerHandler.getPlayer(target.getLongID());

            // The target user's upvote amount
            long targetPlayerUpvote = (long) targetPlayer.getObject(DataType.PROFILE_UPVOTE);

            // The target user's downvote amount
            long targetPlayerDownvote = (long) targetPlayer.getObject(DataType.PROFILE_DOWNVOTE);

            // Update values
            targetPlayer.setObject(
                DataType.PROFILE_UPVOTE, targetPlayerUpvote+1L
                );

            messageHandler.sendEmbed(new EmbedBuilder().withDesc(
                "You upvoted `" + target.getName() + "`!\n"
                    + "Their vote is now `"+((targetPlayerUpvote+1L)-targetPlayerDownvote)+ "`!"
            ));
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap st = new HashMap<String, String>();
        st.put("upvote <@user>", "Upvotes a user's Chad profile.");
        return Command.helpCommand(st, "UpVote", e);
    }
}
