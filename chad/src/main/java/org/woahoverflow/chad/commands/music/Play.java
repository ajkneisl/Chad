/*package com.chad.commands.music;

import ChadVar;
import com.chad.handle.audio.MusicHandler;
import com.chad.handle.audio.MusicUtils;
import Command;
import ChadException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import java.util.List;

public class Play implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            try {
                IGuild guild = e.getGuild();
                IVoiceChannel vc = e.getAuthor().getVoiceStateForGuild(guild).getChannel();

                String ident = "";

                for (String s : args)
                {
                    ident += s + " ";
                }

                ident = ident.trim();

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

                MusicHandler handler = ChadVar.musicHandlers.get(guild);

                ChadVar.UI_HANDLER.addLog("got music handler for guild '" + guild.getStringID() + "'");

                // if the guild doesnt have a music handler, register one
                if (handler == null)
                {
                    try {
                        AudioPlayerManager apm = new DefaultAudioPlayerManager();

                        handler = new MusicHandler(apm);
                        ChadVar.musicHandlers.put(guild, handler);
                    } catch (Exception exe) {
                        exe.printStackTrace();
                        ChadException.error("A problem occurred while creating a new music handler: " + exe.getMessage());
                    }
                }

                ChadVar.UI_HANDLER.addLog("about to play");

                //MusicUtils.loadAndPlay(e, e.getChannel(), ident); // lookup and enqueue the track provided

                final String identifier = "https://www.youtube.com/watch?v=18JQUYgpOlw";

                AudioPlayerManager manager = MusicUtils.getGuildAudioPlayer(guild).manager;

                YoutubeAudioSourceManager ytasm = new YoutubeAudioSourceManager();

                //YoutubeAudioTrack track = new YoutubeAudioTrack(new AudioTrackInfo("PoleteR - April Showers", "MrSuicideSheep", 271L, "poleter april showers", true, identifier), ytasm);

                //MusicUtils.getGuildAudioPlayer(guild).manager.registerSourceManager(ytasm);
                manager.registerSourceManager(new HttpAudioSourceManager());

                AudioSourceManagers.registerRemoteSources(manager);

                MusicUtils.getGuildAudioPlayer(guild).manager.loadItem(identifier, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack audioTrack) {
                        MusicUtils.getGuildAudioPlayer(guild).scheduler.queue(audioTrack);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist audioPlaylist) {
                        ChadVar.UI_HANDLER.addLog("loaded playlist");
                    }

                    @Override
                    public void noMatches() {
                        ChadVar.UI_HANDLER.addLog("no matches found for: " + identifier);
                    }

                    @Override
                    public void loadFailed(FriendlyException e) {
                        e.printStackTrace();
                        ChadVar.UI_HANDLER.addLog("load failed: " + e);
                    }
                });

                ChadVar.UI_HANDLER.addLog("registered YoutubeAudioSourceManager");

                //MusicUtils.getGuildAudioPlayer(guild).scheduler.queue(track);

                ChadVar.UI_HANDLER.addLog("played: " + identifier);
            } catch (Exception ex) {
                ex.printStackTrace();
                ChadException.error("Exception in Play: " + ex);
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}*/
