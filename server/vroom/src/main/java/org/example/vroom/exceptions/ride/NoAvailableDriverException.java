package org.example.vroom.exceptions.ride;

public class NoAvailableDriverException extends RuntimeException {
    public NoAvailableDriverException() {
        super("There are no drivers currently available.");
    }
}
