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
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Marry a user
 *
 * @see DivorcePlayer
 * @author sho
 */
public class MarryPlayer implements Command.Class{
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            String prefix = (String) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            // If they didn't mention anyone
            if (e.getMessage().getMentions().isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "marry **@user**");
                return;
            }

            // The other person
            IUser otherPerson = e.getMessage().getMentions().get(0);

            // Make sure they're not marrying Chad or themselves
            if (otherPerson.equals(e.getAuthor()) || otherPerson.equals(e.getClient().getOurUser())) {
                messageHandler.sendError("You can't marry that person!");
                return;
            }

            // The author's player instance
            Player player = PlayerHandler.getPlayer(e.getAuthor().getLongID());

            // Player's marry data, in format `player_id&guild_id`
            String[] playerMarryData = ((String) player.getObject(DataType.MARRY_DATA)).split("&");

            // The other person's marry data
            String[] otherPlayerMarryData = ((String) PlayerHandler.getPlayer(otherPerson.getLongID()).getObject(DataType.MARRY_DATA)).split("&");

            // Makes sure it's just the username and the guild id
            if (otherPlayerMarryData.length != 2) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION);
                return;
            }

            // If they're already married
            if (!(otherPlayerMarryData[0].equalsIgnoreCase("none") && otherPlayerMarryData[1].equalsIgnoreCase("none"))) {
                messageHandler.sendError(otherPerson.getName() + " is already married!");
                return;
            }

            // Makes sure it's just the username and the guild id
            if (playerMarryData.length != 2) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION);
                return;
            }

            // If they're already married
            if (!(playerMarryData[0].equalsIgnoreCase("none") && playerMarryData[1].equalsIgnoreCase("none"))) {
                messageHandler.sendError("You're already married!");
                return;
            }

            // Sends the invitation message
            IMessage acceptMessage = RequestBuffer
                .request(() -> e.getChannel().sendMessage("Will you marry `"+e.getAuthor().getName()+"`, `" + otherPerson.getName() + "`?")).get();

            // Creates a request buffer and reacts with the Y and N emojis
            RequestBuilder rb = new RequestBuilder(e.getClient());
            rb.shouldBufferRequests(true);
            rb.doAction(() -> {
                acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")); // Y
                return true;
            }).andThen(() -> {
                acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")); // N
                return true;
            }).execute(); // Executes

            // Assigns variables
            boolean reacted = true;
            int timeout = 0;

            while (reacted) {
                // If it's been 10 seconds, exit
                if (timeout == 10) {
                    messageHandler.sendError(otherPerson.getName()+" didn't respond in time!");
                    return;
                }

                // Sleeps a second so it doesn't go so fast
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                // Increases the timeout value
                timeout++;

                // Gets both reactions
                IReaction yReaction = RequestBuffer.request(() -> acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE"))).get();
                IReaction nReaction = RequestBuffer.request(() -> acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3"))).get();

                // Checks if the user reacted to the Y
                if (yReaction.getUserReacted(otherPerson))
                    reacted = false;

                // Checks if the user reacted with the N
                if (nReaction.getUserReacted(otherPerson)) {
                    messageHandler.sendError("User denied!");
                    return;
                }
            }

            // Sets the new marriage data
            player.setObject(DataType.MARRY_DATA, otherPerson.getStringID()+ '&' +e.getGuild().getStringID());
            PlayerHandler.getPlayer(otherPerson.getLongID()).setObject(DataType.MARRY_DATA, e.getAuthor().getStringID()+ '&' +e.getGuild().getStringID());

            messageHandler.sendEmbed(new EmbedBuilder().withDesc("Congratulations `"+otherPerson.getName()+"` and `"+e.getAuthor().getName()+"` are now married!"));
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("marry <@user>", "Request to marry a user.");
        return Command.helpCommand(st, "Marry", e);
    }
}
