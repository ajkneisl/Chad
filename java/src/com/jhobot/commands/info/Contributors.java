package com.jhobot.commands.info;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import org.json.JSONArray;
import org.json.JSONException;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

public class Contributors implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            JSONArray o = null;
            b.withTitle("Contributors for Chad");
            try {
                o = ChadVar.JSON_HANDLER.readArray("https://api.github.com/repos/shoganeko/chad/contributors");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (o == null)
                return;

            int i = 0;
            while (o.length() > i)
            {
                b.appendField(o.getJSONObject(i).getString("login"), "Commits : " + o.getJSONObject(i).getInt("contributions"), false);
                i++;
            }
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("contributors", "Gets all contributors that've committed to Chad.");
        return HelpHandler.helpCommand(st, "Contributors", e);
    }
}
