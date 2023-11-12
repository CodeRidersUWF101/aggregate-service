package com.coderiders.AggregateService.models.commonutils.exceptions;

import lombok.Builder;

@Builder
public class ErrorObj {
  private int errorCode;
  private String errorId;
  private String errorMessage;
}