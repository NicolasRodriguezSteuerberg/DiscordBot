package com.nsteuerberg.personal_bot.music.service.audio;

import com.nsteuerberg.personal_bot.music.listener.MusicEventListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GuildMusicHandler {
    private final AudioPlayer player;
    private final AudioManager manager;
    private final AudioHandler audioHandler;
    private final TrackScheduler trackScheduler;
    private final BlockingQueue<AudioTrack> playlist;
    private final Runnable onDisconnect;
    private final List<MusicEventListener> listeners;

    public GuildMusicHandler(AudioPlayerManager playerManager, AudioManager manager, Runnable onDisconnect) {
        this.player = playerManager.createPlayer();
        this.manager = manager;
        this.onDisconnect = onDisconnect;

        playlist = new LinkedBlockingQueue<>();
        listeners = new ArrayList<>();

        trackScheduler = new TrackScheduler(playlist, player, this);
        audioHandler = new AudioHandler(player);

        this.manager.setSendingHandler(audioHandler);
    }

    public void addTrack(AudioTrack track) {
        trackScheduler.add(track);
    }

    public void addTrack(List<AudioTrack> tracks) {
        trackScheduler.add(tracks);
    }

    public List<AudioTrack> getPlaylist() {
        return playlist.stream().toList();
    }

    public AudioTrack getActualSong() {
        return player.getPlayingTrack();
    }

    void disconnect() {
        firePlaylistEmpty();
        player.stopTrack();
        player.destroy();
        this.manager.closeAudioConnection();
        onDisconnect.run();
    }

    public void addListener(MusicEventListener listener) {
        listeners.add(listener);
    }

    void fireAddTrack(AudioTrack track, int position) {
        listeners.forEach(l -> l.trackAdded(track, position));
    }

    void fireStartTrack(AudioTrack track) {
        listeners.forEach(l -> l.trackStart(track));
    }

    void firePlaylistEmpty() {
        listeners.forEach(l -> l.playlistEmpty());
    }

    void fireTrackError(AudioTrack track, String exception) {
        listeners.forEach(l -> l.trackError(track, exception));
    }
}
