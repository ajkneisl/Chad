package org.woahoverflow.chad.commands.developer;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Sets the balance of a user
 *
 * @author sho
 */
public class ModifyBalance implements Command.Class {
    @Override
    public final Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            String prefix = (String) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            // Checks if the arguments is empty
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "setbalance **new balance**");
                return;
            }

            // If the arguments size is 0, set the value for the author
            if (args.size() == 1) {
                // Makes sure the argument is actually a long
                try {
                    Long.parseLong(args.get(0));
                } catch (NumberFormatException e1) {
                    messageHandler.sendError("Invalid Value!");
                    return;
                }

                // The author's player instance
                Player player = PlayerHandler.getPlayer(e.getAuthor().getLongID());

                // Sets the balance
                player.setObject(DataType.BALANCE, Long.parseLong(args.get(0)));

                System.out.println(args.get(0));

                // Sends the message
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Set your balance to `"+args.get(0)+"`."));
                return;
            }

            // If the arguments size is 2, set the value for another user
            if (args.size() == 2) {
                // Checks if anyone is mentioned
                if (e.getMessage().getMentions().isEmpty()) {
                    messageHandler.sendPresetError(MessageHandler.Messages.NO_MENTIONS);
                    return;
                }

                // Makes sure the argument is actually a long
                try {
                    Long.parseLong(args.get(0));
                } catch (NumberFormatException e1) {
                    new MessageHandler(e.getChannel(), e.getAuthor()).sendError("Invalid Integer!");
                    return;
                }

                // The mentioned user's player instance
                Player player = PlayerHandler.getPlayer(e.getMessage().getMentions().get(0).getLongID());

                // Sets the balance of the mentioned user
                player.setObject(DataType.BALANCE, Long.parseLong(args.get(0)));

                // Sends the message
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Set `" + e.getMessage().getMentions().get(0).getName() + "`'s balance to `"+args.get(0)+"`."));
                return;
            }
            messageHandler.sendError("Invalid Arguments!");
        };
    }

    @Override
    public final Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("modbal <amount> [@user]", "Sets a user's balance.");
        return Command.helpCommand(st, "Modify Balance", e);
    }
}
