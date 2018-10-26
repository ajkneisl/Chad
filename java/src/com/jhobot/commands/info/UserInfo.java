package com.jhobot.commands.info;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UserInfo implements Command {
    @DefineCommand(category = Category.INFO)
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            IUser u;

            if (e.getMessage().getMentions().isEmpty())
            {
                StringBuilder sb = new StringBuilder();

                for (String s : args)
                {
                    sb.append(s).append(" ");
                }

                if (e.getGuild().getUsersByName(sb.toString().trim()).isEmpty())
                {
                    new MessageHandler(e.getChannel()).sendError("Invalid User");
                    return;
                }

                u = e.getGuild().getUsersByName(sb.toString().trim()).get(0);
            } else {
                u = e.getMessage().getMentions().get(0);
            }

            StringBuilder roles = new StringBuilder();
            for (IRole r : u.getRolesForGuild(e.getGuild()))
            {
                roles.append(r.getName()).append(", ");
            }
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("User : " + u.getName());
            b.appendField("Roles", roles.toString().substring(0, roles.toString().length()-2) + "[" + u.getRolesForGuild(e.getGuild()).size() + "]", true);
            String human;
            if (u.isBot())
                human = "False";
            else
                human = "True";
            b.appendField("Human", human, true);
            Date date = Date.from(e.getGuild().getJoinTimeForUser(u));
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date date2 = Date.from(u.getCreationDate());
            b.appendField("Guild Join Date", format.format(date), false);
            b.appendField("Account Creation Date", format.format(date2), false);

            b.withImage(u.getAvatarURL());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());

            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("userinfo <user>", "Gives information about the mentioned user.");
        return HelpHandler.helpCommand(st, "User Info", e);
    }
}
