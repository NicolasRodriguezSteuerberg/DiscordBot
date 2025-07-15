package com.nsteuerberg.personal_bot.score.persistance.repository;

import com.nsteuerberg.personal_bot.score.persistance.entity.ScoreEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE ScoreEntity e SET e.textExp = e.textExp + :value where e.guildId = :guildId and e.userId = :userId")
    int updateTextExp(@Param("value") int incValue, @Param("guildId") String guildId, @Param("userId") String userId);

    @Transactional
    @Modifying
    @Query("UPDATE ScoreEntity e SET e.voiceExp = e.voiceExp + :value where e.guildId = :guildId and e.userId = :userId")
    int updateVoiceExp(@Param("value") int incValue, @Param("guildId") String guildId, @Param("userId") String userId);
}
