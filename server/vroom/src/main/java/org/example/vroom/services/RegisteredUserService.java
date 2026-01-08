package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.RegisterRequestDTO;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.User;
import org.example.vroom.enums.UserStatus;
import org.example.vroom.exceptions.UserAlreadyExistsException;
import org.example.vroom.mappers.RegisteredUserMapper;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegisteredUserMapper registeredUserMapper;
    @Autowired
    private EmailService emailService;

    public void createUser(RegisterRequestDTO req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new UserAlreadyExistsException("User with this email already exists");

        RegisteredUser user = registeredUserMapper.createUser(req);

        user = registeredUserRepository.saveAndFlush(user);

        String userEmail = req.getEmail();
        String id = Long.toString(user.getId());

        try {
            emailService.sendActivationMail(user.getEmail(), String.valueOf(user.getId()));
        } catch (Exception e) {
            throw new RuntimeException("User created but email failed to send");
        }
    }

    public boolean activateUser(Long id) {
        return registeredUserRepository.activateUserById(id) > 0;
    }
}
