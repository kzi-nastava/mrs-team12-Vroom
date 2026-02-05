package org.example.vroom.repositories;

import org.example.vroom.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("delete from Token t where t.expiresAt < :expiresAt")
    void deleteByExpiresAt(@Param("expiresAt") LocalDateTime expiresAt);
}
