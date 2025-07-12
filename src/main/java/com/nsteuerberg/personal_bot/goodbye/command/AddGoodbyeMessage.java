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

import java.util.NoSuchElementException;

@Component
@CustomName(CommandConstants.ADD_GOODBYE_MESSAGE)
public class AddGoodbyeMessage implements IGoodbyeCommand {
    private static final Logger logger = LoggerFactory.getLogger(AddGoodbyeMessage.class);

    @Autowired
    private IGoodbyeRepository goodbyeRepository;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            logger.warn("Alguien intento usar el comando fuera de una guild");
            event.reply("No se puede usar este comando fuera de un servidor")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        OptionMapping messageOpt = event.getOption("message");
        if (messageOpt == null) {
            logger.warn("Alguien intento usar el comando sin mensaje");
            event.reply("Tienes que escribir un mensaje para moder usar este bot")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        try{
            GoodbyeEntity goodbyeEntity = goodbyeRepository.findById(event.getGuild().getId()).orElseThrow();
            goodbyeEntity.addGoodbyeMessage(messageOpt.getAsString());
            goodbyeRepository.save(goodbyeEntity);
            event.reply("Se agrego exitosamente el nuevo mensaje de despedidas")
                    .setEphemeral(true)
                    .queue();
        } catch (NoSuchElementException e) {
            logger.warn("No existen datos en nuestra base de datos para las despedidas del servidor: {}", event.getGuild().getName());
            event.reply("Para poder agregar mensajes de despedida primero tienes que configurar las despedidas").setEphemeral(true).queue();
            return;
        }
    }
}
