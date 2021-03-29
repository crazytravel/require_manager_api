/*
 * Copyright (c) 2019 Mercedes-Benz. All rights reserved.
 */
package cc.iteck.rm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(BusinessException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getErrorCode().getStatus().value(),
                ex.getErrorCode().name(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(ResponseCodeException.class)
    public final ResponseEntity<ExceptionResponse> handleAllRequestExceptions(ResponseCodeException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.resolve(ex.getResponseCode());
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getResponseCode(),
                httpStatus.name(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }
}
