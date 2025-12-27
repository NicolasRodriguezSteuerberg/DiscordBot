package com.nsteuerberg.personal_bot.music.audio.model;

import com.nsteuerberg.personal_bot.music.audio.MusicMessageManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final BlockingQueue<AudioTrack> queue;
    private final AudioPlayer player;
    private final MusicMessageManager messageManager;
    private final Runnable disconnect;

    public TrackScheduler(AudioPlayer player, MusicMessageManager messageManager, Runnable disconnect) {
        queue = new LinkedBlockingQueue<>();
        this.player = player;
        this.messageManager = messageManager;
        this.disconnect = disconnect;
    }

    public void add(AudioTrack track, String userId) {
        track.setUserData(new TrackEntry(userId, false));
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            messageManager.onAddedTrack(track, queue.size());
        }
    }

    public List<AudioTrack> getQueue() {
        return new ArrayList<>(queue);
    }

    public void next() {
        AudioTrack track = queue.poll();
        if (track!=null) {
            System.out.println("playing next");
            player.startTrack(track, false);
        } else {
            messageManager.onQueueEmpty();
            disconnect.run();
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);
        AudioTrack track = player.getPlayingTrack();
        TrackEntry entry = (TrackEntry) track.getUserData();
        entry.setPause();
        messageManager.onPlayPause(track);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
        AudioTrack track = player.getPlayingTrack();
        TrackEntry entry = (TrackEntry) track.getUserData();
        entry.setPlaying();
        messageManager.onPlayPause(track);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
        TrackEntry entry = (TrackEntry) track.getUserData();
        entry.setPlaying();
        messageManager.onStartTrack(track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
        if (endReason.mayStartNext) next();
    }
}
