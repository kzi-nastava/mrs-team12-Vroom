package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    public void deleteExpiredTokens(){
        tokenRepository.deleteByExpiresAt(LocalDateTime.now());
    }
}
