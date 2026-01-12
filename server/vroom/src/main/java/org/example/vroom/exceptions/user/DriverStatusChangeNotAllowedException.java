package org.example.vroom.exceptions.user;

public class DriverStatusChangeNotAllowedException extends RuntimeException {
    public DriverStatusChangeNotAllowedException(String message) {
        super(message);
    }
}
