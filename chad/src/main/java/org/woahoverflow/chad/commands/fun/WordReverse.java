package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Reverses a word
 *
 * @author sho
 */
public class WordReverse implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            // Makes sure the arguments aren't empty
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, GuildHandler.handle.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX)+ "upvote **@user**");
                return;
            }

            // Gets the word from all the arguments
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : args)
                stringBuilder.append(s).append(' ');

            // Gets the word & sends
            String word = stringBuilder.toString().trim();
            messageHandler.sendEmbed(new EmbedBuilder().withDesc("Word: `" + word + "`\n`" + stringBuilder.reverse().toString().trim() + '`'));
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("wr <word>", "Reverses a word.");
        return Command.helpCommand(hash, "Word Reverse", e);
    }
}
