package com.nsteuerberg.personal_bot.welcome.command;

import com.nsteuerberg.personal_bot.commands.interfaces.IWelcomeCommand;
import com.nsteuerberg.personal_bot.configuration.CustomName;
import com.nsteuerberg.personal_bot.welcome.service.implementations.WelcomeChatServiceImpl;
import com.nsteuerberg.personal_bot.utils.constants.CommandConstants;
import net.dv8tion.jda.api.entities.Guild;
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
@CustomName(CommandConstants.SEE_WELCOME_MESSAGES)
public class SeeWelcomeMessages implements IWelcomeCommand {
    @Autowired
    private WelcomeChatServiceImpl welcomeChatService;
    private static Logger logger = LoggerFactory.getLogger(SeeWelcomeMessages.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            Guild guild = event.getGuild();
            if (guild == null) throw new NoSuchElementException("No esta en una guild");
            List<String> welcomeMessages = welcomeChatService.getWelcomeMessage(guild);

            if (welcomeMessages.isEmpty()) throw new NoSuchElementException("No tienes mensajes");
            MessageEmbed embed =  welcomeChatService.getEmbedWelcomeMessage(event.getMember(), welcomeMessages, 0);
            ActionRow actionRow = welcomeChatService.getNavigationWelcomeMessages(event.getMember(), 0, welcomeMessages.size());

            event.replyEmbeds(embed)
                    .addActionRow(actionRow.getComponents())
                    .setEphemeral(true)
                    .queue();
        } catch (NoSuchElementException e) {
            event.reply("No estas en una guild o no hay ninguna configuracion en nuestra base de datos")
                    .setEphemeral(true)
                    .queue();
            logger.warn("SeeWelcomeMessages:: no esta desde una guild o no existe en nuestra base de datos");
        }
    }
}
