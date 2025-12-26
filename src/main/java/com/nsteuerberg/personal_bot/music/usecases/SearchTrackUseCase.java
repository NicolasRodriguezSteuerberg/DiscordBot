package com.nsteuerberg.personal_bot.music.usecases;

import com.nsteuerberg.personal_bot.music.service.audio.TrackSearchHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class SearchTrackUseCase {
    private final AudioPlayerManager playerManager;

    public SearchTrackUseCase(AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public CompletableFuture<List<AudioTrack>> execute(String query, int limit) {
        CompletableFuture<List<AudioTrack>> future = new CompletableFuture<>();
        if (!query.startsWith("http")) {
            query = "ytsearch:" + query;
        }
        playerManager.loadItem(query, new TrackSearchHandler(future, limit));
        return future;
    }

}
