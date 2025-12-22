package com.nsteuerberg.personal_bot.score.service.implementation;

import com.nsteuerberg.personal_bot.score.persistance.entity.ScoreEntity;
import com.nsteuerberg.personal_bot.score.persistance.repository.ScoreRepository;
import com.nsteuerberg.personal_bot.utils.constants.ButtonIds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nsteuerberg.personal_bot.utils.common.Colors.getRandomColor;

@Service
public class ShowTierList {
    private final ScoreRepository repository;

    public ShowTierList(ScoreRepository repository) {
        this.repository = repository;
    }

    public Page<ScoreEntity> getScoreEntityPage(String guildId, int page, int size, boolean isText) {
        return repository.findAllByGuildId(
                guildId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, isText ? "textExp" : "voiceExp"))
        );
    }

    public MessageEmbed embed(List<ScoreEntity> scores, int page, int size) {
        if (scores.isEmpty())
            return new EmbedBuilder().setTitle("Tier List").setDescription("There is not data from users").build();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Tier List -> Page nº %s".formatted(page + 1))
                .setColor(getRandomColor());

        int i = 1;
        String text = "";
        for (ScoreEntity score: scores) {
            text += "\n## %s <@%s>\n- Text: %s xp\n- Voice %s xp".formatted(
                    page * size + i,
                    score.getUserId(),
                    score.getTextExp(),
                    score.getVoiceExp()
            );
            i++;
        }
        embedBuilder.setDescription(text);
        return embedBuilder.build();
    }

    public ActionRow getNavigationButtons(int page, int totalPages, boolean isText) {
        String basePrevButton = isText
                ? ButtonIds.TIER_LIST_TEXT_PREV.getBaseId()
                : ButtonIds.TIER_LIST_VOICE_PREV.getBaseId();
        String baseNextButton = isText
                ? ButtonIds.TIER_LIST_TEXT_NEXT.getBaseId()
                : ButtonIds.TIER_LIST_VOICE_NEXT.getBaseId();
        Button prevButton = Button.secondary(
                basePrevButton + (page - 1),
                "⬅️Anterior"
        );
        if (page == 0) prevButton = prevButton.asDisabled();

        Button nextButton = Button.secondary(
                baseNextButton + (page + 1),
                "➡️Siguiente"
        );

        if (page == totalPages - 1) nextButton = nextButton.asDisabled();

        return ActionRow.of(prevButton, nextButton);
    }
}
