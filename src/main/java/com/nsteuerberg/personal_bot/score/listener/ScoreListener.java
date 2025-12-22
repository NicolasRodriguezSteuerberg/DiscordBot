package com.nsteuerberg.personal_bot.score.listener;

import com.nsteuerberg.personal_bot.commands.interfaces.IScoreCommand;
import com.nsteuerberg.personal_bot.score.service.implementation.ScoreServiceImpl;
import com.nsteuerberg.personal_bot.utils.constants.ButtonIds;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScoreListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ScoreListener.class);

    private final Map<String, IScoreCommand> commandMap;
    private final ScoreServiceImpl service;

    public ScoreListener(Map<String, IScoreCommand> commandMap, ScoreServiceImpl service) {
        this.commandMap = commandMap;
        this.service = service;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        IScoreCommand command = commandMap.get(event.getName());
        if (command==null) return;
        logger.info("onSlashCommandInteraction: se registro un comando valido para esta clase, ejecutandolo...");
        command.execute(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (event.getMember().getUser().isBot()) return;
        service.onUserMessage(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        service.onButtonInteraction(event);
    }

}
