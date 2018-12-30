package org.woahoverflow.chad.commands.music;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

/**
 * @author sho
 * @since 0.7.0
 */
public class Skip implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // The guild's music manager
            GuildMusicManager manager = Chad.getMusicManager(e.getGuild());

            // Chad's voice channel
            IVoiceChannel channel = e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel();

            // Makes sure that Chad is playing music
            if (channel == null || manager.player.isPaused())
            {
                messageHandler.sendMessage("Chad isn't playing music!");
                return;
            }

            // Makes sure the author is in the same channel as the bot
            if (channel != e.getAuthor().getVoiceStateForGuild(e.getGuild()).getChannel()) {
                messageHandler.sendError("You aren't in the same channel as me!");
                return;
            }

            // If they wanted to skip all
            if (args.size() == 1 && args.get(0).equalsIgnoreCase("all"))
            {
                manager.clear();

                messageHandler.sendError("Queue is empty, leaving!");
                channel.leave();

                // Gets rid of the playing song
                manager.clear();
                return;
            }

            // If there's a next track, play that
            if (manager.scheduler.nextTrack())
            {
                messageHandler.sendMessage("Skipped current song!");
                return;
            }

            // If there's not another track, do this
            messageHandler.sendError("Queue is empty, leaving!");
            channel.leave();

            // Gets rid of the playing song
            manager.clear();
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("skip", "Skips the current song.");
        st.put("skip all", "Skips all the current songs.");
        return Command.helpCommand(st, "Skip", e);
    }
}
