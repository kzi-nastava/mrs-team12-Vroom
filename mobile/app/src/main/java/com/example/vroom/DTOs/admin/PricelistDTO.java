package com.example.vroom.DTOs.admin;

public class PricelistDTO {

    private double priceStandard;
    private double priceLuxury;
    private double priceMinivan;

    public PricelistDTO() {
    }

    public PricelistDTO(double priceStandard, double priceLuxury, double priceMinivan) {
        this.priceStandard = priceStandard;
        this.priceLuxury = priceLuxury;
        this.priceMinivan = priceMinivan;
    }

    public double getPriceStandard() {
        return priceStandard;
    }

    public void setPriceStandard(double priceStandard) {
        this.priceStandard = priceStandard;
    }

    public double getPriceLuxury() {
        return priceLuxury;
    }

    public void setPriceLuxury(double priceLuxury) {
        this.priceLuxury = priceLuxury;
    }

    public double getPriceMinivan() {
        return priceMinivan;
    }

    public void setPriceMinivan(double priceMinivan) {
        this.priceMinivan = priceMinivan;
    }
}
