package org.example.vroom.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.responses.auth.LoginResponseDTO;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.UserStatus;
import org.example.vroom.exceptions.auth.InvalidPasswordException;
import org.example.vroom.exceptions.auth.InvalidTokenException;
import org.example.vroom.exceptions.auth.TokenPresentException;
import org.example.vroom.exceptions.user.AccountStatusException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.TokenRepository;
import org.example.vroom.repositories.UserRepository;
import org.example.vroom.utils.EmailService;
import org.example.vroom.utils.JwtService;
import org.example.vroom.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private PasswordUtils passwordUtils;


    public LoginResponseDTO login(User user, HttpServletResponse response) {
        if(user instanceof RegisteredUser && (
                ((RegisteredUser) user).getUserStatus().equals(UserStatus.INACTIVE) ||
                        ((RegisteredUser) user).getUserStatus().equals(UserStatus.BLOCKED))
        )
            throw new AccountStatusException("This account is inactive or blocked");

        String token = jwtService.generateToken(user);
        long expiresIn = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();

        String type = switch (user) {
            case RegisteredUser u -> "REGISTERED_USER";
            case Driver d -> "DRIVER";
            case Admin a -> "ADMIN";
            default -> "unknown";
        };

        if(type.equals("DRIVER")){
            Driver d = (Driver) user;
            d.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(d);
        }
        return LoginResponseDTO.builder()
                .type(type)
                .token(token)
                .build();
    }

    public void logout(Long id, String type){
        if(!type.equals("DRIVER")) return;

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(user instanceof Driver driver){
            driver.setStatus(DriverStatus.INACTIVE);
            userRepository.save(driver);
        }
    }

    @Transactional
    public void forgotPassword(String email){
        Optional<Token> tokenOptional = tokenRepository.findByUserEmail(email);
        if(tokenOptional.isPresent() && !tokenOptional.get().isExpired()){
            throw new TokenPresentException("Token is present, please check spam if you cannot find email in primary inbox");
        }

        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new UserNotFoundException("User not found");


        String code = new Random().ints(12, 0, 36)
                .mapToObj(i -> Integer.toString(i, 36))
                .collect(Collectors.joining(""))
                .toUpperCase();

        Token token = Token.builder()
                .user(user.get())
                .code(code)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        tokenRepository.save(token);

        try {
            emailService.sendTokenMail(user.get().getEmail(), code);
        } catch (Exception e) {
            tokenRepository.delete(token);
            throw new RuntimeException("We encountered an issue sending the email. Please try again later");
        }
    }

    public void resetPassword(String email, String code, String password){
        Optional<Token> tokenOptional = tokenRepository.findByUserEmail(email);
        if(tokenOptional.isEmpty())
            throw new InvalidTokenException("Invalid or expired token");

        if(!passwordUtils.isPasswordValid(password))
            throw new InvalidPasswordException("Password doesn't match criteria");

        Token token = tokenOptional.get();
        if(!token.getCode().equals(code))
            throw new InvalidTokenException("Invalid or expired token");

        if(token.isExpired())
            throw new InvalidTokenException("Invalid or expired token");

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.saveAndFlush(user);

        tokenRepository.delete(token);
    }
}
