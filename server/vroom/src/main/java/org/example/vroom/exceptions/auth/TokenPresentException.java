package org.example.vroom.exceptions.auth;

public class TokenPresentException extends RuntimeException{
    public TokenPresentException(String message){
        super(message);
    }
}
