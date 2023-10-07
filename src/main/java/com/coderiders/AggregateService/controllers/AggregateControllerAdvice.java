package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.commonutils.exceptions.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RestControllerAdvice
public class AggregateControllerAdvice {

    @ExceptionHandler(AggregateException.class)
    private ResponseEntity<ErrorResponse> aggregateExceptionHandler(AggregateException ex) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        System.out.println(Arrays.toString(ex.getStackTrace()));
        logException(ex, "AggregateException");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorId("ISE");
        errorResponse.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());


        logException(ex, "RuntimeException");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse();

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
            builder.append("\nFile Name: ").append(ele.getFileName());
            builder.append("\nLine Number: ").append(ele.getLineNumber());

            log.error(builder.toString());
        }
    }
}




