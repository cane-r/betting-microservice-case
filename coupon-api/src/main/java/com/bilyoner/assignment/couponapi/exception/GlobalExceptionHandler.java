package com.bilyoner.assignment.couponapi.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.checkerframework.checker.nullness.Opt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.log4j.Log4j2;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponApiException.class)
    public ResponseEntity<ErrorDto> handleBalanceApiException(CouponApiException ex, HttpServletRequest request) {
        log.error("Exception : " + ex.getMessage());
        final ErrorCodeEnum errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorDto(errorCode,ex.getMessage()));
    }
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
	protected ResponseEntity<Map<String, String>> handleMethodArgumentExceptionErrorHandler(MethodArgumentNotValidException ex,WebRequest request) {
		log.error(ex.toString());
        Map<String, String> errorMap = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(e-> e.getField(),e-> e.getDefaultMessage()));
		return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
	}
    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex,WebRequest request) {
        log.error("Exception : " + ex);
        Set<ConstraintViolation<?>> set = ex.getConstraintViolations();
        Map<String, String> errorMap = set.stream().collect(Collectors.toMap(e-> e.getPropertyPath().toString(),e-> e.getMessage()));
        return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneralException(Exception ex,HttpServletRequest request) {
        log.error("Exception : " + ex);
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCodeEnum.getHttpStatus()).body(new ErrorDto(errorCodeEnum,ex.getMessage()));
    }
}