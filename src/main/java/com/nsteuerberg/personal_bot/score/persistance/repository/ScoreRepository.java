package com.nsteuerberg.personal_bot.score.persistance.repository;

import com.nsteuerberg.personal_bot.score.persistance.entity.ScoreEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    Page<ScoreEntity> findAllByGuildId(String guildId, Pageable pageable);

    Optional<ScoreEntity> findByGuildIdAndUserId(String guildId, String userId);
}
