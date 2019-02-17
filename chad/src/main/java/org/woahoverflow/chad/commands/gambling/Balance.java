package org.woahoverflow.chad.commands.gambling;

import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Gets a user's balance
 *
 * @author sho
 */
public class Balance implements Command.Class {
    @Override
    public final Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            if (args.isEmpty()) {
                Player player = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Your balance is `"+player.getObject(DataType.BALANCE)+"`!"));
                return;
            }

            if (e.getMessage().getMentions().size() == 1) {
                IUser targetIUser = e.getMessage().getMentions().get(0);
                Player player = PlayerHandler.handle.getPlayer(targetIUser.getLongID());
                messageHandler.sendEmbed(new EmbedBuilder().withDesc('`' +targetIUser.getName()+"`'s balance is `"+player.getObject(DataType.BALANCE)+"`!"));
                return;
            }

            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, GuildHandler.handle.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX) + "balance");
        };
    }

    @Override
    public final Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("balance", "See your balance.");
        st.put("balance <@user>", "See another user's balance.");
        return Command.helpCommand(st, "Balance", e);
    }
}
