package com.aiinterviewcoach.exception;

public class InvalidInterviewConfigurationException extends RuntimeException {
    private final String field;

    public InvalidInterviewConfigurationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
