package com.nsteuerberg.personal_bot.welcome.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "welcome_settings")
public class WelcomeEntity {
    @Id
    @Column(name = "guild_id")
    String guildId;
    @Column(name = "chat_id")
    String chatId;
    @Column(name = "add_member_count")
    Boolean addMemberCount;
    @Column(name = "add_date_entry")
    Boolean addDateEntry;
    @Column(name = "title_message")
    String titleMessage;
    @Column(name = "rule_chat")
    String ruleChat;
    @Column(name = "message_list", nullable = false)
    List<String> messagesList;

    public void addMessage(String message) {
        messagesList.add(message);
    }

    public void deleteMessage(int index) {
        messagesList.remove(index);
    }

    public void editMessage(String message, int index) {
        messagesList.remove(index);
        messagesList.add(index, message);
    }
}
