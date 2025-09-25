package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.security.user.ChangePasswordRequest;
import com.andreamarino.pharmazon.service.implementationClass.UserServiceImpl;
import com.andreamarino.pharmazon.service.interfaceForClass.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    @Test
    public void updateUserDto_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User();
        user.setUsername(username);

        //Mock
        when(userService.updateUserDto(any(UserDto.class), anyString())).thenReturn(userDto);

        //Test
        ResponseEntity<?> response = userController.updateUserDto(userDto, username, user);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica dell'utente avvenuta con successo.", responseBody.get("message"));
        assertEquals(userDto, responseBody.get("userDto"));
        verify(userService, times(1)).updateUserDto(userDto, username);
    }

    @Test
    public void updatePassword_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setUsername(username);

        Client client = new Client(user);

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password_corrente", "password_nuova", "password_nuova");
        
        //Mock
        doNothing().when(userService).updatePassword(any(ChangePasswordRequest.class), any(Client.class));

        //Test
        ResponseEntity<?> response = userController.updatePassword(changePasswordRequest, client);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica della password utente avvenuta con successo.", responseBody.get("message"));
        verify(userService, times(1)).updatePassword(changePasswordRequest, client);
    }

    @Test
    public void updateUserDto_WhenValidInput_ResponseForbidden(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User();
        user.setUsername("username");

        //Test
        ResponseEntity<?> response = userController.updateUserDto(userDto, username, user);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Username inserito come parametro, non e' conforme all'username legato al token.", responseBody.get("message"));
    }

    @Test
    public void getUserListDto_WhenValid_ResponseOk(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        List<UserDto> listUserDto = new ArrayList<>();
        listUserDto.add(userDto);
        
        //Mock
        when(userService.getUserListDto()).thenReturn(listUserDto);

        //Test
        ResponseEntity<?> response = userController.getUserListDto();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listUserDto, response.getBody());
        verify(userService, times(1)).getUserListDto();
    }

    @Test
    public void changePassword_WhenValidInput_ResponseOk(){
        //Setup
        String email = "user@user.com";
        
        //Mock
        doNothing().when(userService).sendEmail(anyString());

        //Test
        ResponseEntity<?> response = userController.changePassword(email);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica della password avvenuta con successo.", responseBody.get("message"));
        verify(userService, times(1)).sendEmail(email);
    }

    @Test
    public void getUserDto_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");
        
        //Mock
        when(userService.findByUsername(anyString())).thenReturn(userDto);

        //Test
        ResponseEntity<?> response = userController.getUserDto(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    public void deleteUserDto_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";

        User user = new User();
        user.setUsername(username);
        
        //Mock
        doNothing().when(userService).deleteUserDto(anyString());

        //Test
        ResponseEntity<?> response = userController.deleteUserDto(username, user);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Eliminazione dell'utente avvenuta con successo.", responseBody.get("message"));
        verify(userService, times(1)).deleteUserDto(username);
    }
}
