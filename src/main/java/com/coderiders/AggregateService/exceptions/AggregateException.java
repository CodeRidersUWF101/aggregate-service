package com.coderiders.AggregateService.exceptions;

public class AggregateException extends RuntimeException {

    public AggregateException() {
        super();
    }

    public AggregateException(String message) {
        super(message);
    }

    public AggregateException(Throwable cause) {
        super(cause);
    }

    public AggregateException(String message, Throwable cause) {
        super(message, cause);
    }
}
