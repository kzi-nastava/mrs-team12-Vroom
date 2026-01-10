package org.example.vroom.exceptions;

public class UserDoesntExistException extends RuntimeException{
    public UserDoesntExistException(String message) {
        super(message);
    }
}
