package org.example.vroom.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
