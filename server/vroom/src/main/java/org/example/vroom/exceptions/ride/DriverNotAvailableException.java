package org.example.vroom.exceptions.ride;

public class DriverNotAvailableException extends RuntimeException {
    public DriverNotAvailableException(String message) {
        super(message);
    }
}
