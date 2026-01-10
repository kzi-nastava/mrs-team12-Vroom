package org.example.vroom.exceptions.user;

public class AccountStatusException extends RuntimeException{
    public AccountStatusException(String message){
        super(message);
    }
}
