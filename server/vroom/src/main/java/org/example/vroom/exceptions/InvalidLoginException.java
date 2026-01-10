package org.example.vroom.exceptions;

public class InvalidLoginException extends RuntimeException{
    public InvalidLoginException(String message){
        super(message);
    }
}
