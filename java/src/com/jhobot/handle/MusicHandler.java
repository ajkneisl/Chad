package com.jhobot.handle;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.ui.ChadException;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import sx.blah.discord.handle.obj.IGuild;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.Queue;

public class MusicHandler {
    public IGuild guild;
    public Queue<AudioTrack> queue = new LinkedList<>();
    public AudioPlayerManager playerManager;
    public AudioPlayer player;
    public TrackScheduler scheduler;
    public boolean currentlyPlaying = false;

    public AudioDataFormat format;
    public AudioInputStream stream;
    public SourceDataLine.Info info;
    public SourceDataLine line;

    public MusicHandler(IGuild guild)
    {
        try {
            this.guild = guild;
            guild.getAudioManager().setAudioProvider(getAudioProvider());
            playerManager = new com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager();

            format = playerManager.getConfiguration().getOutputFormat();
            stream = AudioPlayerInputStream.createStream(player, format, 10000L, false);
            info = new DataLine.Info(SourceDataLine.class, new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 44100, 16, 2, 4, 44100, false));
            line = (SourceDataLine) AudioSystem.getLine(info);

            YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();
            youtubeAudioSourceManager.configureRequests(config -> RequestConfig.copy(config).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build());

            playerManager.registerSourceManager(youtubeAudioSourceManager);

            if (playerManager == null)
                ChadVar.UI_HANDLER.addLog("playerManager is null");

            ChadVar.UI_HANDLER.addLog("passed playerManager init");

            player = playerManager.createPlayer();

            ChadVar.UI_HANDLER.addLog("passed player init");

            scheduler = new TrackScheduler(this, player);

            ChadVar.UI_HANDLER.addLog("passed scheduler init");

            player.addListener(scheduler);

            ChadVar.UI_HANDLER.addLog("passed manager setup");

            if (player == null)
                ChadVar.UI_HANDLER.addLog("player is null");

            ChadVar.UI_HANDLER.addLog("END OF MusicHandler CONSTRUCTOR");
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
                    scheduler.queue(track);
                    ChadVar.UI_HANDLER.addLog("track loaded and enqueued: " + identifier);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        scheduler.queue(track);
                        ChadVar.UI_HANDLER.addLog("track loaded and enqueued: " + identifier);
                    }
                }

                @Override
                public void noMatches() {
                    ChadVar.UI_HANDLER.addLog("no matches for identifier");
                }

                @Override
                public void loadFailed(FriendlyException throwable) {
                    ChadVar.UI_HANDLER.addLog("track load failed");
                }
            });

            line.open(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, 44100, 16, 2, 4, 44100, false));
            line.start();

            byte[] buffer = new byte[format.maximumChunkSize()];
            int chunkSize;

            while ((chunkSize = stream.read(buffer)) >= 0) {
                line.write(buffer, 0, chunkSize);
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
