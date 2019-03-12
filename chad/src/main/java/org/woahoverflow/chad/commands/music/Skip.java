package org.woahoverflow.chad.commands.music;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;
import java.util.List;

import static org.woahoverflow.chad.framework.handle.MusicHandlerKt.getMusicManager;

/**
 * Skips songs within the guild's music player
 *
 * @author sho
 */
public class Skip implements Command.Class {
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Chad's voice channel
            IVoiceChannel channel = e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel();

            // Makes sure that Chad is playing music
            if (channel == null) {
                messageHandler.sendMessage("Chad isn't playing music!");
                return;
            }

            // Makes sure the author is in the same channel as the bot
            if (channel != e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel()) {
                messageHandler.sendError("You aren't in the same channel as Chad!");
                return;
            }

            // The guild's music manager
            GuildMusicManager manager = getMusicManager(e.getGuild(), channel);

            // Skips all of the songs in the queue
            if (args.size() == 1 && args.get(0).equalsIgnoreCase("all")) {
                manager.clear();
                messageHandler.sendMessage("Skipped all songs in queue!");
                return;
            }

            manager.scheduler.nextTrack();
            messageHandler.sendMessage("Skipped current song!");
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("skip", "Skips the current song.");
        st.put("skip all", "Skips all the current songs.");
        return Command.helpCommand(st, "Skip", e);
    }
}
