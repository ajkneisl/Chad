package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.List;

@SuppressWarnings("unused")
public class TicTacToe implements Command.Class  {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        // TODO hello?
        return() -> {
            IMessage m = RequestBuffer.request(() -> e.getChannel().sendMessage("")).get();
            RequestBuilder rb = new RequestBuilder(e.getClient());
            rb.shouldBufferRequests(true);
            rb.doAction(() -> {
                m.addReaction(ReactionEmoji.of("\uD83C\uDDFE"));
                return true;
            }).andThen(() -> {
                m.addReaction(ReactionEmoji.of("\uD83C\uDDF3"));
                return true;
            }).execute();
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
