package com.aiinterviewcoach.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("The email or password is incorrect.");
    }
}

