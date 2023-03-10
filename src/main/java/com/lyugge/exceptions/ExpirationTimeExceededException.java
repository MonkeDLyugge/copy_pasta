package com.lyugge.exceptions;

public class ExpirationTimeExceededException extends RuntimeException {
    public ExpirationTimeExceededException(String message) {
        super(message);
    }
}
