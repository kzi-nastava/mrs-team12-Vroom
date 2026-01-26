package org.example.vroom.exceptions.user;

public class PendingRequestExistsException extends RuntimeException {
    public PendingRequestExistsException(String message) {
        super(message);
    }
}
