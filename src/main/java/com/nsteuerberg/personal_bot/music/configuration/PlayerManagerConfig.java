package com.nsteuerberg.personal_bot.music.configuration;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerManagerConfig {
    @Bean
    public AudioPlayerManager audioPlayerManager() {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager youtubeManager = new YoutubeAudioSourceManager();
        playerManager.registerSourceManager(youtubeManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        // excluimos el youtube source manager por defecto de lavaplayer
        AudioSourceManagers.registerRemoteSources(playerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
        return playerManager;
    }
}
