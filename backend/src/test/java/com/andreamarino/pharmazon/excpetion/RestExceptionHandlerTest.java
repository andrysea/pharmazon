package com.andreamarino.pharmazon.excpetion;

import com.andreamarino.pharmazon.exception.RestExceptionHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.NoSuchElementException;

import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.WebRequest;
import org.mockito.InjectMocks;
import com.andreamarino.pharmazon.exception.model.ErrorResponse;

@ExtendWith(MockitoExtension.class)
public class RestExceptionHandlerTest {

    @InjectMocks
    private RestExceptionHandler restExceptionHandler;

    @Test
    public void exceptionGeneric_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.exceptionGeneric(new Exception("Test exception"));
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Test exception", response.getBody().getMessage());
    }

    @Test
    public void notFoundException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.notFoundException(new NotFoundException("Test exception"));
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Test exception", response.getBody().getMessage());
    }

    @Test
    public void duplicateException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.duplicateException(new DuplicateException("Test exception"));
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test exception", response.getBody().getMessage());
    }

    @Test
    public void noSuchElementException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.noSuchElementException(new NoSuchElementException());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void authenticationException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.authenticationException(new BadCredentialsException("Credenziali inserite, non corrette."));
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Credenziali inserite, non corrette.", response.getBody().getMessage());
    }

    @Test
    public void illegalArgumentException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.illegalArgumentException(new IllegalArgumentException("Test exception."));
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test exception.", response.getBody().getMessage());
    }

    @Test
    public void illegalStateException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.illegalStateException(new IllegalStateException("Test exception."));
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test exception.", response.getBody().getMessage());
    }

    @Test
    public void nullPointerException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.nullPointerException(new NullPointerException("Test exception."));
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Test exception.", response.getBody().getMessage());
    }

    @Test
    public void mailException_WhenValid_ReturnObject(){
        RestExceptionHandler handler = new RestExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.mailException(new NullPointerException("Test exception."));
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Test exception.", response.getBody().getMessage());
    }    
}
