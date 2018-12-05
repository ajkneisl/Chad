package org.woahoverflow.chad.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.audio.obj.GuildMusicManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author sho
 * @since 0.7.0
 */
public class Skip implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            GuildMusicManager manager = Chad.getMusicManager(e.getGuild());
            if (e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel() == null || manager.player.isPaused())
            {
                new MessageHandler(e.getChannel()).sendMessage("Chad isn't playing music!");
                return;
            }

            manager.scheduler.nextTrack();
            new MessageHandler(e.getChannel()).sendMessage("Skipped current song!");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("skip", "Skips the current song");
        return Command.helpCommand(st, "Skip", e);
    }
}
