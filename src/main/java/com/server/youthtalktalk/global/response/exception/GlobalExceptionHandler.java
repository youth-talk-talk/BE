package com.server.youthtalktalk.global.response.exception;

import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
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
    public ResponseEntity<BaseResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        BaseResponseCode baseResponseCode = e.getBaseResponseCode();

        BaseResponse baseResponse = new BaseResponse(baseResponseCode);

        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponseCode.getStatus()));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<BaseResponse> handleEntityNotFoundException(EntityNotFoundException e,
                                                                      HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        BaseResponseCode baseResponseCode = e.getBaseResponseCode();

        BaseResponse baseResponse = new BaseResponse(baseResponseCode);

        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponseCode.getStatus()));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                                              HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        BaseResponseCode baseResponseCode = BaseResponseCode.INVALID_INPUT_VALUE;

        BaseResponse baseResponse = new BaseResponse(baseResponseCode);

        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponseCode.getStatus()));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        BaseResponseCode baseResponseCode = BaseResponseCode.INVALID_INPUT_VALUE;

        BaseResponse baseResponse = new BaseResponse(baseResponseCode);

        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponseCode.getStatus()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<BaseResponse> handleException(
            Exception e,
            HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        BaseResponseCode baseResponseCode = BaseResponseCode.INVALID_INPUT_VALUE;

        BaseResponse baseResponse = new BaseResponse(baseResponseCode);

        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponseCode.getStatus()));
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                              HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        BaseResponseCode baseResponseCode = BaseResponseCode.INVALID_INPUT_VALUE;

        BaseResponse baseResponse = new BaseResponse(baseResponseCode);

        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponseCode.getStatus()));
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {
        log.error("[ERROR] : {}", e.getMessage(), e);

        BaseResponseCode baseResponseCode = BaseResponseCode.METHOD_NOT_ALLOWED;

        BaseResponse baseResponse = new BaseResponse(baseResponseCode);

        return new ResponseEntity<>(baseResponse, HttpStatus.valueOf(baseResponseCode.getStatus()));
    }
}
