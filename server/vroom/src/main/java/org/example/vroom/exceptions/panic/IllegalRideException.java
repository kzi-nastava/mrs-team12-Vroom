package org.example.vroom.exceptions.panic;

public class IllegalRideException extends RuntimeException{
    public IllegalRideException(String message){
        super(message);
    }
}
