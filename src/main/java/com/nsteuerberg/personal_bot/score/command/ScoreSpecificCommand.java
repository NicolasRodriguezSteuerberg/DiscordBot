package com.nsteuerberg.personal_bot.score.command;

import com.nsteuerberg.personal_bot.commands.interfaces.IScoreCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.score.persistance.entity.ScoreEntity;
import com.nsteuerberg.personal_bot.score.persistance.repository.ScoreRepository;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@CustomName(CommandConstants.SCORE)
public class ScoreSpecificCommand implements IScoreCommand {
    Logger logger = LoggerFactory.getLogger(ScoreSpecificCommand.class);
    private final ScoreRepository scoreRepository;

    @Value("${score.base-level}")
    private long baseLevel;

    public ScoreSpecificCommand(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        logger.info("Tier list text -> guild {}, user {}", event.getGuild().getName(), event.getUser().getName());
        OptionMapping userOption = event.getOption("user");
        User user = userOption == null ? event.getUser() : userOption.getAsUser();
        Optional<ScoreEntity> scoreOptional = scoreRepository.findByGuildIdAndUserId(event.getGuild().getId(), user.getId());
        if (!scoreOptional.isPresent()) {
            event.reply("Usuario sin datos de puntuacion").queue();
            return;
        }

        ScoreEntity score = scoreOptional.get();

        event.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Score")
                        .setDescription("Puntuación de %s".formatted(user.getAsMention()))
                        .setThumbnail(user.getEffectiveAvatarUrl())
                        .addField("Text", progressBar(score.getTextExp()), false)
                        .addField("Voice", progressBar(score.getVoiceExp()), false)
                        .build()
        ).queue();

    }

    private String progressBar(Long xp) {
        int level = getLevel(xp);
        long currentLevelXp = xpForLevel(level);
        long nextLevelXp = xpForLevel(level + 1);
        double progress;
        if (xp == 0) {
            progress = 0.0;
        } else {
            progress = (double) (xp - currentLevelXp) / (nextLevelXp - currentLevelXp);
        }

        progress = Math.max(0.0, Math.min(progress, 1.0));

        int filled = (int) (progress * 10);
        return "%s -> %s".formatted(
                level,
                ":green_square:".repeat(filled) + ":black_large_square:".repeat(10 - filled)
        );
    }

    private int getLevel(long xp) {
        return (int) Math.floor(Math.sqrt(xp / (double) baseLevel));
    }

    private long xpForLevel(int level)  {
        return baseLevel * (long) level * level;
    }


}
