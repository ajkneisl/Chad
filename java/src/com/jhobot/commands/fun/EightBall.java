package com.jhobot.commands.fun;

import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class EightBall implements Command {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        if (args.size() == 0)
        {
            new Messages(e.getChannel()).sendError("You didn't ask a question!");
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
        new Messages(e.getChannel()).send(answers[index], "8Ball");
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : 8ball");
        b.appendField(db.getString(e.getGuild(), "prefix") + "8ball [question]", "Answers your best questions.", false);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        new Messages(e.getChannel()).sendEmbed(b.build());
    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e, DB db) {
        return e.getClient().getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.SEND_MESSAGES);
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e, DB db) {
        return true;
    }
}
