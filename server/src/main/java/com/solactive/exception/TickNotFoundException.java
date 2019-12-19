package com.solactive.exception;

public class TickNotFoundException extends RuntimeException {
    public TickNotFoundException(String message) {
        super(message);
    }
}