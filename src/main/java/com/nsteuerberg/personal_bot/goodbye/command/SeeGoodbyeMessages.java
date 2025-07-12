package com.nsteuerberg.personal_bot.goodbye.command;

import com.nsteuerberg.personal_bot.commands.interfaces.IGoodbyeCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeEntity;
import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeMessagesEntity;
import com.nsteuerberg.personal_bot.goodbye.persistance.repository.IGoodbyeRepository;
import com.nsteuerberg.personal_bot.goodbye.service.implementations.GoodbyeServiceImpl;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@CustomName(CommandConstants.SEE_GOODBYE_MESSAGES)
public class SeeGoodbyeMessages implements IGoodbyeCommand {
    private static final Logger logger = LoggerFactory.getLogger(SeeGoodbyeMessages.class);

    @Autowired
    private IGoodbyeRepository goodbyeRepository;
    @Autowired
    private GoodbyeServiceImpl goodbyeService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("No se puede hacer este comando fuera de un servidor").queue();
            logger.warn("Alguien ha intentado acceder a este comando fuera de un servidor");
            return;
        }
        try {
            List<GoodbyeMessagesEntity> messageList = goodbyeService.getGoodbyeMessageList(event.getGuild().getId());
            if (messageList.isEmpty()) {
                event.reply("No hay mensajes guardados").setEphemeral(true).queue();
                logger.info("No tiene mensajes guardados");
                return;
            }

            MessageEmbed embed = goodbyeService.getEmbedGoodbyeMessage(event.getMember(), messageList, 0);
            ActionRow actionRow = goodbyeService.getNavigationWelcomeMessage(event.getMember(), 0, messageList.size());

            event.replyEmbeds(embed)
                    .addActionRow(actionRow.getComponents())
                    .setEphemeral(true)
                    .queue();
        } catch (NoSuchElementException e) {
            event.reply("No se pueden mirar los mensajes, debes activar los ajustes de despedida para poder hacerlo").queue();
            logger.warn("Se intentó mirar mensajes de una server que no existen los datos de configuracion");
            return;
        }
    }
}
