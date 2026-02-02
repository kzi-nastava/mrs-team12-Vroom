package org.example.vroom.mappers;

import org.example.vroom.DTOs.AdminDTO;
import org.example.vroom.DTOs.responses.AdminUserDTO;
import org.example.vroom.entities.Admin;
import org.example.vroom.entities.User;
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

    public AdminUserDTO toAdminDTO(User user) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setBlockedReason(user.getBlockedReason());
        dto.setBlocked(
                user.getBlockedReason() != null && !user.getBlockedReason().isBlank()
        );
        return dto;
    }
}

