package org.example.vroom.mappers;

import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.DTOs.requests.auth.RegisterRequestDTO;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RegisteredUserMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisteredUser createUser(RegisterRequestDTO user) throws IOException {
        byte[] photoBytes = null;
        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            photoBytes = user.getProfilePhoto().getBytes();
        }

        return RegisteredUser.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .password(passwordEncoder.encode(user.getPassword()))
                .address(user.getAddress())
                .gender(user.getGender())
                .profilePhoto(photoBytes)
                .build();
    }

    public RegisteredUserDTO toDTO(RegisteredUser user) {
        if(user == null)
            return null;

        return RegisteredUserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .profilePhoto(user.getProfilePhoto())
                .blockedReason(user.getBlockedReason())
                .status(user.getUserStatus())
                .build();
    }

    public RegisteredUser toEntity(RegisteredUserDTO user, String password) {
        if(user == null)
            return null;

        return RegisteredUser.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .profilePhoto(user.getProfilePhoto())
                .blockedReason(user.getBlockedReason())
                .userStatus(user.getStatus())
                .build();
    }

    public List<RegisteredUserDTO> toDTOList(List<RegisteredUser> users){
        if(users == null)
            return null;

        return users.stream()
                .map(this::toDTO)
                .toList();
    }
}
