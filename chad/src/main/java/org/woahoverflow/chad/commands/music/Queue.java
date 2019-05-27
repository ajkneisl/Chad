package org.woahoverflow.chad.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.MusicHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.GuildMusicManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Gets the music player's queue
 *
 * @author sho
 */
public class Queue implements Command.Class {
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            IVoiceChannel chadChannel = RequestBuffer.request(() -> e.getClient().getOurUser().getVoiceStateForGuild(e.getGuild()).getChannel()).get();

            // If Chad's not even joined
            if (chadChannel == null) {
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("There's no things currently playing!"));
                return;
            }

            GuildMusicManager manager = MusicHandler.getMusicManager(e.getGuild(), chadChannel);
            List<AudioTrack> queue = manager.scheduler.getFullQueue();

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("remove"))
            {
                try
                {
                    int removeIndex = Integer.parseInt(args.get(1));
                    if (removeIndex > 0)
                    {
                        removeIndex--;
                        String trackTitle = queue.get(removeIndex).getInfo().title;
                        queue.remove(removeIndex);
                        messageHandler.sendMessage(String.format("Removed `%s` from the queue.", trackTitle));
                    } else {
                        messageHandler.sendError("Whoops! You can't use `0` as an index value. *It breaks the **code**.*");
                    }
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // If there's nothing playing
            if (manager.player.getPlayingTrack() == null && queue.size() == 0) {
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("There's no things currently playing!"));
                return;
            }

            // The currently playing song
            String string = manager.player.isPaused() ? "Currently paused. `"+manager.player.getPlayingTrack().getInfo().title+"` by `"+manager.player.getPlayingTrack().getInfo().author+"` was playing.\n\n"
                    : "Currently playing `"+manager.player.getPlayingTrack().getInfo().title+"` by `"+manager.player.getPlayingTrack().getInfo().author+"`\n\n";

            // Builds the queue into the string
            if (queue.size() <= 10) {
                string += IntStream.range(0, queue.size()).mapToObj(
                    i -> (i+1) + ". `" + queue.get(i).getInfo().title + "` by `" + queue.get(i)
                        .getInfo().author + "`\n" + queue.get(i).getInfo().uri + "\n\n")
                    .collect(Collectors.joining());
            } else {
                string += IntStream.range(0, 10).mapToObj(
                    i -> (i+1) + ". `" + queue.get(i).getInfo().title + "` by `" + queue.get(i)
                        .getInfo().author + "`\n" + queue.get(i).getInfo().uri + "\n\n")
                    .collect(Collectors.joining());

                string += "... and `"+(queue.size()-10)+"` more!\n\n";
            }

            // Builds the queue size into it
            string += "There's currently `" + queue.size() + "` songs in the queue.";

            messageHandler.sendEmbed(new EmbedBuilder().withDesc(string));
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("queue", "Gets the current song(s) in the queue.");
        st.put("queue remove <index>", "Removes a specific song from the queue.");
        return Command.helpCommand(st, "Queue", e);
    }
}