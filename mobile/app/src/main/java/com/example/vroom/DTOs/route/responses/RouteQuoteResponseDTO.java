package com.example.vroom.DTOs.route.responses;

public class RouteQuoteResponseDTO {
    private double price;
    private double time;

    public RouteQuoteResponseDTO(double time, double price) {
        this.time = time;
        this.price = price;
    }

    public RouteQuoteResponseDTO() {
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
