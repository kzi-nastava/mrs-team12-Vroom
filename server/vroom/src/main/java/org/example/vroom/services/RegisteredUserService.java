package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.DTOs.requests.auth.RegisterRequestDTO;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.enums.UserStatus;
import org.example.vroom.exceptions.auth.InvalidPasswordException;
import org.example.vroom.exceptions.registered_user.ActivationExpiredException;
import org.example.vroom.exceptions.user.UserAlreadyExistsException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.RegisteredUserMapper;
import org.example.vroom.mappers.RegisteredUserProfileMapper;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.UserRepository;
import org.example.vroom.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RegisteredUserProfileMapper registeredUserProfileMapper;

    private boolean isPasswordValid(String pass){
        if(pass == null || pass.isEmpty() ||
                pass.length() < 8 || !pass.matches(".*[0-9].*") ||
                !pass.matches(".*[a-z].*") || !pass.matches(".*[A-Z].*"))
            return false;

        return true;
    }

    @Transactional
    public void createUser(RegisterRequestDTO req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new UserAlreadyExistsException("User with this email already exists");

        if(!isPasswordValid(req.getPassword())) throw new InvalidPasswordException("Password doesn't match criteria");

        req.setPassword(passwordEncoder.encode(req.getPassword()));
        RegisteredUser user = registeredUserMapper.createUser(req);
        user.setUserStatus(UserStatus.INACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        user = registeredUserRepository.saveAndFlush(user);

        String userEmail = req.getEmail();
        String id = Long.toString(user.getId());

        try {
            emailService.sendActivationMail(user.getEmail(), String.valueOf(user.getId()));
        } catch (Exception e) {
            throw new RuntimeException("User created but email failed to send");
        }
    }

    @Transactional
    public void activateUser(Long id) {
        Optional<RegisteredUser> userOptional = registeredUserRepository.findById(id);
        if(userOptional.isEmpty())
            throw new UserNotFoundException("User not found");

        RegisteredUser user = userOptional.get();

        if(LocalDateTime.now().isAfter(user.getCreatedAt().plusDays(1)))
            throw new ActivationExpiredException("The activation link has expired (valid 24h)");

        user.setUserStatus(UserStatus.ACTIVE);
        registeredUserRepository.save(user);
    }

    public RegisteredUserDTO getMyProfile(String email) {

        RegisteredUser user = registeredUserRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
        return registeredUserProfileMapper.toDTO(user);
    }

    @Transactional
    public RegisteredUserDTO updateMyProfile(
            String email,
            RegisteredUserDTO dto
    ) {
        RegisteredUser user = registeredUserRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        registeredUserProfileMapper.updateEntity(user, dto);

        return registeredUserProfileMapper.toDTO(
                registeredUserRepository.save(user)
        );
    }
}
