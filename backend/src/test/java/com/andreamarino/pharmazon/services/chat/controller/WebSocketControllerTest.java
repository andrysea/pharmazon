package com.andreamarino.pharmazon.services.chat.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.chat.model.MessageDto;

@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {

    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    public void chat_WhenValidInput_ReturnObject(){
        //Setup
        String code = "123";
        String username = "andrysea";

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(username);
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        MessageDto messageDto = new MessageDto();
        messageDto.setUserDto((UserDto) clientDto);
        messageDto.setLastMessage(false);
        messageDto.setMessage("Ciao avrei bisogno di alcune informazioni!");

        //Test
        MessageDto messageDtoReturned = webSocketController.chat(code, messageDto);
        assertEquals(messageDto, messageDtoReturned);
    }

    @Test
    public void chat_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String code = "123";
        String username = null;

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(username);
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        MessageDto messageDto = new MessageDto();
        messageDto.setUserDto((UserDto) clientDto);
        messageDto.setLastMessage(false);
        messageDto.setMessage("Ciao avrei bisogno di alcune informazioni!");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("Lo username inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void chat_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String code = "123";
        String username = "";

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(username);
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        MessageDto messageDto = new MessageDto();
        messageDto.setUserDto((UserDto) clientDto);
        messageDto.setLastMessage(false);
        messageDto.setMessage("Ciao avrei bisogno di alcune informazioni!");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("Lo username inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void chat_WhenInvalidInputUserNull_NotFoundException(){
        //Setup
        String code = "123";
        ClientDto clientDto = null;

        MessageDto messageDto = new MessageDto();
        messageDto.setUserDto((UserDto) clientDto);
        messageDto.setLastMessage(false);
        messageDto.setMessage("Ciao avrei bisogno di alcune informazioni!");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("L'oggetto relativo al cliente e' nullo.", exception.getMessage());
    }

    @Test
    public void chat_WhenInvalidInputMessageNull_NotFoundException(){
        //Setup
        String code = "123";
        String username = null;

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(username);
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        MessageDto messageDto = new MessageDto();
        messageDto.setUserDto((UserDto) clientDto);
        messageDto.setLastMessage(false);
        messageDto.setMessage(null);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("Il messaggio inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void chat_WhenInvalidInputMessageEmpty_NotFoundException(){
        //Setup
        String code = "123";
        String username = "";

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(username);
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        MessageDto messageDto = new MessageDto();
        messageDto.setUserDto((UserDto) clientDto);
        messageDto.setLastMessage(false);
        messageDto.setMessage("");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("Il messaggio inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void chat_WhenInvalidInputMessageDtoNull_NotFoundException(){
        //Setup
        String code = "123";
        MessageDto messageDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("L'oggetto relativo al messaggio e' nullo.", exception.getMessage());
    }

    
    @Test
    public void chat_WhenInvalidInputCodeNull_NotFoundException(){
        //Setup
        String code = null;
        MessageDto messageDto = new MessageDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void chat_WhenInvalidInputCodeEmpty_NotFoundException(){
        //Setup
        String code = "";
        MessageDto messageDto = new MessageDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            webSocketController.chat(code, messageDto));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }
    
}
