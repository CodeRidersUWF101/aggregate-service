package com.coderiders.AggregateService.exceptions;

public class AggregateException extends RuntimeException {

    private String additionalMessage;

    public AggregateException() {
        super();
    }

    public AggregateException(String message) {
        super(message);
    }

    public AggregateException(String message, String additionalMessage) {
        super(message);
        this.additionalMessage = additionalMessage;
    }

    public AggregateException(Throwable cause) {
        super(cause);
    }

    public AggregateException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getAdditionalMessage() {
        return this.additionalMessage;
    }
}
