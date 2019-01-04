package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

/**
 * Gets a variety of random things
 *
 * @author sho
 */
public class Random implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            String prefix = (String) GuildHandler.handle.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            // Makes sure there's arguments
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "random **number/quote/word**");
                return;
            }
            
            switch (args.get(0).toLowerCase()) {
                case "number":
                    SecureRandom rand = new SecureRandom();
                    
                    // If the args size is 2, custom number was inputted
                    if (args.size() == 2) {
                        // Try block is to catch if the argument wasn't a number
                        try {
                            int i = Integer.parseInt(args.get(1));

                            // Makes sure the input isn't 0
                            if (i == 0) {
                                messageHandler.sendError("Cannot use 0!");
                                return;
                            }

                            // Gets the random numbers and sends
                            messageHandler.sendEmbed(new EmbedBuilder().withDesc("Your random number is `"+rand.nextInt(i)+"`. (out of `"+i+"`)"));
                        } catch (NumberFormatException throwaway) {
                            messageHandler.sendError("Invalid Number");
                        }
                        return;
                    }

                    // Sends a random number within 100
                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("Your random number is `"+rand.nextInt(100)+"`."));
                    return;
                case "word":
                    String word = ChadVar.wordsList.get(new java.util.Random().nextInt(ChadVar.wordsList.size()));

                    // Makes the first letter of the word uppercase
                    String uppercaseWord = word.toUpperCase().charAt(0)+word.substring(1);

                    // Gets a random word and sends it
                    messageHandler.sendEmbed(new EmbedBuilder().withDesc(uppercaseWord));
                    return;
                default:
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "random **quote/word/number**");
            }
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("random number [max]", "Gives random number with an optional max value.");
        st.put("random word", "Gets a random word.");
        return Command.helpCommand(st, "Random", e);

    }
}
