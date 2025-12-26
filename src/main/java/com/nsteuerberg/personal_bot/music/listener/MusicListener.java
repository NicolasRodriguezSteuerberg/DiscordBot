package com.nsteuerberg.personal_bot.music.listener;

import com.nsteuerberg.personal_bot.commands.interfaces.IMusicCommand;
import com.nsteuerberg.personal_bot.music.service.MusicService;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MusicListener extends ListenerAdapter {
    private final MusicService musicService;
    private final Map<String, IMusicCommand> commandMap;

    public MusicListener(MusicService musicService, Map<String, IMusicCommand> commandMap) {
        this.musicService = musicService;
        this.commandMap = commandMap;
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        musicService.onCommandAutoCompleteInteraction(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        IMusicCommand command = commandMap.get(event.getName());
        if (command == null) return;
        command.execute(event);
    }


}
