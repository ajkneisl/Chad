package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoRole implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            String option = args.get(0).toLowerCase();
            MessageHandler m = new MessageHandler(e.getChannel());
            System.out.println("Auto Role: " + option);
            switch (option) {
                default:
                    break;
                case "on":
                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "role_on_join", true);
                    ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());
                    EmbedBuilder on_emb = new EmbedBuilder();
                    on_emb.withTitle("Auto Role");
                    on_emb.withDesc("Auto Role enabled.");
                    on_emb.withFooterText(Util.getTimeStamp());
                    m.sendEmbed(on_emb.build());
                    break;
                case "off":
                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "role_on_join", false);
                    ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());
                    EmbedBuilder off_emb = new EmbedBuilder();
                    off_emb.withTitle("Auto Role");
                    off_emb.withDesc("Auto Role disabled.");
                    off_emb.withFooterText(Util.getTimeStamp());
                    m.sendEmbed(off_emb.build());
                    break;
                case "set":
                    args.remove(0); // remove option argument
                    StringBuilder sb = new StringBuilder();
                    List<IRole> roles = new ArrayList<>();
                    for (String s : args) {
                        sb.append(s).append(" ");
                        roles = RequestBuffer.request(() -> e.getGuild().getRolesByName(sb.toString().trim())).get();
                        if (!roles.isEmpty()) break;
                    }
                    IRole set_role = roles.get(0);
                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "join_role", set_role.getStringID());
                    ChadVar.CACHE_DEVICE.cacheGuild(e.getGuild());
                    EmbedBuilder set_emb = new EmbedBuilder();
                    set_emb.withTitle("Auto Role");
                    set_emb.withDesc("New users will now automatically receive the role: " + set_role.getName());
                    set_emb.withFooterText(Util.getTimeStamp());
                    m.sendEmbed(set_emb.build());
                    break;
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("autorole [on/off]", "Toggles automatic role assignment features.");
        st.put("autorole set [role name]", "Sets role.");
        return HelpHandler.helpCommand(st, "Auto Role", e);
    }
}
