package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.RegisterRequestDTO;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.exceptions.user.UserAlreadyExistsException;
import org.example.vroom.mappers.RegisteredUserMapper;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(RegisterRequestDTO req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new UserAlreadyExistsException("User with this email already exists");

        req.setPassword(passwordEncoder.encode(req.getPassword()));
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
