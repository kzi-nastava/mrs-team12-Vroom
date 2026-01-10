package org.example.vroom.repositories;

import org.example.vroom.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserEmail(String email);
}
