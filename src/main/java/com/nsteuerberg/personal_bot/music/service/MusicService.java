package com.nsteuerberg.personal_bot.music.service;

import com.nsteuerberg.personal_bot.music.service.audio.GuildMusicHandler;
import com.nsteuerberg.personal_bot.music.usecases.AutoCompleteSongUseCase;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MusicService {
    private final Map<String, GuildMusicHandler> musicHandlerMap;
    private final AutoCompleteSongUseCase autoCompleteSongUseCase;

    private final Logger logger = LoggerFactory.getLogger(MusicService.class);

    public MusicService(AutoCompleteSongUseCase autoCompleteSongUseCase) {
        this.autoCompleteSongUseCase = autoCompleteSongUseCase;
        this.musicHandlerMap = new ConcurrentHashMap<>();
    }

    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals(CommandConstants.PLAY.getName())){
            autoCompleteSongUseCase.execute(event);
        }
    }

    public GuildMusicHandler getMusicHandler(String guildId) {
        return musicHandlerMap.get(guildId);
    }

    public void addMusicHandler(String guildId, GuildMusicHandler musicHandler) {
        musicHandlerMap.put(guildId, musicHandler);
    }

    public void deleteMusicHandler(String guildId) {
        musicHandlerMap.remove(guildId);
        logger.info("Eliminado music handler de la guild {}", guildId);
    }
}
