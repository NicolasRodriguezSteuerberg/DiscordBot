package com.nsteuerberg.personal_bot.welcome.service.implementations;

import com.nsteuerberg.personal_bot.welcome.persistance.entity.WelcomeEntity;
import com.nsteuerberg.personal_bot.welcome.persistance.repository.WelcomeRepository;
import com.nsteuerberg.personal_bot.welcome.service.interfaces.IWelcomeChatService;
import com.nsteuerberg.personal_bot.utils.constants.MyTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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
public class WelcomeChatServiceImpl implements IWelcomeChatService {

    private static Logger logger = LoggerFactory.getLogger(WelcomeChatServiceImpl.class);

    private final WelcomeRepository welcomeRepository;

    @Autowired
    public WelcomeChatServiceImpl(final WelcomeRepository welcomeRepository){
        this.welcomeRepository = welcomeRepository;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        WelcomeEntity welcomeEntity = WelcomeEntity.builder()
                .guildId(event.getGuild().getId())
                //.chatId(null)
                .addMemberCount(false)
                .addDateEntry(false)
                //.ruleChat(null)
                //.titleMessage(null)
                .messagesList(List.of())
                .build();
        welcomeRepository.save(welcomeEntity);
    }

    @Override
    public void onMemberJoin(GuildMemberJoinEvent event) {
        logger.info("onMemberJoin:: ejecutandose metodo de miembro recien unido");
        Guild guild = event.getGuild();
        WelcomeEntity welcomeEntity = welcomeRepository.findById(guild.getId()).orElseThrow();

        TextChannel textChannel = guild.getTextChannelById(welcomeEntity.getChatId());
        if (textChannel == null) {
            logger.info("No se encontro el canal de texto de la base de datos, enviando mensaje de aviso");
            textChannel = guild.getTextChannels().getFirst();
            textChannel.sendMessage(guild.getOwner().getAsMention() + " el canal de texto de la base de datos ya no existe, reconfiguralo por favor").queue();
            return;
        }
        textChannel
                .sendMessageEmbeds(
                    createWelcomeEmbed(welcomeEntity, guild, event.getMember())
                )
                .queue();
        logger.info("ON-MEMBER-JOIN:: Se envio el mensaje de bienvenida exitosamente");
    }

    private MessageEmbed createWelcomeEmbed(WelcomeEntity welcomeEntity, Guild guild, Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setThumbnail(member.getEffectiveAvatarUrl())
                .setColor(getRandomColor())
                .setTitle((welcomeEntity.getTitleMessage() != null) ? welcomeEntity.getTitleMessage(): "Bienvenido al servidor")
        ;

        if (welcomeEntity.getAddDateEntry()) embedBuilder.setTimestamp(MyTime.getNow());
        embedBuilder.setTimestamp(member.getTimeJoined());
        if (welcomeEntity.getMessagesList().isEmpty()) {
            embedBuilder.setDescription(String.format("%s se ha unido al servidor", member.getAsMention()));
        } else {
            int randNumber = ThreadLocalRandom.current().nextInt(welcomeEntity.getMessagesList().size());
            String message = welcomeEntity.getMessagesList().get(randNumber);
            if (message.contains("%u")){
                message = message.replace("%u", member.getAsMention());
            }
            embedBuilder.setDescription(message);
        }
        if (welcomeEntity.getAddMemberCount()) {
            embedBuilder.addField("Miembro nº: ", String.valueOf(guild.getMemberCount()), false);
        }
        if (welcomeEntity.getRuleChat() != null) {
            embedBuilder.addField("Reglas: ","<#" + welcomeEntity.getRuleChat() + ">" , false);
        }
        return embedBuilder.build();
    }

    private Color getRandomColor(){
        return new Color(
                ThreadLocalRandom.current().nextInt(256), // Componente R
                ThreadLocalRandom.current().nextInt(256), // Componente G
                ThreadLocalRandom.current().nextInt(256)  // Componente B
        );
    }

    public List<String> getWelcomeMessage(Guild guild) {
        return welcomeRepository.findById(guild.getId()).orElseThrow().getMessagesList();
    }

    public MessageEmbed getEmbedWelcomeMessage(Member member, List<String> messageList, int page) {
        String descriptionMessage = messageList.get(page);
        if (descriptionMessage.contains("%u")) {
            descriptionMessage = descriptionMessage.replace("%u", member.getAsMention());
        }
        return new EmbedBuilder()
                .setTitle(String.format("Mensaje de bienvenida %d/%d", page + 1, messageList.size()))
                .setDescription(descriptionMessage)
                .setColor(getRandomColor())
                .build();
    }

    public ActionRow getNavigationWelcomeMessages(Member member, int page, int totalPages) {
        Button prevButton = Button.secondary("prev_msg:" + (page-1), "⬅️ Anterior");
        if (page == 0) {
            prevButton = prevButton.asDisabled();
        }
        Button edit = Button.secondary("edit_msg:" + page, "✏️ Editar");
        Button remove = Button.danger("del_msg:" + page, "🗑️ Eliminar");
        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            edit = edit.asDisabled();
            remove = remove.asDisabled();
        }
        Button nextButton = Button.secondary("next_msg:" + (page+1), "➡️ Siguiente");
        if (page == totalPages-1) {
            nextButton = nextButton.asDisabled();
        }

        return ActionRow.of(prevButton, edit, remove, nextButton);
    }

    public void deleteWelcomeMessage(Guild guild, int page) {
        WelcomeEntity welcomeEntity = welcomeRepository.findById(guild.getId()).orElseThrow();
        welcomeEntity.deleteMessage(page);
        welcomeRepository.save(welcomeEntity);
    }

    public void editWelcomeMessage(Guild guild, String newContent, int page) {
        WelcomeEntity welcomeEntity = welcomeRepository.findById(guild.getId()).orElseThrow();
        System.out.println(welcomeEntity.getMessagesList().size());
        if (page < 0 || page >= welcomeEntity.getMessagesList().size()) {
            throw new IllegalArgumentException("No se puede editar una pagina que no existe, " + page + "/" + welcomeEntity.getMessagesList().size());
        }
        welcomeEntity.editMessage(newContent, page);
        welcomeRepository.save(welcomeEntity);
    }

}
