package org.example.vroom.mappers;

import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.entities.RegisteredUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisteredUserMapper {
    public RegisteredUserDTO toDTO(RegisteredUser user) {
        if(user == null)
            return null;

        return new RegisteredUserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getProfilePhoto(),
                user.getBlockedReason(),
                user.getStatus()
        );
    }

    public RegisteredUser toEntity(RegisteredUserDTO user, String password) {
        if(user == null)
            return null;

        return RegisteredUser.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(password)
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
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
