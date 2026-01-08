package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.RegisterRequestDTO;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.enums.UserStatus;
import org.example.vroom.mappers.RegisteredUserMapper;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;
    @Autowired
    private RegisteredUserMapper registeredUserMapper;
    @Autowired
    private EmailService emailService;

    public void createUser(RegisterRequestDTO req) {
        RegisteredUser user = registeredUserMapper.createUser(req);
        user = registeredUserRepository.saveAndFlush(user);

        String userEmail = req.getEmail();
        String id = Long.toString(user.getId());

        try {
            emailService.sendActivationMail(user.getEmail(), String.valueOf(user.getId()));
        } catch (Exception e) {
            throw new RuntimeException("User created but email failed to send.");
        }
    }

    public boolean activateUser(Long id) {
        return registeredUserRepository.findById(id).map(user -> {
            if(!user.getUserStatus().equals(UserStatus.INACTIVE))
                return false;

            user.setUserStatus(UserStatus.ACTIVE);
            registeredUserRepository.save(user);
            return true;
        }).orElse(false);
    }
}
