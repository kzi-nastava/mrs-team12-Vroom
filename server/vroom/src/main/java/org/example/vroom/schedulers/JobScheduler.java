package org.example.vroom.schedulers;

import jakarta.transaction.Transactional;
import org.example.vroom.services.RegisteredUserService;
import org.example.vroom.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JobScheduler {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RegisteredUserService registeredUserService;

    // cheking expired tokens every 3h
    @Scheduled(cron = "0 0 */3 * * ?")
    public void deleteExpiredTokens(){
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);

        tokenService.deleteExpiredTokens(threshold);
        registeredUserService.deleteExpiredAccounts(threshold);
    }
}
