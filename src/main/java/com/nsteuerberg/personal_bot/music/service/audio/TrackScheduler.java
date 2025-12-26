package com.nsteuerberg.personal_bot.music.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final BlockingQueue<AudioTrack> playlist;
    private final AudioPlayer player;
    private final GuildMusicHandler guildMusicHandler;

    public TrackScheduler(BlockingQueue<AudioTrack> playlist, AudioPlayer player, GuildMusicHandler guildMusicHandler) {
        this.playlist = playlist;
        this.player = player;
        // Para poder recibir los eventos de onTrackStart, onTrackFinish...
        this.player.addListener(this);
        this.guildMusicHandler = guildMusicHandler;
    }

    public void add(AudioTrack track) {
        if(!player.startTrack(track, true)){
            playlist.offer(track);
            guildMusicHandler.fireAddTrack(track, playlist.size());
        }
    }

    public void add(List<AudioTrack> tracks) {
        tracks.forEach(this::add);
    }

    public void next() {
        AudioTrack track = playlist.poll();
        if (track != null) {
            player.startTrack(track, false);
        } else {
            guildMusicHandler.disconnect();
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
        guildMusicHandler.fireStartTrack(track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // ToDo Comprobar que pasa si dejas el super
        // super.onTrackEnd(player, track, endReason);
        System.out.println("Track end");
        if (endReason.mayStartNext) next();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        guildMusicHandler.fireTrackError(track, "Stuck " + thresholdMs);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        guildMusicHandler.fireTrackError(track, "Stuck " + thresholdMs);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        System.out.println(exception.getMessage());
    }

}
