package com.jhobot.handle;

import com.jhobot.core.ChadVar;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class MusicUtils {

	public static synchronized MusicHandler getGuildAudioPlayer(IGuild guild)
	{
		MusicHandler musicManager = null;
		try {
			musicManager = ChadVar.musicHandlers.get(guild);
		} catch (Exception e) { }

		if(musicManager == null)
		{
			musicManager = new MusicHandler(new com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager());

			musicManager.manager.registerSourceManager(new YoutubeAudioSourceManager());
			//musicManager.manager.registerSourceManager(new SoundCloudAudioSourceManager());
			//musicManager.manager.registerSourceManager(new HttpAudioSourceManager());

            AudioSourceManagers.registerRemoteSources(musicManager.manager);

			ChadVar.musicHandlers.put(guild, musicManager);

			ChadVar.UI_HANDLER.addLog("#getGuildAudioPlayer(): finished musicManager setup and registration");
		}

		guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());
		return musicManager;
	}

	public static void loadAndPlay(final MessageReceivedEvent event, final IChannel channel, final String trackURL)
	{
		final MusicHandler musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.player.setVolume(25);

		if(musicManager.scheduler.getQueueSize() < 10) {
			musicManager.manager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack audioTrack) {
				    ChadVar.UI_HANDLER.addLog("track loaded and enqueued: " + audioTrack.getIdentifier());
					play(channel.getGuild(), musicManager, audioTrack);
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					for (AudioTrack track : playlist.getTracks()) {
						ChadVar.UI_HANDLER.addLog("track loaded and enqueued: " + track.getIdentifier());
						play(channel.getGuild(), musicManager, track);
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
		} else {
			ChadVar.UI_HANDLER.addLog("queue is full"); //TODO: notify user
		}
	}

	public static void play(IGuild guild, MusicHandler musicManager, AudioTrack track) {
		musicManager.scheduler.queue(track);
	}

	public static void pause(IGuild guild, MusicHandler musicManager) {
		musicManager.player.setPaused(true);
	}

	public static void unpause(IGuild guild, MusicHandler musicManager) {
		musicManager.player.setPaused(false);
	}

	public static void skipTrack(IChannel channel) {
		MusicHandler musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.scheduler.nextTrack();
	}
}