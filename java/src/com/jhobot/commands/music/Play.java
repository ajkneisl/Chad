package com.jhobot.commands.music;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.MusicHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.ui.ChadException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Play implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            IGuild guild = e.getGuild();
            IVoiceChannel vc = e.getAuthor().getVoiceStateForGuild(guild).getChannel();

            if (!vc.isConnected())
            {
                vc.join();
                ChadVar.UI_HANDLER.addLog("Joined the voice channel");
            }

            while (!vc.isConnected())
            {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


            try {
                sx.blah.discord.util.audio.AudioPlayer.getAudioPlayerForGuild(e.getGuild()).queue(
                        new URL("https://cce.oeaa.cc/e0031b32820151234868e1c25cb66d63/18JQUYgpOlw")
                );
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            }

        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
