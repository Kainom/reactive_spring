package com.kainom.reactive.err;

public class EmailUniquessException extends RuntimeException {
    public EmailUniquessException() {
        super("Email already exists");
    }
    
}
