package org.woahoverflow.chad.commands.info;

import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.Command.Class;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

public class Contributors implements Class {

    private static final Pattern COMPILE = Pattern.compile(", ");

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());
            // Creates embed builder and gets the JSON array from the cdn
            EmbedBuilder embedBuilder = new EmbedBuilder();
            JSONArray o = ChadVar.JSON_DEVICE.readArray("https://cdn.woahoverflow.org/chad/data/contributors.json");

            // Checks if the user is trying to see a specific profile
            if (args.size() == 2 && args.get(0).equalsIgnoreCase("view"))
            {
                JSONObject object = null;
                for (Object obj : o)
                {
                    // Checks if the user found is the user that was requested
                    if (((JSONObject) obj).getString("display_name").equalsIgnoreCase(args.get(1)))
                    {
                        // if
                        object = (JSONObject) obj;
                    }
                }

                // Checks to see if it actually found a correct user.
                if (object == null)
                {
                    messageHandler.sendError("Invalid User");
                    return;
                }

                // Builds profile
                embedBuilder.withUrl("https://github.com/" + object.getString("github_name"));
                embedBuilder.withTitle("https://github.com/" + object.getString("display_name"));
                embedBuilder.withImage(object.getString("github_avatar_url"));
                embedBuilder.withDesc("Discord : " + object.getString("discord"));
                messageHandler.sendEmbed(embedBuilder);
                return;
            }

            // Creates a string builder and adds all the official contributors to it.
            StringBuilder stringBuilder = new StringBuilder();
            embedBuilder.withTitle("Official Contributors for Chad");
            o.forEach((obj) -> {
                JSONObject json = (JSONObject) obj;
                stringBuilder.append(", ").append(json.getString("display_name"));
            });

            // Adds the description (replaces the first ', ' to nothing)
            embedBuilder.withDesc(COMPILE.matcher(stringBuilder.toString().trim()).replaceFirst(""));

            // Sends
            messageHandler.sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("contributors", "Gets all contributors that've committed to Chad.");
        st.put("contributors view <contributor name>", "Views that contributor's profile.");
        return Command.helpCommand(st, "Contributors", e);
    }
}
