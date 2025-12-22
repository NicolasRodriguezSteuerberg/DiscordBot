package com.nsteuerberg.personal_bot.score.service.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IScoreService {
    void onUserMessage(MessageReceivedEvent event);
    void addTextExp(String userId, String guildId, int xpIncrement);
    void onVoiceScheduled();
    void addVoiceExp(String userId, String guildId);
}
