package ua.zxz.multydbsysytem.web.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handle(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
                                                               WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        if (Objects.nonNull(fieldError)) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            return new ResponseEntity<>(createErrorResponse(field + ": " + message), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createErrorResponse("Arguments not valid"), HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createErrorResponse(String message) {
        return new ErrorResponse(message);
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
    }
}
