package org.example.vroom.DTOs.requests.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DriverUpdateRequestAdminDTO {

    private Long id;
    private Long driverId;
    private RequestStatus status;
    private LocalDateTime createdAt;

    private DriverDTO payload;
}

