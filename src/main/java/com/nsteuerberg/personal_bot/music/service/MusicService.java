package com.nsteuerberg.personal_bot.music.service;

import com.nsteuerberg.personal_bot.music.audio.model.GuildMusicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MusicService {
    private final Map<String, GuildMusicManager> musicManagerMap;

    private final Logger logger = LoggerFactory.getLogger(MusicService.class);

    public MusicService() {
        this.musicManagerMap = new ConcurrentHashMap<>();
    }

    public GuildMusicManager getMusicManager(String guildId) {
        return musicManagerMap.get(guildId);
    }

    public void addMusicHandler(String guildId, GuildMusicManager musicManager) {
        musicManagerMap.put(guildId, musicManager);
    }

    public void deleteMusicHandler(String guildId) {
        musicManagerMap.remove(guildId);
        logger.info("Eliminado music handler de la guild {}", guildId);
    }
}
