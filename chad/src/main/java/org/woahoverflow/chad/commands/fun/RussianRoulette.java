package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RussianRoulette implements Command.Class {
    @Override
    public Runnable help(MessageReceivedEvent e)
    {
        HashMap<String, String> st = new HashMap<>();
        st.put("rrl <user/@user>", "Plays russian roulette with a selected user.");
        return Command.helpCommand(st, "Russian Roulette", e);
    }

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
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

            if (u == null)
            {
                m.sendError("Invalid User!");
                return;
            }

            if (u == e.getAuthor())
            {
                m.sendError("You can't play with yourself!");
                return;
            }
            if (u == e.getClient().getOurUser())
            {
                new MessageHandler(e.getChannel()).sendError("You can't play with Chad!");
                return;
            }

            final IUser u2 = u;


            IMessage m2 = RequestBuffer.request(() -> e.getChannel().sendMessage("Do you accept `" + e.getAuthor().getName() + "`'s challenge, `" + u2.getName() + "`?")).get();

            RequestBuilder rb = new RequestBuilder(e.getClient());
            rb.shouldBufferRequests(true);
            rb.doAction(() -> {
                m2.addReaction(ReactionEmoji.of("\uD83C\uDDFE")); // yes
                return true;
            }).andThen(() -> {
                m2.addReaction(ReactionEmoji.of("\uD83C\uDDF3")); // no
                return true;
            }).execute();

            boolean reacted = true;
            int timeout = 0;
            while (reacted)
            {
                if (timeout == 10)
                {
                    m.sendError("`"+u2.getName()+"` didn't respond in time!");
                    return;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                timeout++;

                IReaction y = RequestBuffer.request(() -> m2.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE"))).get();

                IReaction n = RequestBuffer.request(() -> m2.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3"))).get();

                if (y.getUserReacted(u2))
                    reacted = false;

                if (n.getUserReacted(u2))
                {
                    m.send("User Denied!", "Russian Roulette");
                    return;
                }
            }
            IUser win = null;
            IUser loser = null;

            int r1 = new java.util.Random().nextInt(100);
            int r2 = new java.util.Random().nextInt(100);

            if (r1 > r2) {
                win = e.getAuthor();
                loser = u2;
            }
            if (r2 > r1) {
                win = u2;
                loser = e.getAuthor();
            }

            if (win == null || loser == null)
            {
                new MessageHandler(e.getChannel()).sendError("");
                return;
            }

            MessageBuilder b = new MessageBuilder(e.getClient()).withChannel(e.getChannel()).withContent("`" +win.getName()+"` is the winner! \n`"+loser.getName()+"`\uD83D\uDD2B");
            RequestBuffer.request(b::build);
        };
    }
}
