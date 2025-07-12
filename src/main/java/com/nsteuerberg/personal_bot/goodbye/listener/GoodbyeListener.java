package com.nsteuerberg.personal_bot.goodbye.listener;

import com.nsteuerberg.personal_bot.commands.interfaces.IGoodbyeCommand;
import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeMessagesEntity;
import com.nsteuerberg.personal_bot.goodbye.service.implementations.GoodbyeServiceImpl;
import com.nsteuerberg.personal_bot.utils.constants.ButtonIds;
import com.nsteuerberg.personal_bot.utils.constants.ModalIds;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class GoodbyeListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GoodbyeListener.class);

    private final Map<String, IGoodbyeCommand> commandHashMap;
    private final GoodbyeServiceImpl goodbyeService;

    public GoodbyeListener(final Map<String, IGoodbyeCommand> commandHashMap, GoodbyeServiceImpl goodbyeService) {
        this.commandHashMap = commandHashMap;
        this.goodbyeService = goodbyeService;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        IGoodbyeCommand command = commandHashMap.get(event.getName());
        if (command == null) return;
        logger.info("OnSlashCommandInteraction: ejecutando evento...");
        command.execute(event);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        logger.info("OnGuildMemberRemove:: se ha eliminado un usuario en la guild: {}", event.getGuild().getName());
        goodbyeService.onMemberLeft(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();
        if (buttonId == null) return;
        try {
            if (buttonId.startsWith(ButtonIds.GOODBYE_PREV_MSG.getBaseId()) || buttonId.startsWith(ButtonIds.GOODBYE_NEXT_MSG.getBaseId())) {
                logger.info("onButtonInteraction: Boton de paginacion");
                int page = Integer.parseInt(buttonId.substring(ButtonIds.GOODBYE_NEXT_MSG.getBaseId().length()));
                List<GoodbyeMessagesEntity> messagesEntities = goodbyeService.getGoodbyeMessageList(event.getGuild().getId());
                event
                        .editMessageEmbeds(goodbyeService.getEmbedGoodbyeMessage(event.getMember(), messagesEntities, page))
                        .setActionRow(goodbyeService.getNavigationWelcomeMessage(event.getMember(), page, messagesEntities.size()).getComponents())
                        .queue();
                logger.info("onButtonInteraction: Se cambio de pagina exitosamente");
                return;
            } else if (buttonId.startsWith(ButtonIds.GOODBYE_EDIT_MSG.getBaseId())) {
                logger.info("onButtonInteraction: Boton de edicion pulsado");
                int page = Integer.parseInt(buttonId.substring(ButtonIds.GOODBYE_EDIT_MSG.getBaseId().length()));
                String message = goodbyeService.getGoodbyeMessageList(event.getGuild().getId()).get(page).getMessage();
                Modal modal = Modal.create(
                            ModalIds.GOODBYE_EDIT.getBaseId() + page,
                            "Editar mensaje de despedida"
                        )
                        .addActionRow(
                                TextInput.create(
                                        "new_content",
                                        "Nuevo contenido, '%u' para que se mencione",
                                        TextInputStyle.PARAGRAPH
                                )
                                        .setValue(message)
                                        .setRequired(true)
                                        .build()
                        )
                        .build();
                event.replyModal(modal).queue();
                logger.info("onButtonInteraction: se abrio un modal al usuario para la edicion del mensaje");
                return;
            } else if (buttonId.startsWith(ButtonIds.GOODBYE_DEL_MSG.getBaseId())) {
                logger.info("onButtonInteraction: se presionó el boton de borrado");
                int page = Integer.parseInt(buttonId.substring(ButtonIds.GOODBYE_DEL_MSG.getBaseId().length()));
                goodbyeService.deleteWelcomeMessage(event.getGuild().getId(), page);
                event.reply("Se eliminó el mensaje exitosamente").setEphemeral(true).queue();
                logger.info("onButtonInteraction: se borro el mensaje exitosamente");
                return;
            }

        } catch (NoSuchElementException e){
            logger.warn("No hay ajustes de despedida en nuestra base de datos -> {}: {}", e.getClass().getName(), e.getMessage());
            event.reply("No existen ajustes de despedida en nuestra base de datos").queue();
            return;

        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getGuild() == null) return;
        try {
            if (event.getModalId().startsWith(ModalIds.GOODBYE_EDIT.getBaseId())) {
                logger.info("onModalInteraction: Se ha recibido la edicion de un mensaje");
                int page = Integer.parseInt(event.getModalId().substring(ModalIds.GOODBYE_EDIT.getBaseId().length()));
                String newContent = event.getValue("new_content").getAsString();
                goodbyeService.editWelcomeMessage(event.getGuild().getId(), page, newContent);
                event.reply("Se modificó el mensaje exitosamente")
                        .setEphemeral(true)
                        .queue();
                logger.info("onModalInteraction: mensaje editado exitosamente");
                return;
            }
        } catch (NoSuchElementException e){
            event.reply("No hay informacion en nuestra base de datos de ajustes de mensajes de salida, no se puede editar el mensaje").queue();
            logger.warn("onModalInteraction: no existe la informacion de ajustes de mensajes de salida");
            return;
        }
    }
}
