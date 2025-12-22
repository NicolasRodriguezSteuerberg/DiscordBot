package com.nsteuerberg.personal_bot.score.service.implementation;

import com.nsteuerberg.personal_bot.score.persistance.entity.MessageEntity;
import com.nsteuerberg.personal_bot.score.persistance.entity.ScoreEntity;
import com.nsteuerberg.personal_bot.score.persistance.repository.ScoreRepository;
import com.nsteuerberg.personal_bot.score.service.interfaces.IScoreService;
import com.nsteuerberg.personal_bot.utils.constants.ButtonIds;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScoreServiceImpl implements IScoreService {
    @Value("${score.message.min-length}")
    private int messageMinLength;
    @Value("${score.message.cooldown-sec}")
    private int cooldownMessage;
    @Value("${score.message.xp.min}")
    private int textXpMin;
    @Value("${score.message.xp.max}")
    private int textXpMax;


    private final Map<String, Map<String, Long>> guildUserMessageTiemout;
    private final ScoreRepository scoreRepository;
    private final ShowTierList showTierList;
    private JDA jda;
    private final int pageSize = 1;

    private static final Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);

    public ScoreServiceImpl(ScoreRepository scoreRepository, ShowTierList showTierList) {
        this.scoreRepository = scoreRepository;
        this.showTierList = showTierList;
        guildUserMessageTiemout = new ConcurrentHashMap<>();
    }

    @Autowired
    public void setJda(@Lazy JDA jda) {
        this.jda = jda;
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();
        if (buttonId == null) return;
        Integer page = null;
        Boolean isText = null;
        if (buttonId.startsWith(ButtonIds.TIER_LIST_TEXT_PREV.getBaseId())
            || buttonId.startsWith(ButtonIds.TIER_LIST_TEXT_NEXT.getBaseId())
        ) {
            page = Integer.parseInt(buttonId.substring(ButtonIds.TIER_LIST_TEXT_NEXT.getBaseId().length()));
            isText = true;
        } else if (buttonId.startsWith(ButtonIds.TIER_LIST_VOICE_PREV.getBaseId()) || buttonId.startsWith(ButtonIds.TIER_LIST_VOICE_NEXT.getBaseId())) {
            page = Integer.parseInt(buttonId.substring(ButtonIds.TIER_LIST_VOICE_NEXT.getBaseId().length()));
            isText = false;
        }
        if (page == null) return;
        Page<ScoreEntity> scorePage = showTierList.getScoreEntityPage(event.getGuild().getId(), page, pageSize, isText);
        MessageEmbed embed = showTierList.embed(scorePage.getContent(), page, pageSize);
        ActionRow actionRow = showTierList.getNavigationButtons(scorePage.getNumber(), scorePage.getTotalPages(), isText);
        event
                .editMessageEmbeds(embed)
                .setActionRow(actionRow.getComponents())
                .queue();
    }

    @Override
    public void onUserMessage(MessageReceivedEvent event) {
        logger.info("New message from {}", event.getMember().getEffectiveName());
        guildUserMessageTiemout.putIfAbsent(event.getGuild().getId(), new ConcurrentHashMap<>());
        Map<String, Long> userMessageTimeout = guildUserMessageTiemout.get(event.getGuild().getId());

        long now = System.currentTimeMillis();
        Long prevMsgTime =  userMessageTimeout.get(event.getMember().getId());

        userMessageTimeout.put(event.getMember().getId(), now);
        if (prevMsgTime != null && (now - prevMsgTime) < cooldownMessage * 1000) return;

        int lengthMessage = event.getMessage().getContentRaw().length();
        if (lengthMessage < messageMinLength) return;

        int xp = Math.min(Math.max(lengthMessage / 50, textXpMin), textXpMax);

        addTextExp(event.getMember().getId(), event.getGuild().getId(), xp);
    }

    @Override
    public void addTextExp(String userId, String guildId, int xpIncrement) {
        logger.info("Adding {} points to {} in the {} guild", xpIncrement, userId, guildId);
        int updated = scoreRepository.updateTextExp(xpIncrement, guildId, userId);
        if (updated==0) {
            logger.info("No habia datos de ese usuario, creandolo...");
            createScoreEntity(userId, guildId, xpIncrement, 0);
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

    @Override
    public void addVoiceExp(String userId, String guildId) {
        logger.info("Agregando puntos de voz al usuario: {} del server: {}", userId, guildId);
        int updated = scoreRepository.updateVoiceExp(1, guildId, userId);
        if (updated == 0) {
            createScoreEntity(userId, guildId, 0, 1);
        }
    }

    @Scheduled(fixedRate = 60_000)
    public void removeOldTextData() {
        logger.info(guildUserMessageTiemout.toString());
        long now = System.currentTimeMillis();
        logger.info("Tarea scheduled para borrar datos locales de texto para spam");
        guildUserMessageTiemout.values().forEach(userMaps ->
                userMaps.entrySet().removeIf(entry -> now - entry.getValue() > 10)
        );

        guildUserMessageTiemout.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        logger.info(guildUserMessageTiemout.toString());
    }

}
