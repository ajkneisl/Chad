package org.woahoverflow.chad.commands.fun;

import com.google.common.net.HttpHeaders;
import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Random implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (args.size() == 0)
            {
                help(e, args);
                return;
            }

            switch (args.get(0).toLowerCase())
            {
                case "number":
                    java.util.Random rand = new java.util.Random();
                    if (args.size() == 2)
                    {
                        try {
                            int i2 = Integer.parseInt(args.get(1));

                            if (i2 == 0)
                            {
                                m.sendError("Cannot use 0!");
                                return;
                            }

                            m.send("Number is : " + rand.nextInt(i2), "Random Number");
                        } catch (NumberFormatException ee)
                        {
                            new MessageHandler(e.getChannel()).sendError("Invalid Number");
                        }
                        return;
                    }

                    m.send("Number is : " + rand.nextInt(100), "Random Number");
                    return;
                case "quote":
                    try {
                        JSONObject obj = ChadVar.JSON_HANDLER.read("https://talaikis.com/api/quotes/random/");
                        EmbedBuilder b = new EmbedBuilder();
                        b.withTitle("Random Quote");
                        b.appendField("Author", obj.getString("author"), true);
                        // Switches category's first letter to be uppercase
                        String s1 = obj.getString("cat").substring(0, 1).toUpperCase();
                        String cap = s1 + obj.getString("cat").substring(1);
                        b.appendField("Category", cap, true);
                        b.appendField("Quote", obj.getString("quote"), false);
                        b.withFooterText(Util.getTimeStamp());
                        b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
                        m.sendEmbed(b.build());
                    } catch (Exception ee)
                    {
                        ee.printStackTrace();
                        m.sendError("API Exception!");
                    }
                    return;
                case "sentence":

                    List<String> l = new ArrayList<>();
                    try {
                        URL obj = new URL("https://cdn.woahoverflow.org/chad/data/words.txt");
                        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                        // optional default is GET
                        con.setRequestMethod("GET");
                        //add request header
                        con.setRequestProperty("User-Agent", HttpHeaders.USER_AGENT);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            l.add(inputLine);
                        }
                        in.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    int in = new java.util.Random().nextInt(20);
                    StringBuilder b = new StringBuilder();
                    for (int i = 0; i < in; i++)
                    {
                        b.append(l.get(new java.util.Random().nextInt(l.size()))).append(" ");
                    }
                    new MessageHandler(e.getChannel()).send(b.toString().trim(),"Sentence");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("random quote", "Gives random quote.");
        st.put("random number [max]", "Gives random number with an optional max value.");
        return Command.helpCommand(st, "Random", e);

    }
}
