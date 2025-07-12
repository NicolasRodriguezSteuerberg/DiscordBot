package com.nsteuerberg.personal_bot.goodbye.service.implementations;

import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeEntity;
import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeMessagesEntity;
import com.nsteuerberg.personal_bot.goodbye.persistance.repository.IGoodbyeRepository;
import com.nsteuerberg.personal_bot.goodbye.service.interfaces.IGoodbyeService;
import com.nsteuerberg.personal_bot.utils.constants.ButtonIds;
import com.nsteuerberg.personal_bot.utils.constants.MyTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GoodbyeServiceImpl implements IGoodbyeService {

    private static final Logger logger = LoggerFactory.getLogger(GoodbyeServiceImpl.class);
    @Autowired
    private IGoodbyeRepository goodbyeRepository;

    @Override
    public void onMemberLeft(GuildMemberRemoveEvent event) {
        logger.info("onMemberJoin:: ejecutandose metodo de miembro recien unido");
        Guild guild = event.getGuild();
        GoodbyeEntity goodbyeEntity = goodbyeRepository.findById(guild.getId()).orElseThrow();

        TextChannel textChannel = guild.getTextChannelById(goodbyeEntity.getChatId());
        if (textChannel == null) {
            logger.warn("No se encontro el canal de texto de la base de datos, enviando mensaje de aviso");
            textChannel = guild.getTextChannels().getFirst();
            textChannel.sendMessage(guild.getOwner().getAsMention() + " el canal de texto de las despedidas que hay en nuestra base de datos ya no existe, reconfiguralo por favor").queue();
            return;
        }

        textChannel
                .sendMessageEmbeds(
                        createGoodbyeEmbed(goodbyeEntity, guild, event.getUser())
                )
                .queue();
        logger.info("OnMemberLeft: Se envio el mensaje de despedida");
    }

    public List<GoodbyeMessagesEntity> getGoodbyeMessageList(String guildId) {
        return goodbyeRepository.findById(guildId).orElseThrow().getMessagesList();
    }

    @Override
    public MessageEmbed getEmbedGoodbyeMessage(Member member, List<GoodbyeMessagesEntity> messageList, int page) {
        String descriptionMessage = messageList.get(page).getMessage();
        if (descriptionMessage.contains("%u")) {
            descriptionMessage = descriptionMessage.replace("%u", member.getAsMention());
        }
        return new EmbedBuilder()
                .setTitle(String.format("Mensaje de bienvenida %d/%d", page+1, messageList.size()))
                .setDescription(descriptionMessage)
                .setColor(getRandomColor())
                .build();
    }

    @Override
    public ActionRow getNavigationWelcomeMessage(Member member, int page, int totalPages) {
        Button prevButton = Button.secondary(ButtonIds.GOODBYE_PREV_MSG.getBaseId() + (page-1), "⬅️ Anterior");
        if (page == 0) prevButton = prevButton.asDisabled();

        Button edit = Button.secondary(ButtonIds.GOODBYE_EDIT_MSG.getBaseId() + page, "✏️ Editar");
        Button remove = Button.danger(ButtonIds.GOODBYE_DEL_MSG.getBaseId() + page, "🗑️ Eliminar");
        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            edit = edit.asDisabled();
            remove = remove.asDisabled();
        }

        Button nextButton = Button.secondary(ButtonIds.GOODBYE_NEXT_MSG.getBaseId()+ (page + 1), "➡️ Siguiente");
        if (page == totalPages-1) nextButton = nextButton.asDisabled();

        return ActionRow.of(prevButton, edit, remove, nextButton);
    }

    @Override
    public void deleteWelcomeMessage(String guildId, int index) {
        GoodbyeEntity goodbyeEntity = goodbyeRepository.findById(guildId).orElseThrow();
        goodbyeEntity.removeGoodbyeMessage(index);
        goodbyeRepository.save(goodbyeEntity);
    }

    @Override
    public void editWelcomeMessage(String guildId, int index, String newContent) {
        GoodbyeEntity goodbyeEntity = goodbyeRepository.findById(guildId).orElseThrow();
        goodbyeEntity.editGoodbyeMessage(index, newContent);
        goodbyeRepository.save(goodbyeEntity);
    }

    private MessageEmbed createGoodbyeEmbed(GoodbyeEntity goodbyeEntity, Guild guild, User user) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setColor(getRandomColor())
                .setTitle((goodbyeEntity.getTitleMessage() != null) ? goodbyeEntity.getTitleMessage(): "Nos ha dejado")
            ;
        if (goodbyeEntity.isAddDate()) embedBuilder.setTimestamp(MyTime.getNow());
        if (goodbyeEntity.getMessagesList().isEmpty()) {
            embedBuilder.setDescription(
                    String.format("%s se ha ido del servidor", user.getAsMention())
            );
        } else {
            int randNumber = ThreadLocalRandom.current().nextInt(goodbyeEntity.getMessagesList().size());
            String message = goodbyeEntity.getMessagesList().get(randNumber).getMessage();
            if (message.contains("%u")) message = message.replace("%u", user.getAsMention());
            embedBuilder.setDescription(message);
        }
        if (goodbyeEntity.isAddMemberCount()) {
            embedBuilder.addField(
                    "Miembros restantes: ", String.valueOf(guild.getMemberCount()), true
            );
        }
        return  embedBuilder.build();
    }

    private Color getRandomColor(){
        return new Color(
                ThreadLocalRandom.current().nextInt(256), // Componente R
                ThreadLocalRandom.current().nextInt(256), // Componente G
                ThreadLocalRandom.current().nextInt(256)  // Componente B
        );
    }
}
