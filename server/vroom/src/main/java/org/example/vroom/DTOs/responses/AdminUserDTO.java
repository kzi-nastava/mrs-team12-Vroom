package org.example.vroom.DTOs.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserDTO {
    private Long id;
    private String email;
    private String blockedReason;
    private boolean blocked;
}

