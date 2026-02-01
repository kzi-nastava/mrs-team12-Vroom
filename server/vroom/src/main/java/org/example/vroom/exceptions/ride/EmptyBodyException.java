package org.example.vroom.exceptions.ride;

public class EmptyBodyException extends RuntimeException {
    public EmptyBodyException(String message) {
        super(message);
    }
}
