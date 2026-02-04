package org.example.vroom.DTOs.responses.ride;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class RideReportDTO {

    private List<DailyRideReportDTO> daily;
    private long totalRides;
    private double totalMoney;
    private double totalKilometers;
    private double avgMoneyPerRide;
    private double avgKilometersPerRide;

    public RideReportDTO(
            List<DailyRideReportDTO> daily,
            long totalRides,
            double totalMoney,
            double totalKilometers
    ) {
        this.daily = daily;
        this.totalRides = totalRides;
        this.totalMoney = totalMoney;
        this.totalKilometers = totalKilometers;
        this.avgMoneyPerRide = totalRides == 0 ? 0 : totalMoney / totalRides;
        this.avgKilometersPerRide = totalRides == 0 ? 0 : totalKilometers / totalRides;
    }

}
