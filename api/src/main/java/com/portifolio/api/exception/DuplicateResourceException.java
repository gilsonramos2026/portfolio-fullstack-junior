package com.portifolio.api.exception;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String message) {
        super(message);
    }
}
