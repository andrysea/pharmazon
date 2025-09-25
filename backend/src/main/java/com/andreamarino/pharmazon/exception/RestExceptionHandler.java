package com.andreamarino.pharmazon.exception;

import java.util.NoSuchElementException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.andreamarino.pharmazon.exception.model.ErrorResponse;

@RestController
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{
    
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> exceptionGeneric(Exception e){
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorResponse> notFoundException(Exception e){
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateException.class)
    public final ResponseEntity<ErrorResponse> duplicateException(Exception e){
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public final ResponseEntity<ErrorResponse> noSuchElementException(RuntimeException e){
        ErrorResponse error = new ErrorResponse(HttpStatus.NO_CONTENT.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<ErrorResponse> authenticationException(RuntimeException e){
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Credenziali inserite, non corrette.");
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ErrorResponse> illegalArgumentException(RuntimeException e){
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public final ResponseEntity<ErrorResponse> illegalStateException(RuntimeException e){
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public final ResponseEntity<ErrorResponse> nullPointerException(RuntimeException e){
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MailException.class)
    public final ResponseEntity<ErrorResponse> mailException(RuntimeException e){
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = "Il corpo della richiesta è mancante o non è leggibile.";
        return new ResponseEntity<>(error, headers, status);
    }
}
