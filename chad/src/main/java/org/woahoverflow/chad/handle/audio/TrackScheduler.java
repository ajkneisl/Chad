/*package com.chad.handle;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {

	private final AudioPlayer player;
	private final Queue<AudioTrack> queue;
	private boolean repeat;
	private AudioTrack lastTrack;

	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedList<>();
		this.repeat = false;
	}

	public void queue(AudioTrack track) {
		if(!player.startTrack(track, true)) {
			queue.offer(track);
		}
	}

	public Queue<AudioTrack> getQueue() { return this.queue; }

	public void nextTrack() {
		player.startTrack(queue.poll(), false);
	}

	public void repeatTrack() {
		player.startTrack(lastTrack.makeClone(), false);
	}

	public void clearQueue() {
		this.queue.clear();
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean getRepeat() {
		return this.repeat;
	}

	public void shuffle() {
		Collections.shuffle((List<?>) queue);
	}

	public boolean toggleRepeat() {
		if(this.repeat) {
			this.repeat = false;
		} else {
			this.repeat = true;
		}
		return this.repeat;
	}

	public int getQueueSize() {
		return this.queue.size();
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		this.lastTrack = track;
		if(endReason.mayStartNext) {
			if(repeat) {
				repeatTrack();
			} else {
				nextTrack();
			}
		}
	}
}*/