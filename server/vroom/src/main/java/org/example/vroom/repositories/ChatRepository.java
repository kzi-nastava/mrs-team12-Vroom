package org.example.vroom.repositories;

import org.example.vroom.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByUserId(Long userId);
}
