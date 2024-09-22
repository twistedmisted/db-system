package ua.zxz.multydbsysytem.web.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.zxz.multydbsysytem.dto.ErrorResponse;
import ua.zxz.multydbsysytem.exception.WrongDataException;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handle(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({DataAccessException.class})
    public ResponseEntity<Object> handle(DataAccessException ex) {
        return new ResponseEntity<>(createErrorResponse(ex.getCause().getMessage(), false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({WrongDataException.class})
    public ResponseEntity<Object> handle(WrongDataException ex) {
        return new ResponseEntity<>(createErrorResponse(ex.getMessage(), true), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),  true), HttpStatus.UNAUTHORIZED);
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
            return new ResponseEntity<>(createErrorResponse(field + ": " + message, true), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createErrorResponse("Arguments not valid", true), HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createErrorResponse(String message, boolean notification) {
        return new ErrorResponse(message, notification);
    }
}
