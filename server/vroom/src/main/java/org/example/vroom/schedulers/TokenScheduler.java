package org.example.vroom.schedulers;

import org.example.vroom.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenScheduler {
    @Autowired
    private TokenService tokenService;

    // cheking expired tokens every 3h
    @Scheduled(cron = "0 0 */3 * * ?")
    public void deleteExpiredTokens(){
        tokenService.deleteExpiredTokens();
    }
}
