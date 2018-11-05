package com.jhobot.handle;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.ui.ChadException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import sx.blah.discord.handle.obj.IGuild;

import java.util.LinkedList;
import java.util.Queue;

public class MusicHandler {
    public IGuild guild;
    public Queue<AudioTrack> queue = new LinkedList<>();
    public AudioPlayerManager playerManager;
    public AudioPlayer player;
    public TrackScheduler scheduler;
    public boolean currentlyPlaying = false;

    public MusicHandler(IGuild guild)
    {
        try {
            ChadVar.UI_HANDLER.addLog("1");
            this.guild = guild;
            ChadVar.UI_HANDLER.addLog("2");
            playerManager = new com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager();
            ChadVar.UI_HANDLER.addLog("3");
            player = playerManager.createPlayer();
            ChadVar.UI_HANDLER.addLog("4");
            guild.getAudioManager().setAudioProvider(getAudioProvider());
            ChadVar.UI_HANDLER.addLog("5");
            //YoutubeAudioSourceManager youtubeAudioSourceManager = new com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager();
            ChadVar.UI_HANDLER.addLog("6");
            //youtubeAudioSourceManager.configureRequests(config -> RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build());
            ChadVar.UI_HANDLER.addLog("7");
            //playerManager.registerSourceManager(youtubeAudioSourceManager);
            ChadVar.UI_HANDLER.addLog("8");
            //playerManager.registerSourceManager(new HttpAudioSourceManager());
            ChadVar.UI_HANDLER.addLog("9");
            AudioSourceManagers.registerRemoteSources(playerManager);
            ChadVar.UI_HANDLER.addLog("10");
            scheduler = new TrackScheduler(this, player);
            ChadVar.UI_HANDLER.addLog("11");
            player.addListener(scheduler);
            ChadVar.UI_HANDLER.addLog("12");
        } catch (Exception ex) {
            ex.printStackTrace();
            ChadException.error("Exception in MusicHandler: " + ex);
        }
    }

    /*
     * Lookup a track, and enqueue it.
     */
    public void play(String identifier)
    {
        try {
            playerManager.loadItem(identifier, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    ChadVar.UI_HANDLER.addLog("track loaded and enqueued: " + identifier);
                    scheduler.queue(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        ChadVar.UI_HANDLER.addLog("track loaded and enqueued: " + identifier);
                        scheduler.queue(track);
                    }
                }

                @Override
                public void noMatches() {
                    ChadVar.UI_HANDLER.addLog("no matches for identifier");
                }

                @Override
                public void loadFailed(FriendlyException throwable) {
                    ChadVar.UI_HANDLER.addLog("track load failed: " + throwable);
                }
            });

            while (getAudioProvider().isReady())
            {
                ChadVar.UI_HANDLER.addLog("audio provider is ready");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Pause the track.
     */
    public void pause()
    {
        player.setPaused(true);
    }

    /*
     * Resume the track.
     */
    public void resume()
    {
        player.setPaused(false);
    }

    /*
     * Returns an AudioProvider wrapper for the AudioPlayer.
     */
    public AudioProvider getAudioProvider()
    {
        if (player == null)
        {
            ChadVar.UI_HANDLER.addLog("#getAudioProvider(): player is null");

            if (playerManager == null)
            {
                ChadVar.UI_HANDLER.addLog("#getAudioProvider(): playerManager is null");
                playerManager = new com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager();
                ChadVar.UI_HANDLER.addLog("#getAudioProvider(): playerManager was assigned to");
                if (playerManager == null)
                {
                    ChadVar.UI_HANDLER.addLog("#getAudioProvider(): playerManager is still null");
                }
            }

            player = playerManager.createPlayer();
            if (player == null)
            {
                ChadVar.UI_HANDLER.addLog("#getAudioProvider(): player is still null");
            }
        }

        ChadVar.UI_HANDLER.addLog("returning audio provider");
        return new AudioProvider(player);
    }
}
