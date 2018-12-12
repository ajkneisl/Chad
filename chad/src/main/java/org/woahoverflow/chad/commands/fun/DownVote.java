package org.woahoverflow.chad.commands.fun;

import java.util.ArrayList;
import java.util.List;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.Player.DataType;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.7.0
 */
public class DownVote implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            if (e.getMessage().getMentions().isEmpty())
            {
                messageHandler.sendError(MessageHandler.NO_MENTIONS);
                return;
            }

            // Target
            IUser target = e.getMessage().getMentions().get(0);

            // The author's player instance
            Player author = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());

            ArrayList<Long> voteData = (ArrayList<Long>) author.getObject(DataType.VOTE_DATA);

            if (voteData.contains(target.getLongID()))
            {
                messageHandler.sendError("You've already voted on this user!");
                return;
            }

            // Add the voted user to the array
            voteData.add(target.getLongID());

            // ReSet it
            author.setObject(DataType.VOTE_DATA, voteData);

            // The target user's player instance
            Player targetPlayer = PlayerHandler.handle.getPlayer(target.getLongID());

            targetPlayer.setObject(
                DataType.PROFILE_DOWNVOTE,
                (((Long) targetPlayer.getObject(DataType.PROFILE_DOWNVOTE)) + 1L)
            );

            messageHandler.sendEmbed(new EmbedBuilder().withDesc(
                "You downvoted `" + target.getName() + "`!\n"
                    + "Their vote is now `" +
                    targetPlayer.getObject(DataType.PROFILE_DOWNVOTE) + targetPlayer.getObject(DataType.PROFILE_UPVOTE)
                    + "`!"
            ));
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }
}
