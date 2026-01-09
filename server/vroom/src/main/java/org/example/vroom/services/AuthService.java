package org.example.vroom.services;

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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    public LoginResponseDTO login(String email, String password) {
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
        if(!password.equals(userPassword)) {
            throw new PasswordNotMatchException("Passwords do not match");
        }
        String token = jwtService.generateToken(user.get());
        long expiresIn = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();

        String type="";
        if(user.get() instanceof RegisteredUser){
            type="registeredUser";
        }else if(user.get() instanceof Driver){
            type="driver";
        }else if(user.get() instanceof Admin){
            type="admin";
        }

        return LoginResponseDTO.builder()
                .userID(user.get().getId())
                .type(type)
                .token(token)
                .expiresIn(expiresIn)
                .build();
    }

    public void logout(Long id, String type){
        if(!type.equals("driver")) return;

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user instanceof Driver driver){
            driver.setStatus(DriverStatus.INACTIVE);
            userRepository.save(driver);
        }
    }
}
