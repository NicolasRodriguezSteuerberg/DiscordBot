package com.nsteuerberg.personal_bot.welcome.persistance.repository;

import com.nsteuerberg.personal_bot.welcome.persistance.entity.WelcomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WelcomeRepository extends JpaRepository<WelcomeEntity, String> {
    @Query("SELECT welcome.messagesList FROM WelcomeEntity welcome WHERE welcome.guildId=:cond_value")
    Optional<List<String>> findMessagesList(@Param("cond_value") String guildId);
}
