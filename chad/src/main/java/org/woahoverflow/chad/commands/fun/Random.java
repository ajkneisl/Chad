package org.woahoverflow.chad.commands.fun;

import java.security.SecureRandom;
import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class Random implements Command.Class {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Makes sure there's arguments
            if (args.isEmpty())
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }
            
            switch (args.get(0).toLowerCase())
            {
                case "number":
                    SecureRandom rand = new SecureRandom();
                    
                    // If the args size is 2, custom number was inputted
                    if (args.size() == 2)
                    {
                        // Try block is to catch if the argument wasn't a number
                        try {
                            int i = Integer.parseInt(args.get(1));

                            // Makes sure the input isn't 0
                            if (i == 0)
                            {
                                messageHandler.sendError("Cannot use 0!");
                                return;
                            }

                            // Gets the random numbers and sends
                            messageHandler.sendEmbed(new EmbedBuilder().withDesc("Your random number is `"+rand.nextInt(i)+"`. (out of `"+i+"`)"));
                        } catch (NumberFormatException throwaway)
                        {
                            messageHandler.sendError("Invalid Number");
                        }
                        return;
                    }

                    // Sends a random number within 100
                    messageHandler.sendEmbed(new EmbedBuilder().withDesc("Your random number is `"+rand.nextInt(100)+"`."));
                    return;
                case "quote":
                    // Gets a random quote
                    JSONObject obj = JsonHandler.handle.read("https://talaikis.com/api/quotes/random/");
                    
                    // Builds the embed
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.appendField("Author", obj.getString("author"), true);

                    embedBuilder.appendField("Quote", obj.getString("quote"), true);

                    // Sends the embed
                    messageHandler.sendEmbed(embedBuilder);
                    return;
                case "word":
                    String word = ChadVar.wordsList.get(new java.util.Random().nextInt(ChadVar.wordsList.size()));

                    // Makes the first letter of the word uppercase
                    String uppercaseWord = word.toUpperCase().charAt(0)+word.substring(1);

                    // Gets a random word and sends it
                    messageHandler.sendEmbed(new EmbedBuilder().withDesc(uppercaseWord));
                    return;
                default:
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
            }
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("random quote", "Gives random quote.");
        st.put("random number [max]", "Gives random number with an optional max value.");
        st.put("random word", "Gets a random word.");
        return Command.helpCommand(st, "Random", e);

    }
}
