package com.nsteuerberg.personal_bot.score.persistance.entity;

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
@Table(name = "score")
public class ScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "guild_id", nullable = false)
    private String guildId;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "text_exp", nullable = false)
    private Long textExp;
    @Column(name = "voice_exp", nullable = false)
    private Long voiceExp;
}
