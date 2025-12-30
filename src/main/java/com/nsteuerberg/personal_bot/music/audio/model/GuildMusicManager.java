package com.nsteuerberg.personal_bot.music.audio.model;

import com.nsteuerberg.personal_bot.music.audio.AudioHandler;
import com.nsteuerberg.personal_bot.music.audio.MusicMessageManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class GuildMusicManager {
    private final AudioPlayer player;
    private final AudioManager manager;
    private final MusicMessageManager messageManager;
    private final AudioHandler handler;
    private final TrackScheduler scheduler;
    private final Runnable onDisconnect;

    public GuildMusicManager(AudioPlayerManager playerManager, AudioManager manager, TextChannel channel, Runnable onDisconnect) {
        this.player = playerManager.createPlayer();
        this.manager = manager;
        this.onDisconnect = onDisconnect;
        messageManager = new MusicMessageManager(channel);

        scheduler = new TrackScheduler(player, messageManager, () -> disconnect());
        handler = new AudioHandler(player);

        this.player.addListener(scheduler);
        manager.setSendingHandler(handler);
    }

    public List<AudioTrack> getQueue() {
        return scheduler.getQueue();
    }

    public void addTrack(List<AudioTrack> tracks, String userId) {
        tracks.forEach(t -> scheduler.add(t, userId));
    }

    public void addTrack(AudioTrack track, String userId) {
        scheduler.add(track, userId);
    }

    public AudioTrack getCurrentSong() {
        return player.getPlayingTrack();
    }

    public void playPause(boolean isPause) {
        player.setPaused(isPause);
    }

    public void skip() {
        scheduler.next();
    }

    public void stop() {
        disconnect();
    }

    void disconnect() {
        messageManager.onQueueEmpty();
        player.stopTrack();
        player.destroy();
        manager.closeAudioConnection();
        onDisconnect.run();
    }
}
