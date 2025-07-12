package com.nsteuerberg.personal_bot.goodbye.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "goodbye_settings")
public class GoodbyeEntity {
    @Id
    @Column(name = "guild_id")
    private String guildId;
    @Column(name = "chat_id", nullable = false)
    private String chatId;
    @Column(name = "title_message")
    private String titleMessage;
    @Column(name = "add_date", nullable = false)
    private boolean addDate;
    @Column(name = "add_member_count", nullable = false)
    private boolean addMemberCount;
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "goodbyeEntity",
            orphanRemoval = true
    )
    private List<GoodbyeMessagesEntity> messagesList = new ArrayList<>();

    public void addGoodbyeMessage(String message) {
        messagesList.add(
                GoodbyeMessagesEntity.builder()
                        .message(message)
                        .goodbyeEntity(this)
                    .build()
        );
    }

    public void removeGoodbyeMessage(int index) {
        messagesList.remove(index).setGoodbyeEntity(null);
    }

    public void editGoodbyeMessage(int index, String newMessage) {
        messagesList.get(index).setMessage(newMessage);
    }
}
