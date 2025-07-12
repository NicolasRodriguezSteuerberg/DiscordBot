package com.nsteuerberg.personal_bot.goodbye.listener;

import com.nsteuerberg.personal_bot.commands.interfaces.IGoodbyeCommand;
import com.nsteuerberg.personal_bot.goodbye.service.implementations.GoodbyeServiceImpl;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoodbyeListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GoodbyeListener.class);

    private final Map<String, IGoodbyeCommand> commandHashMap;
    private final GoodbyeServiceImpl goodbyeService;

    public GoodbyeListener(final Map<String, IGoodbyeCommand> commandHashMap, GoodbyeServiceImpl goodbyeService) {
        this.commandHashMap = commandHashMap;
        this.goodbyeService = goodbyeService;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        IGoodbyeCommand command = commandHashMap.get(event.getName());
        if (command == null) return;
        logger.info("OnSlashCommandInteraction: ejecutando evento...");
        command.execute(event);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        logger.info("OnGuildMemberRemove:: se ha eliminado un usuario en la guild: {}", event.getGuild().getName());
        goodbyeService.onMemberLeft(event);
    }
}
