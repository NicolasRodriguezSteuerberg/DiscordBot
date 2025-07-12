package com.nsteuerberg.personal_bot.welcome.command;

import com.nsteuerberg.personal_bot.commands.interfaces.IWelcomeCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.welcome.persistance.entity.WelcomeEntity;
import com.nsteuerberg.personal_bot.welcome.persistance.repository.WelcomeRepository;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@CustomName(CommandConstants.ADD_WELCOME_MESSAGE)
public class AddWelcomeMessage implements IWelcomeCommand {
    @Autowired
    private WelcomeRepository welcomeRepository;
    private static final Logger logger = LoggerFactory.getLogger(AddWelcomeMessage.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        logger.info("Ejecutandose commando...");
        try {
            OptionMapping optionMapping =  event.getOption(CommandConstants.ADD_WELCOME_MESSAGE.getOptions()[0].getName());
            WelcomeEntity welcomeEntity = welcomeRepository.findById(event.getGuild().getId()).orElseThrow();
            welcomeEntity.addMessage(optionMapping.getAsString());
            welcomeRepository.save(welcomeEntity);
            event.reply("Se agrego exitosamente el nuevo mensaje")
                    .setEphemeral(false)
                    .queue();
            logger.info("Se agrego el mensaje exitosamente");
        } catch (NoSuchElementException e) {
            event.reply("Tienes que configurar los mensajes de bienvenida antes de agregar un mensaje")
                    .setEphemeral(true)
                    .queue();
        } catch (NullPointerException e) {
            event.reply("Tienes que estar en una guild o agregar el mensaje")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
