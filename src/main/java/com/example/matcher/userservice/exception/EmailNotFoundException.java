package com.example.matcher.userservice.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(final String msg) {
        super(msg);
    }

}
