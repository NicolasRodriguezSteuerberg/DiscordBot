package com.nsteuerberg.personal_bot.goodbye.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "goodbye_messages")
public class GoodbyeMessagesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "message")
    String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id")
    private GoodbyeEntity goodbyeEntity;
}
