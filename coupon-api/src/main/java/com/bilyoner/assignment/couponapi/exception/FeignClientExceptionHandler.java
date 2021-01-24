package com.bilyoner.assignment.couponapi.exception;

import org.springframework.stereotype.Component;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignClientExceptionHandler implements ErrorDecoder  {

    @Override
    public Exception decode(String methodKey, Response response) {
        // for now,simple http status check
        // later,pull message from http body
        
        switch(response.status()) {
            case 400:
            case 404:
            case 500:
                return new CouponApiException(ErrorCodeEnum.INTERNAL_SERVER_ERROR,"There was a network problem!");
            default:
                return new Exception("Unexpected http status code!");
            }
    }
    
}
