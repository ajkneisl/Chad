package com.jhobot.commands.fun;

import com.jhobot.handle.commands.Category;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.PermissionLevels;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.List;

public class TicTacToe implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
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

    @Override
    public PermissionLevels level() {
        return null;
    }

    @Override
    public Category category() {
        return Category.FUN;
    }
}
