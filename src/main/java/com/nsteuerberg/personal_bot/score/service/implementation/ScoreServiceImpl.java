package com.nsteuerberg.personal_bot.score.service.implementation;

import com.nsteuerberg.personal_bot.score.persistance.entity.MessageEntity;
import com.nsteuerberg.personal_bot.score.persistance.entity.ScoreEntity;
import com.nsteuerberg.personal_bot.score.persistance.repository.ScoreRepository;
import com.nsteuerberg.personal_bot.score.service.interfaces.IScoreService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements IScoreService {
    @Value("${spam.message.history.duration.ms}")
    private long messageExpirationTimeMs;
    @Value("${spam.message.lenght.min}")
    private long messageMinLenght;
    @Value("${spam.message.counts.permitted}")
    private long messageCountPermitted;
    @Value("${spam.message.counts.repeated}")
    private long messageCountRepeatedPermitted;

    private final Map<String, Map<String, List<MessageEntity>>> guildUserMessageMap;
    private final ScoreRepository scoreRepository;
    private JDA jda;

    // ToDo agregar la experencia de manera random no siempre de 5 en 5

    private static final Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);

    public ScoreServiceImpl(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
        guildUserMessageMap = new HashMap<>();
    }

    @Autowired
    public void setJda(@Lazy JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onUserMessage(MessageReceivedEvent event) {
        logger.info("New message from {}", event.getMember().getEffectiveName());
        long now = System.currentTimeMillis();
        guildUserMessageMap.putIfAbsent(event.getGuild().getId(), new HashMap<>());
        Map<String, List<MessageEntity>> userMessages = guildUserMessageMap.get(event.getGuild().getId());

        userMessages.putIfAbsent(event.getMember().getId(), new ArrayList<>());
        List<MessageEntity> messages = userMessages.get(event.getMember().getId());

        messages.add(new MessageEntity(event.getMessage().getContentRaw(), now));

        messages.removeIf(entry -> now - entry.getTimestamp() > messageExpirationTimeMs);
        userMessages.put(event.getMember().getId(), messages);
        if (isSpam(messages)) {
            logger.warn("Message from {} is spam", event.getMember().getEffectiveName());
            removeTextExp(event.getMember().getId(), event.getGuild().getId());
            return;
        }
        if (messages.size() > messageCountPermitted) {
            logger.warn("To much messages from {}, we dont add points", event.getMember().getEffectiveName());
            return;
        }
        if (event.getMessage().getContentRaw().length() <= messageMinLenght) {
            logger.warn("Message from {} has not the mininum lenght", event.getMember().getEffectiveName());
            return;
        }

        addTextExp(event.getMember().getId(), event.getGuild().getId());
    }

    @Override
    public boolean isSpam(List<MessageEntity> messageEntities) {
        // Comprobamos que no este usando el mismo mensaje varias veces seguidasa
        Map<String, Long> messageCounts = messageEntities.stream()
                .map(MessageEntity::getMessage)
                .collect(Collectors.groupingBy(
                    messageContent -> messageContent,
                    Collectors.counting()
                ));
        long maxRepetitions = messageCounts.values().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return maxRepetitions > messageCountRepeatedPermitted;
    }

    @Override
    public void removeTextExp(String userId, String guildId) {
        scoreRepository.updateTextExp(-5, guildId, userId);
    }

    @Override
    public void addTextExp(String userId, String guildId) {
        logger.info("Adding points to {} in the {} guild", userId, guildId);
        int updated = scoreRepository.updateTextExp(5, guildId, userId);
        if (updated==0) {
            logger.info("No habia datos de ese usuario, creandolo...");
            createScoreEntity(userId, guildId, 5, 0);
        }
    }

    private void createScoreEntity(String userId, String guildId, long textExp, long voiceExp ) {
        ScoreEntity scoreEntity = ScoreEntity.builder()
                .userId(userId)
                .guildId(guildId)
                .textExp(textExp)
                .voiceExp(voiceExp)
                .build();
        scoreRepository.save(scoreEntity);
    }

    @Override
    @Scheduled(fixedRate = 60_000)
    public void onVoiceScheduled() {
        logger.info("Tarea scheduled para puntos de voz ejecutandose...");
        jda.getGuilds().stream()
                .flatMap(guild -> guild.getVoiceChannels().stream())
                .flatMap(voiceChannel -> voiceChannel.getMembers().stream())
                .filter(member -> !member.getUser().isBot())
                .filter(member -> {
                    GuildVoiceState state = member.getVoiceState();
                    return (
                        state != null && !state.isMuted() &&
                        !state.isSelfMuted() && !state.isDeafened()
                        && !state.isSelfDeafened()

                    );
                })
                .forEach(member -> {
                    addVoiceExp(member.getId(), member.getGuild().getId());
                });
    }

    @Scheduled(fixedRate = 60_000)
    public void removeOldTextData() {
        long now = System.currentTimeMillis();
        logger.info("Tarea scheduled para borrar datos locales de texto para spam");
        guildUserMessageMap.values().forEach(value -> {
            value.values().forEach(messageEntities -> {
                messageEntities.removeIf(message -> now - message.getTimestamp() > messageExpirationTimeMs);
            });
        });

        guildUserMessageMap.values().forEach(userMessages -> {
            userMessages.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        });

        guildUserMessageMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public void addVoiceExp(String userId, String guildId) {
        logger.info("Agregando puntos de voz al usuario: {} del server: {}", userId, guildId);
        int updated = scoreRepository.updateVoiceExp(5, guildId, userId);
        if (updated == 0) {
            createScoreEntity(userId, guildId, 0, 5);
        }
    }

}
