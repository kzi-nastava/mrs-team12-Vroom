package org.example.vroom.DTOs.responses.ride;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class GetActiveRideInfoDTO {
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private String driverName;
    private String creatorName;
    private List<String> passengerEmails;

}
