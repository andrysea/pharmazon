package com.andreamarino.pharmazon.security.auth;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.security.user.Role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService service;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    public void register_WhenValidInput_ResponseCreated(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);

        //Mock
        when(service.register(any(UserDto.class))).thenReturn(userDto);

        //Test
        ResponseEntity<?> response = authenticationController.register(userDto);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registrazione dell'utente avvenuta con successo.", responseBody.get("message"));
        assertEquals(userDto, responseBody.get("userDto"));
        verify(service, times(1)).register(userDto);
    }

    @Test
    public void authenticate_WhenValidInput_ResponseOk(){
        //Setup
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("andrysea", "password");
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("token");

        //Mock
        when(service.authenticate(any(AuthenticationRequest.class))).thenReturn(authenticationResponse);

        //Test
        ResponseEntity<?> response = authenticationController.authenticate(authenticationRequest);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Autenticazione dell'utente avvenuta con successo.", responseBody.get("message"));
        assertEquals(authenticationResponse, responseBody.get("token"));
        verify(service, times(1)).authenticate(authenticationRequest);
    }

    @Test
    public void checkToken_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";

        //Mock
        doNothing().when(service).checkToken(anyString());

        //Test
        ResponseEntity<?> response = authenticationController.checkToken(username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Token valido.", responseBody.get("message"));
        verify(service, times(1)).checkToken(username); 
    }

    @Test
    public void revokeToken_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";

        //Mock
        doNothing().when(service).revokeToken(anyString());

        //Test
        ResponseEntity<?> response = authenticationController.revokeToken(username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Token revocati.", responseBody.get("message"));
    }
    
}
