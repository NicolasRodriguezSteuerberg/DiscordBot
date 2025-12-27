package com.nsteuerberg.personal_bot.music.audio;

import com.nsteuerberg.personal_bot.music.audio.model.TrackSearchHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class TrackSearchService {
    private final AudioPlayerManager playerManager;

    public TrackSearchService(AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public CompletableFuture<List<AudioTrack>> getAudioTracks(String query, int limit) {
        CompletableFuture<List<AudioTrack>> future = new CompletableFuture<>();
        if (!query.startsWith("http")) {
            query = "ytsearch:" + query;
        }
        playerManager.loadItem(query, new TrackSearchHandler(future, limit));
        return future;
    }
}
