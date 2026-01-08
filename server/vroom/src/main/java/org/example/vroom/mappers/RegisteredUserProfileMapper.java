package org.example.vroom.mappers;

import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.entities.RegisteredUser;
import org.springframework.stereotype.Component;

@Component
public class RegisteredUserProfileMapper extends BaseProfileMapper {

    public RegisteredUserDTO toDTO(RegisteredUser user) {
        RegisteredUserDTO dto = RegisteredUserDTO.builder().build();
        mapBase(user, dto);
        dto.setStatus(user.getUserStatus());
        return dto;
    }

    public void updateEntity(RegisteredUser user, RegisteredUserDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setProfilePhoto(dto.getProfilePhoto());
    }
}


