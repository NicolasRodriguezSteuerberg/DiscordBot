package com.nsteuerberg.personal_bot.score.command;

import com.nsteuerberg.personal_bot.commands.interfaces.IScoreCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.score.persistance.entity.ScoreEntity;
import com.nsteuerberg.personal_bot.score.service.implementation.ShowTierList;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@CustomName(CommandConstants.TIER_LIST_TEXT)
public class TierListTextCommand implements IScoreCommand {
    private static final Logger logger = LoggerFactory.getLogger(TierListTextCommand.class);
    @Value("${score.tier-list.size}")
    private int size;
    private final int page = 0;

    private final ShowTierList showTierList;

    public TierListTextCommand(ShowTierList showTierList) {
        this.showTierList = showTierList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        logger.info("Tier list text -> guild {}, user {}", event.getGuild().getName(), event.getUser().getName());
        Page<ScoreEntity> scorePage = showTierList.getScoreEntityPage(event.getGuild().getId(), page, size, true);
        MessageEmbed msg = showTierList.embed(scorePage.getContent(), page, size);
        ActionRow row = showTierList.getNavigationButtons(scorePage.getNumber(), scorePage.getTotalPages(), true);
        event.replyEmbeds(msg).addActionRow(row.getComponents()).queue();
    }
}
