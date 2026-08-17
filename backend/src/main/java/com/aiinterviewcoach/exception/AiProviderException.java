package com.aiinterviewcoach.exception;

import org.springframework.http.HttpStatus;

public class AiProviderException extends RuntimeException {
    private final HttpStatus status;
    private final boolean retryable;

    public AiProviderException(HttpStatus status, String message, boolean retryable) {
        super(message);
        this.status = status;
        this.retryable = retryable;
    }

    public AiProviderException(HttpStatus status, String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.retryable = retryable;
    }

    public HttpStatus getStatus() { return status; }
    public boolean isRetryable() { return retryable; }
}
