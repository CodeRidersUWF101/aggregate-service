package com.coderiders.AggregateService.exceptions;

import lombok.Data;

import java.util.List;

@Data
public class AggregateErrorResponse {
    private int errorCode;
    private String errorId;
    private String errorMessage;
    private List<ErrorObj> additionalErrors;
}
