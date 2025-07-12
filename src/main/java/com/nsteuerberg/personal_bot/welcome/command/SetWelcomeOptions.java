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

import java.util.List;

@Component
@CustomName(CommandConstants.SET_WELCOME_OPTIONS)
public class SetWelcomeOptions implements IWelcomeCommand {
    @Autowired
    private WelcomeRepository welcomeRepository;
    Logger logger = LoggerFactory.getLogger(SetWelcomeOptions.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("Precisas de estar en un server para usar esto")
                    .setEphemeral(true)
                    .queue();
            logger.warn("{} ha intentado usar el comando {} fuera de un servidor", event.getUser().getName(), event.getInteraction().getName());
            return;
        }
        WelcomeEntity welcomeEntity = welcomeRepository.findById(event.getGuild().getId())
                .orElseGet(() -> WelcomeEntity.builder()
                        .guildId(event.getGuild().getId())
                        .addMemberCount(Boolean.FALSE)
                        .addDateEntry(Boolean.FALSE)
                        .messagesList(List.of())
                        .build()
        );
        OptionMapping welcomeChatOpt = event.getOption("welcome_chat");
        OptionMapping titleOpt = event.getOption("title");
        OptionMapping addDateOpt = event.getOption("add_date");
        OptionMapping addMemberCountOpt = event.getOption("add_member_count");
        OptionMapping ruleChatOpt = event.getOption("rule_chat");

        if (welcomeChatOpt == null && titleOpt == null && addDateOpt == null &&  addMemberCountOpt == null && ruleChatOpt == null){
            event.reply("Tienes que mandar al menos una opcion para cambiar los ajustes de bienvenidas").queue();
            logger.warn("El evento de SetWelcomeOptions se mando sin argumentos");
            return;
        }
        if (welcomeChatOpt != null) {
            welcomeEntity.setChatId(welcomeChatOpt.getAsChannel().asTextChannel().getId());
        }
        if (titleOpt != null) {
            welcomeEntity.setTitleMessage(titleOpt.getAsString());
        }
        if (addDateOpt != null) {
            welcomeEntity.setAddDateEntry(addDateOpt.getAsBoolean());
        }
        if (addMemberCountOpt != null) {
            welcomeEntity.setAddMemberCount(addMemberCountOpt.getAsBoolean());
        }
        if (ruleChatOpt != null) {
            welcomeEntity.setRuleChat(ruleChatOpt.getAsChannel().asTextChannel().getId());
        }
        welcomeRepository.save(welcomeEntity);
        logger.info("Se han cambiado exitosamente las opciones de bienvenida del servidor: {}", event.getGuild().getName());
        event.reply("Se han cambiado exitosamente las opciones de bienvenida")
                .setEphemeral(true)
                .queue();
    }
}
