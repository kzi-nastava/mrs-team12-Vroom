package org.example.vroom.DTOs;

import lombok.*;

import org.example.vroom.DTOs.responses.GetRouteDTO;
import org.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideDTO {

    private Long id;
    private RideStatus status;

    private DriverDTO driver;
    private GetRouteDTO route;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<String> passengers;
    private double price;
}