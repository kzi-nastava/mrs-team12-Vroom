package org.example.vroom.mappers;

import org.example.vroom.DTOs.AdminDTO;
import org.example.vroom.entities.Admin;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMapper {

    public AdminDTO toDTO(Admin admin) {
        if (admin == null)
            return null;

        return AdminDTO.builder()
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .email(admin.getEmail())
                .gender(admin.getGender())
                .phoneNumber(admin.getPhoneNumber())
                .address(admin.getAddress())
                .profilePhoto(admin.getProfilePhoto())
                .blockedReason(admin.getBlockedReason())
                .build();
    }

    public Admin toEntity(AdminDTO admin, String password) {
        if (admin == null)
            return null;

        return Admin.builder()
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .email(admin.getEmail())
                .gender(admin.getGender())
                .phoneNumber(admin.getPhoneNumber())
                .address(admin.getAddress())
                .profilePhoto(admin.getProfilePhoto())
                .blockedReason(admin.getBlockedReason())
                .build();
    }

    public List<AdminDTO> toDTOList(List<Admin> admins) {
        if (admins == null)
            return null;

        return admins.stream()
                .map(this::toDTO)
                .toList();
    }
}

