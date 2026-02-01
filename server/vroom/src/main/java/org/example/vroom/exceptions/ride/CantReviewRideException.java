package org.example.vroom.exceptions.ride;

public class CantReviewRideException extends RuntimeException {
    public CantReviewRideException(String message) {
        super(message);
    }
}
