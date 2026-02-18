package org.example.vroom.DTOs.responses.ride;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AcceptedRideDTO {
    String startAddress;
    String endAddress;
    String driverName;
    String vehicleInfo;
    String licensePlate;
}
