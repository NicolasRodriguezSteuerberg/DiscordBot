package com.nsteuerberg.personal_bot.score.service.interfaces;

import com.nsteuerberg.personal_bot.score.persistance.entity.MessageEntity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface IScoreService {
    void onUserMessage(MessageReceivedEvent event);

    boolean isSpam(List<MessageEntity> messageEntities);

    void removeTextExp(String userId, String guildId);

    void addTextExp(String userId, String guildId);

    void onVoiceScheduled();

    void addVoiceExp(String userId, String guildId);
}
