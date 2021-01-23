package com.bilyoner.assignment.balanceapi.exception;

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
    private final String detailedMessage;
}
