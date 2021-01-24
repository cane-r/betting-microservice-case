package com.bilyoner.assignment.couponapi.exception;

import lombok.Getter;

@Getter
public class CouponApiException extends RuntimeException{
    private final ErrorCodeEnum errorCode;

    public CouponApiException(ErrorCodeEnum errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public CouponApiException(ErrorCodeEnum errorCode , String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
