package org.woahoverflow.chad.commands.fun;

import com.google.common.net.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                            messageHandler.send("Number is : " + rand.nextInt(i), "Random Number");
                        } catch (NumberFormatException throwaway)
                        {
                            messageHandler.sendError("Invalid Number");
                        }
                        return;
                    }

                    // Sends a random number within 100
                    messageHandler.send("Number is : " + rand.nextInt(100), "Random Number");
                    return;
                case "quote":
                    // Gets a random quote
                    JSONObject obj = ChadVar.JSON_DEVICE.read("https://talaikis.com/api/quotes/random/");
                    
                    // Builds the embed
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.withTitle("Random Quote");
                    embedBuilder.appendField("Author", obj.getString("author"), true);

                    // Switches commandCategory's first letter to be uppercase
                    String s1 = obj.getString("cat").substring(0, 1).toUpperCase();
                    String cap = s1 + obj.getString("cat").substring(1);

                    embedBuilder.appendField("Category", cap, true);
                    embedBuilder.appendField("Quote", obj.getString("quote"), false);

                    // Sends the embed
                    messageHandler.sendEmbed(embedBuilder);
                    return;
                case "word":

                    List<String> wordList = new ArrayList<>();
                    try {
                        // Defines the URL and Connection
                        URL url = new URL("https://cdn.woahoverflow.org/chad/data/words.txt");
                        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                        // Sets the properties of the connection
                        con.setRequestMethod("GET");
                        con.setRequestProperty("User-Agent", HttpHeaders.USER_AGENT);

                        @SuppressWarnings("all")
                        // Creates a buffered reader at the word url
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

                        // Adds the words to the list
                        wordList = in.lines().collect(Collectors.toList());

                        // Closes the reader
                        in.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    // Gets a random word and sends it
                    messageHandler.send(wordList.get(new SecureRandom().nextInt(300000)),"Word");
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
