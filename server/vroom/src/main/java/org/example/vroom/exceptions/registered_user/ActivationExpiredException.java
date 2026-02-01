package org.example.vroom.exceptions.registered_user;

public class ActivationExpiredException extends RuntimeException {
    public ActivationExpiredException(String message) {
        super(message);
    }
}
