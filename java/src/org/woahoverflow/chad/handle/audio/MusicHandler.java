/*package com.chad.handle.audio;

import com.chad.handle.TrackScheduler;
import com.chad.handle.audio.AudioProvider;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class MusicHandler {
    public final AudioPlayerManager manager;
	public final AudioPlayer player;
	public final TrackScheduler scheduler;

	public MusicHandler(AudioPlayerManager manager) {
	    this.manager = manager;
		player = manager.createPlayer();
		scheduler = new TrackScheduler(player);
		player.addListener(scheduler);
	}

	public AudioProvider getAudioProvider() {
		return new AudioProvider(player);
	}
}*/