package com.jhobot.commands.fun;

import com.jhobot.JhoBot;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RussianRoulette implements Command
{
    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args)
    {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Russian Roulette");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "rrl @user", "Plays russian roulette with a selected user.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Messages m = new Messages(e.getChannel());
            IUser u = null;
            if (e.getMessage().getMentions().isEmpty())
            {
                StringBuilder b = new StringBuilder();

                for (String s : args)
                {
                    b.append(s + " ");
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

            final IUser u2 = u;


            IMessage m2 = RequestBuffer.request(() -> {
                return e.getChannel().sendMessage("Do you accept `" + e.getAuthor().getName() + "`'s challenge, `" + u2.getName() + "`?");
            }).get();

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

                IReaction y = RequestBuffer.request(() -> {
                    return m2.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE"));
                }).get();

                IReaction n = RequestBuffer.request(() -> {
                    return m2.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3"));
                }).get();

                if (y.getUserReacted(u2))
                    reacted = false;

                if (n.getUserReacted(u2))
                {
                    m.send("User Denied!", "Russian Roulette");
                    return;
                }
            }
            int r = new java.util.Random().nextInt(100);
            IUser win = null;
            IUser loser = null;
            if (r >= 50) {
                win = e.getAuthor();
                loser = u2;
            }
            if (r < 50) {
                win = u2;
                loser = e.getAuthor();
            }

            MessageBuilder b = new MessageBuilder(e.getClient()).withChannel(e.getChannel()).withContent("`" +win.getName()+"` is the winner! \n`"+loser.getName()+"`\uD83D\uDD2B");
            RequestBuffer.request(b::build);
        };
    }
}
