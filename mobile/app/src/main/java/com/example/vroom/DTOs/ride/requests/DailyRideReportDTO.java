package com.example.vroom.DTOs.ride.requests;

import java.time.LocalDate;

public class DailyRideReportDTO {
    private LocalDate date;
    private long rideCount;
    private double money;
    private double km;

    public DailyRideReportDTO() {
    }

    public DailyRideReportDTO(LocalDate date, long rideCount, double money, double km) {
        this.date = date;
        this.rideCount = rideCount;
        this.money = money;
        this.km = km;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getRideCount() {
        return rideCount;
    }

    public void setRideCount(long rideCount) {
        this.rideCount = rideCount;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }
}