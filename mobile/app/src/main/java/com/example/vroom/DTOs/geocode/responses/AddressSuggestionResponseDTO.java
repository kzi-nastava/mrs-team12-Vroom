package com.example.vroom.DTOs.geocode.responses;

public class AddressSuggestionResponseDTO {
    private String label;
    private double lat;
    private double lon;

    public AddressSuggestionResponseDTO(String label, double lat, double lon) {
        this.label = label;
        this.lat = lat;
        this.lon = lon;
    }

    public AddressSuggestionResponseDTO() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
