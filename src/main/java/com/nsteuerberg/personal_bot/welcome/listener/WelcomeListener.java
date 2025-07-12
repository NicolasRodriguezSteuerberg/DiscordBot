package com.nsteuerberg.personal_bot.welcome.listener;

import com.nsteuerberg.personal_bot.commands.interfaces.IWelcomeCommand;
import com.nsteuerberg.personal_bot.welcome.service.implementations.WelcomeChatServiceImpl;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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
public class WelcomeListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeListener.class);

    private final Map<String, IWelcomeCommand> commandHashMap;
    private final WelcomeChatServiceImpl welcomeChatService;

    public WelcomeListener(final Map<String, IWelcomeCommand> commandHashMap, final WelcomeChatServiceImpl welcomeChatService){
        this.commandHashMap = commandHashMap;
        this.welcomeChatService = welcomeChatService;
        logger.info(commandHashMap.toString());
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        logger.info("GUILD_LISTENER:: El bot se acaba de unir al servidor: {} con id: {}", event.getGuild().getName(), event.getGuild().getId());
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        logger.info("GUILD_LISTENER:: Se acaba de unir: {} al server: {}", event.getMember().getEffectiveName(), event.getGuild().getName());
        try{
            welcomeChatService.onMemberJoin(event);
        } catch (NoSuchElementException e) {
            logger.warn("onGuildMemberJoin No esta configurado el server para usar GuildMemberJoin");
        } catch (Exception e) {
            logger.error("onGuildMemberJoin:: Error inexperado en evento de guildMemberJoin -> {}: {}", e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        IWelcomeCommand command = commandHashMap.get(event.getName());
        if (command==null) return;
        logger.info("onSlashCommandInteraction: se registro un comando valido para esta clase, ejecutandolo...");
        command.execute(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        logger.info("onButtonInteraction: se acaba de recibir un evento de boton");
        String buttonId = event.getButton().getId();
        try {
            if (buttonId.startsWith("prev_msg:") || buttonId.startsWith("next_msg:")) {
                logger.info("onButttonInteraction: se presiono un boton de paginacion");
                // prev_msg y next_msg miden lo mismo, asi que podemos hacerlo a la vez
                int page = Integer.parseInt(buttonId.substring("prev_msg:".length()));
                List<String> welcomeMessages =  welcomeChatService.getWelcomeMessage(event.getGuild());
                event.editMessageEmbeds(welcomeChatService.getEmbedWelcomeMessage(event.getMember(), welcomeMessages, page))
                        .setActionRow(welcomeChatService.getNavigationWelcomeMessages(event.getMember(), page, welcomeMessages.size()).getComponents())
                        .queue();
                logger.info("onButtonInteraction: se cambio de pagina exitosamente");
                return;
            } else if (buttonId.startsWith("edit_msg:")) {
                logger.info("onButtonInteracion: see presiono un boton de edicion de mensaje");
                int page = Integer.parseInt(buttonId.substring("edit_msg:".length()));
                String message = welcomeChatService.getWelcomeMessage(event.getGuild()).get(page);
                Modal modal = Modal.create("edit_msg:" + page, "Editar mensaje de bienvenida")
                        .addActionRow(
                                TextInput.create("new_content", "Nuevo contenido, '%u' para que se mencione", TextInputStyle.PARAGRAPH)
                                        .setValue(message)
                                        .setRequired(true)
                                        .build()
                        )
                        .build();
                event.replyModal(modal).queue();
                logger.info("onButtonInteraction: se abrio un modal para la edicion exitosamente");
                return;
            } else if (buttonId.startsWith("del_msg:")) {
                logger.info("onButtonInteraction: se presiono un mensaje de eliminacion de mensaje");
                int page = Integer.parseInt(buttonId.substring("del_msg:".length()));
                welcomeChatService.deleteWelcomeMessage(event.getGuild(), page);
                event.reply("Se eliminó exitosamente").setEphemeral(true).queue();
                logger.info("onButtonInteraction: Se elimino el mensaje exitosamente");
                return;
            }
            logger.info("onButtonInteraction: no se presiono ningun boton valido para este listener");
        } catch (NullPointerException | NoSuchElementException e) {
            logger.warn("OnButtonIteraction -> {}: {}", e.getClass().getName(), e.getMessage());
            event.reply("Hubo un error haciendo la accion...").setEphemeral(true).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        try {
            if (event.getModalId().startsWith("edit_msg:")) {
                int page = Integer.parseInt(event.getModalId().substring("edit_msg:".length()));
                String newContent = event.getValue("new_content").getAsString();
                welcomeChatService.editWelcomeMessage(event.getGuild(), newContent, page);
                event.reply("Mensaje editado exitosamente").setEphemeral(true).queue();
            }
        } catch (NoSuchElementException e) {
            logger.warn("onModalInteraction:: no hay informacion de bienvenida en nuestra base de datos");
            event.reply("No hay configuracion de bienvenida de vuestro server en nuestra base de datos")
                    .setEphemeral(true)
                    .queue();
        } catch (NullPointerException e) {
            logger.warn("onModalInteraction:: guild o newContent es nulo -> {}: {}", e.getClass().getName(), e.getMessage());
            event.reply("Tienes que estar en una guilid o agregar el contenido").setEphemeral(true).queue();
        }
    }
}
