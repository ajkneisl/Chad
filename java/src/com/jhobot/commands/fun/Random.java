package com.jhobot.commands.fun;

import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.CommandClass;
import org.json.JSONException;
import org.json.JSONObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Random implements CommandClass {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {
        if (args.size() == 0)
        {
            helpCommand(e, db);
            return;
        }

        if (args.get(0).equalsIgnoreCase("number"))
        {
            java.util.Random rand = new java.util.Random();
            if (args.size() == 2)
            {
                try {
                    Integer i2 = Integer.parseInt(args.get(1));
                    
                    if (i2 == 0)
                    {
                        new Messages(e.getCHannel()).sendError("Cannot use 0!");
                        return;
                    }

                    new Messages(e.getChannel()).send("Number is : " + rand.nextInt(i2), "Random Number");
                } catch (NumberFormatException ee)
                {
                    new Messages(e.getChannel()).sendError("Invalid Number");
                }
                return;
            }

            new Messages(e.getChannel()).send("Number is : " + rand.nextInt(100), "Random Number");
            return;
        }

        if (args.get(0).equalsIgnoreCase("quote"))
        {
            try {
                JSONObject obj = JSON.read("https://talaikis.com/api/quotes/random/");
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Random Quote");
                b.appendField("Author", obj.getString("author"), true);
                // Switches category's first letter to be uppercase
                String s1 = cat.substring(0, 1).toUpperCase();
                String cap = s1 + cat.substring(1);
                
                b.appendField("Category", cap, true);
                b.appendField("Quote", obj.getString("quote"), false);
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
                new Messages(e.getChannel()).sendEmbed(b.build());
            } catch (IOException ee)
            {
                ee.printStackTrace();
                new Messages(e.getChannel()).sendError("API Exception!");
            }
            return;
        }

        helpCommand(e, db);
    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Help : Random");
        b.appendField(db.getString(e.getGuild(), "prefix") + "random number [max]", "Gives random number.", false);
        b.appendField(db.getString(e.getGuild(), "prefix") + "random quote", "Gives random quote.", false);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
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
