package com.nsteuerberg.personal_bot.goodbye.command;

import com.nsteuerberg.personal_bot.commands.interfaces.IGoodbyeCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeEntity;
import com.nsteuerberg.personal_bot.goodbye.persistance.repository.IGoodbyeRepository;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CustomName(CommandConstants.SET_GOODBYE_OPTIONS)
public class SetGoodbyeOptions implements IGoodbyeCommand {

    private static final Logger logger = LoggerFactory.getLogger(SetGoodbyeOptions.class);
    @Autowired
    private IGoodbyeRepository goodbyeRepository;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        logger.info("se ejecuto el comando de opciones de despedida");
        if (event.getGuild() == null) {
            event.reply("Necesitas estar en un server para poder hacer este comando")
                    .setEphemeral(true)
                    .queue();
            logger.warn("{} ha intentado usar el comando {} fuera de un servidor", event.getUser().getName(), event.getInteraction().getName());
            return;
        }
        GoodbyeEntity goodbyeEntity = goodbyeRepository.findById(event.getGuild().getId())
                .orElseGet(() -> GoodbyeEntity.builder()
                        .guildId(event.getGuild().getId())
                        .addMemberCount(false)
                        .addDate(false)
                        .build()
                );
        OptionMapping goodbyeChatOpt = event.getOption("goodbye_chat");
        OptionMapping titleOpt = event.getOption("title");
        OptionMapping addDateOpt = event.getOption("add_date");
        OptionMapping addMemberCountOpt = event.getOption("add_member_count");

        if(goodbyeChatOpt == null && titleOpt == null && addDateOpt == null && addMemberCountOpt == null) {
            event.reply("Tienes que mandar alguno de los parametros para poder cambiar los ajustes").setEphemeral(true).queue();
            logger.warn("Han intentado cambiar los ajustes de despedida sin pasar algun parametro");
            return;
        }

        if (goodbyeChatOpt != null) {
            goodbyeEntity.setChatId(goodbyeChatOpt.getAsChannel().asTextChannel().getId());
            logger.info("modificando el chat de despedida");
        }

        if (titleOpt != null) {
            goodbyeEntity.setTitleMessage(titleOpt.getAsString());
            logger.info("modificando el titulo de despedida");
        }

        if (addDateOpt != null) {
            goodbyeEntity.setAddDate(addDateOpt.getAsBoolean());
            logger.info("modificando la fecha de despedida");
        }

        if (addMemberCountOpt != null) {
            goodbyeEntity.setAddMemberCount(addMemberCountOpt.getAsBoolean());
            logger.info("modificando el conteo de despedida");
        }

        if (goodbyeEntity.getChatId() == null) {
            logger.warn("El canal de despedida es nulo");
            event.reply("No habia canal de texto y tampoco lo has pasado por parametro.\nSe precisa que exista")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        goodbyeRepository.save(goodbyeEntity);
        logger.info("Se han cambiado los ajustes de despedida exitosamente");
        event.reply("Se han cambiado exitosamente los ajustes de despedida")
                .queue();
        return;
    }
}
