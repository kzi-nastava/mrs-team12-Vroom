package org.example.vroom.exceptions.ride;

public class TooManyPassengersException extends RuntimeException {
    public TooManyPassengersException(String message) {
        super(message);
    }
}
