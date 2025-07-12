package com.nsteuerberg.personal_bot.goodbye.service.interfaces;

import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeMessagesEntity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;

public interface IGoodbyeService {
    void onMemberLeft(GuildMemberRemoveEvent event);
    MessageEmbed getEmbedGoodbyeMessage(Member member, List<GoodbyeMessagesEntity> messageList, int page);
    ActionRow getNavigationWelcomeMessage(Member member, int page, int totalPages);
    void deleteWelcomeMessage(String guildId, int index);
    void editWelcomeMessage(String guildId, int index, String newContent);
}
