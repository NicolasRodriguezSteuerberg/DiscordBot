package com.nsteuerberg.personal_bot.goodbye.persistance.repository;

import com.nsteuerberg.personal_bot.goodbye.persistance.entity.GoodbyeMessagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGoodbyeMessagesRepository extends JpaRepository<GoodbyeMessagesEntity, Long> {
}
