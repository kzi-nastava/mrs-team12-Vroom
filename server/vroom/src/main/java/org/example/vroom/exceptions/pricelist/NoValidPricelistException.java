package org.example.vroom.exceptions.pricelist;

public class NoValidPricelistException extends RuntimeException {
    public NoValidPricelistException(String message) {
        super(message);
    }
}
