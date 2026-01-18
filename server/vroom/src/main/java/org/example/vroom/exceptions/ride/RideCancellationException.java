package org.example.vroom.exceptions.ride;

public class RideCancellationException extends RuntimeException{
    public RideCancellationException(String message) {
        super(message);
    }
}
