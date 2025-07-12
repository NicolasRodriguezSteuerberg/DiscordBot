package com.nsteuerberg.personal_bot.goodbye.service.implementations;

import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeEntity;
import com.nsteuerberg.personal_bot.goodbye.persistance.repository.IGoodbyeRepository;
import com.nsteuerberg.personal_bot.goodbye.service.interfaces.IGoodbyeService;
import com.nsteuerberg.personal_bot.utils.constants.MyTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
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
