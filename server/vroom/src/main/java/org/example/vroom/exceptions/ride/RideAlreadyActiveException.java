package org.example.vroom.exceptions.ride;

public class RideAlreadyActiveException extends RuntimeException {
    public RideAlreadyActiveException(String message) {
        super(message);
    }
}
