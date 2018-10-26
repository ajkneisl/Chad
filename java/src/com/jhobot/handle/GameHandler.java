package com.jhobot.handle;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess", "CanBeFinal"})
public class GameHandler
{
    private ReactionEmoji yes = ReactionEmoji.of("\uD83C\uDDFE");
    private ReactionEmoji no = ReactionEmoji.of("\uD83C\uDDF3");

    public GameHandler(IChannel channel)
    {
        IChannel ch = channel;
    }

    public IUser getOtherUser(MessageReceivedEvent e, List<String> args)
    {
        IUser u = null;
        if (e.getMessage().getMentions().isEmpty())
        {
            StringBuilder b = new StringBuilder();

            for (String s : args)
            {
                b.append(s).append(" ");
                if (!e.getGuild().getUsersByName(b.toString().trim()).isEmpty())
                    u = e.getGuild().getUsersByName(b.toString().trim()).get(0);
            }
        }
        else {
            u = e.getMessage().getMentions().get(0);
        }

        return u;
    }

    /*
    0 = accept
    1 = deny
    2 = timeout
    3 = error
     */
    public int waitForConfirmation(MessageReceivedEvent e, IUser user)
    {
        IMessage m = RequestBuffer.request(() -> e.getChannel().sendMessage("Do you accept `" + e.getAuthor().getName() + "`'s challenge, `" + user.getName() + "`?")).get();
        RequestBuilder rb = new RequestBuilder(e.getClient());
        rb.shouldBufferRequests(true);
        rb.doAction(() -> {
            m.addReaction(this.yes);
            return true;
        }).andThen(() -> {
            m.addReaction(this.no);
            return true;
        }).execute();
        int timeout = 0;
        while (true)
        {
            if (timeout == 10)
            {
                return 2;
            }
            try {
                TimeUnit.SECONDS.wait(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return 3;
            }
            timeout++;
            IReaction r1 = RequestBuffer.request(() -> e.getMessage().getReactionByEmoji(this.yes)).get();
            IReaction r2 = RequestBuffer.request(() -> e.getMessage().getReactionByEmoji(this.no)).get();

            if (r1.getUserReacted(user))
                return 0;
            if (r2.getUserReacted(user))
                return 1;
        }
    }
}
