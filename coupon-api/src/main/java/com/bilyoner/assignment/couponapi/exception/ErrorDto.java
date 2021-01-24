package com.bilyoner.assignment.couponapi.exception;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorDto {
    @JsonUnwrapped
    private final ErrorCodeEnum errorCode;
    private String detailedMessage;
}
