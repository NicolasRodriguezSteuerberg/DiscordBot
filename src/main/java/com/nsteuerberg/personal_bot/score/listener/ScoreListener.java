package com.nsteuerberg.personal_bot.score.listener;

import com.nsteuerberg.personal_bot.score.service.implementation.ScoreServiceImpl;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ScoreListener extends ListenerAdapter {

    private final ScoreServiceImpl service;

    public ScoreListener(ScoreServiceImpl service) {
        this.service = service;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMember() == null) return;
        service.onUserMessage(event);
    }
}
