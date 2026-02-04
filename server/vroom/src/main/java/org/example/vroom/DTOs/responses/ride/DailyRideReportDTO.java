package org.example.vroom.DTOs.responses.ride;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
@Getter
@Setter
public class DailyRideReportDTO {

    private java.time.LocalDate date;
    private long rideCount;
    private double money;
    private double km;

    public DailyRideReportDTO(
            java.time.LocalDate date,
            long rideCount,
            double money,
            double km
    ) {
        this.date = date;
        this.rideCount = rideCount;
        this.money = money;
        this.km=km;
    }
}
