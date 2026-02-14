package com.example.vroom.DTOs.ride.requests;


import java.util.List;

public class RideReportDTO {
    private List<DailyRideReportDTO> daily;
    private long totalRides;
    private double totalMoney;
    private double totalKilometers;
    private double avgMoneyPerRide;
    private double avgKilometersPerRide;

    public RideReportDTO() {
    }

    public RideReportDTO(List<DailyRideReportDTO> daily, long totalRides, double totalMoney, double totalKilometers) {
        this.daily = daily;
        this.totalRides = totalRides;
        this.totalMoney = totalMoney;
        this.totalKilometers = totalKilometers;
        this.avgMoneyPerRide = totalRides == 0 ? 0 : totalMoney / totalRides;
        this.avgKilometersPerRide = totalRides == 0 ? 0 : totalKilometers / totalRides;
    }

    // Getters and Setters
    public List<DailyRideReportDTO> getDaily() {
        return daily;
    }

    public void setDaily(List<DailyRideReportDTO> daily) {
        this.daily = daily;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getTotalKilometers() {
        return totalKilometers;
    }

    public void setTotalKilometers(double totalKilometers) {
        this.totalKilometers = totalKilometers;
    }

    public double getAvgMoneyPerRide() {
        return avgMoneyPerRide;
    }

    public void setAvgMoneyPerRide(double avgMoneyPerRide) {
        this.avgMoneyPerRide = avgMoneyPerRide;
    }

    public double getAvgKilometersPerRide() {
        return avgKilometersPerRide;
    }

    public void setAvgKilometersPerRide(double avgKilometersPerRide) {
        this.avgKilometersPerRide = avgKilometersPerRide;
    }
}
