package org.woahoverflow.chad.commands.fight;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;


public class Attack implements Command.Class
{
    public MessageHandler messageHandler;

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            messageHandler = new MessageHandler(e.getChannel());

            if (args.size() < 1)
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            // define users
            IUser opponent = e.getMessage().getMentions().get(0);
            IUser author = e.getAuthor();

            // nullpointer sanity check
            if (opponent == null)
            {
                messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                return;
            }

            // grab player datasets
            Player plyOpponent = PlayerManager.handle.getPlayer(opponent.getLongID());
            Player plyAuthor = PlayerManager.handle.getPlayer(author.getLongID());

            // generate damage value
            int damage = new Random().nextInt(3);

            // apply damage
            plyAuthor.setObject(Player.DataType.SWORD_HEALTH, (int)plyAuthor.getObject(Player.DataType.SWORD_HEALTH) - damage);
            if ((int)plyOpponent.getObject(Player.DataType.SHIELD_HEALTH) >= damage)
            {
                plyOpponent.setObject(Player.DataType.SHIELD_HEALTH, (int)plyOpponent.getObject(Player.DataType.SHIELD_HEALTH) - damage);
                plyOpponent.setObject(Player.DataType.HEALTH, (int)plyOpponent.getObject(Player.DataType.HEALTH) - 1);

                messageHandler.sendMessage(String.format("You did %s damage to %s!", 1, opponent.mention()));
            } else
            {
                plyOpponent.setObject(Player.DataType.HEALTH, (int)plyOpponent.getObject(Player.DataType.HEALTH) - damage);

                messageHandler.sendMessage(String.format("You did %s damage to %s!", damage, opponent.mention()));
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }

    public IMessage askToPlay(MessageReceivedEvent e, IUser authorUser, IUser opponentUser)
    {
        // only used once, but thanks lambda
        final IUser user = opponentUser;

        // Sends the invitation message
        IMessage acceptMessage = RequestBuffer
                .request(() -> e.getChannel().sendMessage("Do you accept `" + authorUser.getName() + "`'s challenge, `" + user.getName() + "`?")).get();

        // Creates a request buffer and reacts with the Y and N emotes
        RequestBuilder rb = new RequestBuilder(e.getClient());
        rb.shouldBufferRequests(true);
        rb.doAction(() -> {
            acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")); // Y
            return true;
        }).andThen(() -> {
            acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")); // N
            return true;
        }).execute(); // Executes

        return acceptMessage;
    }

    public IMessage changeTurn(MessageReceivedEvent e, IUser activeUser)
    {
        IMessage fightMessage = RequestBuffer
                .request(() -> e.getChannel().sendMessage("Active player: `" + activeUser.getName() + "`\nWhat do you want do to?\n**A** for `attack`.\n**D** for `defend`.")).get();

        RequestBuilder rb = new RequestBuilder(e.getClient());
        rb.shouldBufferRequests(true);
        rb.doAction(() -> {
            fightMessage.addReaction(ReactionEmoji.of("\uD83C\uDDE6")); // A
            return true;
        }).andThen(() -> {
            fightMessage.addReaction(ReactionEmoji.of("\uD83C\uDDE9")); // D
            return true;
        }).execute(); // Executes

        return fightMessage;
    }

    public void doEdit(MessageReceivedEvent e, IMessage message, String content)
    {
        message.edit(content);
    }
}