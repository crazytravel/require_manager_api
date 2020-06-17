package cc.iteck.rm.controller;

import cc.iteck.rm.exception.AuthenticationException;
import cc.iteck.rm.exception.AuthorizationException;
import cc.iteck.rm.exception.ResourceNotFoundException;
import cc.iteck.rm.exception.ResourceOperateFailedException;
import cc.iteck.rm.model.ErrorWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers,
//                                                                  HttpStatus status,
//                                                                  WebRequest request) {
//        var restErrorEntity = ErrorWrapper.builder()
//                .error(HttpStatus.NOT_ACCEPTABLE)
//                .status(HttpStatus.NOT_ACCEPTABLE.value())
//                .message(ex.getBindingResult().getFieldErrors()
//                        .stream()
//                        .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
//                        .collect(Collectors.joining()))
//                .build();
//        return new ResponseEntity<>(restErrorEntity, restErrorEntity.getError());
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
//                                                             Object body,
//                                                             HttpHeaders headers,
//                                                             HttpStatus status,
//                                                             WebRequest request) {
//        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> ConstraintViolationExceptionHandler(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        List<String> msgList = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<?> cvl = iterator.next();
            msgList.add(cvl.getMessageTemplate());
        }
        logger.debug(msgList);

        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceOperateFailedException.class)
    public ResponseEntity<?> handleStorageException(ResourceOperateFailedException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorWrapper> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> handleAuthorizationException(AuthorizationException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }


    private ResponseEntity<ErrorWrapper> buildErrorResponse(Exception ex, HttpStatus httpStatus) {
        var errorWrapper = ErrorWrapper.builder()
                .code(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .errorDescription(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorWrapper, httpStatus);
    }
}
