package com.nsteuerberg.personal_bot.music.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TrackSearchHandler implements AudioLoadResultHandler {
    private final CompletableFuture<List<AudioTrack>> future;
    private final int limit;

    public TrackSearchHandler(CompletableFuture<List<AudioTrack>> future, int limit) {
        this.future = future;
        this.limit = limit;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        future.complete(List.of(
                audioTrack
        ));
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        future.complete(audioPlaylist.getTracks().stream()
                .limit(limit)
                .toList()
        );
    }

    @Override
    public void noMatches() {
        future.complete(List.of());
    }

    @Override
    public void loadFailed(FriendlyException e) {
        future.completeExceptionally(e);
    }
}
