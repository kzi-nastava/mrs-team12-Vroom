package org.example.vroom.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.vroom.DTOs.responses.LoginResponseDTO;
import org.example.vroom.entities.Admin;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.User;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.UserStatus;
import org.example.vroom.exceptions.AccountStatusException;
import org.example.vroom.exceptions.PasswordNotMatchException;
import org.example.vroom.exceptions.UserDoesntExistException;
import org.example.vroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(String email, String password, HttpServletResponse response) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new UserDoesntExistException("This email is not registered with any account");
        }

        if(user.get() instanceof RegisteredUser && (
                        ((RegisteredUser) user.get()).getUserStatus().equals(UserStatus.INACTIVE) ||
                        ((RegisteredUser) user.get()).getUserStatus().equals(UserStatus.BLOCKED)
            )
        )
            throw new AccountStatusException("This account is inactive or blocked");

        String userPassword = user.get().getPassword();
        if(!passwordEncoder.matches(password, userPassword)) {
            throw new PasswordNotMatchException("Passwords do not match");
        }

        String token = jwtService.generateToken(user.get());
        long expiresIn = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();

        String type = switch (user.get()) {
            case RegisteredUser u -> "registeredUser";
            case Driver d -> "driver";
            case Admin a -> "admin";
            default -> "unknown";
        };

        Cookie cookie = new Cookie("jwt", token);
        cookie.setMaxAge((int) expiresIn/1000);
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        return LoginResponseDTO.builder()
                .userID(user.get().getId())
                .type(type)
                .build();
    }

    public void logout(Long id, String type, HttpServletResponse response){
        Cookie cookie = new Cookie("jwt", null);
        cookie.setMaxAge(0);
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        if(!type.equals("driver")) return;

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user instanceof Driver driver){
            driver.setStatus(DriverStatus.INACTIVE);
            userRepository.save(driver);
        }
    }
}
