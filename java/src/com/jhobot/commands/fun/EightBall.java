package com.jhobot.commands.fun;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class EightBall implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Messages m = new Messages(e.getChannel());
            if (args.size() == 0)
            {
                m.sendError("You didn't ask a question!");
                return;
            }
            String[] answers = {"It is certain", "It is decidedly so", "Without a doubt",
                    "Yes - definitely", "You may rely on it", "As I see it, yes",
                    "Most likely", "Outlook good", "Signs point to yes",
                    "Yes", "Reply hazy, try again", "Ask again later",
                    "Better not tell you now", "Cannot predict now", "Concentrate and ask again",
                    "Don't count on it", "My reply is no", "My sources say no",
                    "Outlook not so good", "Very doubtful"};

            Random random = new Random();
            int index = random.nextInt(answers.length);
            m.send(answers[index], "8Ball");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : 8ball");
            b.appendField(JhoBot.db .getString(e.getGuild(), "prefix") + "8ball [question]", "Answers your best questions.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
