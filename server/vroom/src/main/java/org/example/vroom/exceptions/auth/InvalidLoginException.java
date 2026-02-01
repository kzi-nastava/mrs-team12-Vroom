package org.example.vroom.exceptions.auth;

public class InvalidLoginException extends RuntimeException{
    public InvalidLoginException(String message){
        super(message);
    }
}
