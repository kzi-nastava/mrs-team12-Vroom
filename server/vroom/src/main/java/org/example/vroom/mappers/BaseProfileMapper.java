package org.example.vroom.mappers;

import java.sql.Driver;

import org.apache.catalina.User;
import org.example.vroom.DTOs.UserDTO;
import org.example.vroom.entities.RegisteredUser;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseProfileMapper {

    protected void mapBase(RegisteredUser user, UserDTO dto) {
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setProfilePhoto(user.getProfilePhoto());
        dto.setBlockedReason(user.getBlockedReason());
    }

    protected void mapBase(Driver driver, UserDTO dto) {
        dto.setId(driver.getId());
        dto.setFirstName(driver.getFirstName());
        dto.setLastName(driver.getLastName());
        dto.setEmail(driver.getEmail());
        dto.setGender(driver.getGender());
        dto.setPhoneNumber(driver.getPhoneNumber());
        dto.setAddress(driver.getAddress());
        dto.setProfilePhoto(driver.getProfilePhoto());
        dto.setBlockedReason(driver.getBlockedReason());
    }
}

