package org.example.vroom.exceptions.auth;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}
