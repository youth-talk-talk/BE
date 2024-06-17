package com.server.youthtalktalk.response.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e,
                                                                       HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                                               HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                               HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }
}
