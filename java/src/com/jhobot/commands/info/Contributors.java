package com.jhobot.commands.info;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Category;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.DefineCommand;
import com.jhobot.handle.commands.PermissionLevels;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

import java.util.List;

public class Contributors implements Command {
    @DefineCommand(category = Category.INFO)
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            JSONArray o = null;
            b.withTitle("Contributors for Chad");
            try {
                o = ChadBot.JSON_HANDLER.readArray("https://api.github.com/repos/shoganeko/chad/contributors");
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
        return null;
    }
}
