package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.exceptions.AggregateErrorResponse;
import com.coderiders.AggregateService.exceptions.AggregateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RestControllerAdvice
public class AggregateControllerAdvice {

    @Value("${app.logging}")
    private String loggingLevel;

    @ExceptionHandler(AggregateException.class)
    private ResponseEntity<AggregateErrorResponse> aggregateExceptionHandler(AggregateException ex) {
        AggregateErrorResponse errorResponse = new AggregateErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());


        logException(ex, "AggregateException");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<AggregateErrorResponse> runtimeExceptionHandler(RuntimeException ex) {
        AggregateErrorResponse errorResponse = new AggregateErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());


        logException(ex, "RuntimeException");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<AggregateErrorResponse> exceptionHandler(Exception ex) {
        AggregateErrorResponse errorResponse = new AggregateErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        logException(ex, "Exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logException(Exception ex, String exceptionType) {
        StringBuilder builder = new StringBuilder();
        builder.append(exceptionType).append(" occurred.");

        if (ex.getStackTrace().length > 0) {
            StackTraceElement ele = ex.getStackTrace()[0];
            builder.append("\nClass Name: ").append(ele.getClassName());
            builder.append("\nMethod Name: ").append(ele.getMethodName());

            if (shouldLogInDebug()) {
                builder.append("\nFile Name: ").append(ele.getFileName());
                builder.append("\nLine Number: ").append(ele.getLineNumber());
                log.debug(builder.toString());
            } else {
                log.error(builder.toString());
            }
        } else {
            logErrorOrDebug(builder.toString());
        }
    }

    private boolean shouldLogInDebug() {
        return loggingLevel.equalsIgnoreCase("INFO")
                || loggingLevel.equalsIgnoreCase("WARN")
                || loggingLevel.equalsIgnoreCase("ERROR");
    }

    private void logErrorOrDebug(String message) {
        if (shouldLogInDebug()) {
            log.debug(message);
        } else {
            log.error(message);
        }
    }
}




