package com.jhobot.commands.fun;

import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.CommandClass;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RussianRoulette implements CommandClass {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        if (args.size() == 0)
        {
            helpCommand(e, db);
            return;
        }
        IUser user;

        if (e.getMessage().getMentions().isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            for (String s : args)
            {
                sb.append(s + " ");
            }

            if (e.getGuild().getUsersByName(sb.toString().trim()).isEmpty())
            {
                new Messages(e.getChannel()).sendError("Invalid User!");
                return;
            }

            user = e.getGuild().getUsersByName(sb.toString().trim()).get(0);
        } else
        {
            user = e.getMessage().getMentions().get(0);
        }

        if (user == e.getAuthor())
        {
            new Messages(e.getChannel()).sendError("You can't play with yourself!");
            return;
        }

        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Russian Roulette");
        b.withDesc("Do you accept " + e.getAuthor().getName() + "'s offer, " + user.mention() + "?");
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
        IMessage msg2 = new MessageBuilder(e.getClient()).withChannel(e.getChannel()).withEmbed(b.build()).build();
        RequestBuffer.request(()-> msg2.addReaction(ReactionEmoji.of("\uD83C\uDDF3")));
        RequestBuffer.request(()-> msg2.addReaction(ReactionEmoji.of("\uD83C\uDDFE")));

        try {
            TimeUnit.SECONDS.sleep(3); // waits so it can add reactions without being timed out
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        int reacted = 0;
        int over = 0;

        while (reacted == 0)
        {
            over++;
            if (over == 60)
            {
                new Messages(e.getChannel()).send("User didn't respond in time!", "Russian Roulette");
                return;
            }
            for (IUser u : msg2.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE")).getUsers())
            {
                if (u == user) reacted = 2;
            }

            for (IUser u : msg2.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3")).getUsers())
            {
                if (u == user) reacted = 1;
            }
        }

        if (reacted == 1)
        {
            msg2.delete();
            new Messages(e.getChannel()).send("User denied!", "Russian Roulette");
            return;
        }

        int winner = new java.util.Random().nextInt(1);

        IUser winneruser;

        Messages m = new Messages(e.getChannel());

        RequestBuffer.request(() -> {
            try {
                IMessage m2 = e.getChannel().sendMessage("5");
                TimeUnit.SECONDS.sleep(1);
                m2.edit("4");
                TimeUnit.SECONDS.sleep(1);
                m2.edit("3");
                TimeUnit.SECONDS.sleep(1);
                m2.edit("2");
                TimeUnit.SECONDS.sleep(1);
                m2.edit("1");
                m2.delete();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
        try {
            TimeUnit.SECONDS.sleep(5); // catches up with requestbuffer
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (winner == 0)
        {
            winneruser = e.getAuthor();
        }
        else {
            winneruser = user;
        }

        msg2.delete();
        m.send("The winner is " + winneruser.getName() + "!", "Russian Roulette");
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : Russian Roulette");
        b.appendField(db.getString(e.getGuild(), "prefix") + "rrl [@user/user's name]", "Gives random number.", false);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
        new Messages(e.getChannel()).sendEmbed(b.build());
    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e, DB db) {
        return e.getChannel().getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.SEND_MESSAGES);
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e, DB db) {
        return true;
    }
}
