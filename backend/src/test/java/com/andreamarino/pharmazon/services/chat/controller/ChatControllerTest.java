package com.andreamarino.pharmazon.services.chat.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.chat.model.ChatDto;
import com.andreamarino.pharmazon.services.chat.service.ChatService;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    @Test
    public void insertChat_WhenValidInput_ResponseCreated(){
        //Setup
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

        ChatDto chatDto = new ChatDto();
        chatDto.setClientDto(clientDto);
        chatDto.setActiveChat(true);
        chatDto.setAccepted(false);

        //Mock
        when(chatService.insertChat(anyString())).thenReturn(chatDto);

        //Test
        ResponseEntity<?> response = chatController.insertChat(username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento della chat, associata all'utente (username), avvenuto con successo.", responseBody.get("message"));
        assertEquals(chatDto, responseBody.get("chatDto"));
        verify(chatService, times(1)).insertChat(username);
    }

    @Test
    public void getChatAccepted_WhenValidInput_ResponseOk(){
        //Setup
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

        ChatDto chatDto = new ChatDto();
        chatDto.setClientDto(clientDto);
        chatDto.setActiveChat(true);
        chatDto.setAccepted(false);

        //Mock
        when(chatService.getChatAccepted(anyString())).thenReturn(chatDto);

        //Test
        ResponseEntity<?> response = chatController.getChatAccepted(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chatDto, response.getBody());
        verify(chatService, times(1)).getChatAccepted(username);
    }

    @Test
    public void getChatWaiting_WhenValidInput_ResponseOk(){
        //Setup
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

        Client client = new Client(clientDto);

        ChatDto chatDto = new ChatDto();
        chatDto.setClientDto(clientDto);
        chatDto.setActiveChat(true);
        chatDto.setAccepted(false);

        //Mock
        when(chatService.getChatWaiting(anyString())).thenReturn(chatDto);

        //Test
        ResponseEntity<?> response = chatController.getChatWaiting(username, client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chatDto, response.getBody());
        verify(chatService, times(1)).getChatWaiting(username);
    }

    @Test
    public void getChatWaiting_WhenValidInput_ResponseForbidden(){
        //Setup
        String username = "andrysea";
    
        Client client = new Client();
        client.setUsername("admin");

        //Test
        ResponseEntity<?> response = chatController.getChatWaiting(username, client);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Username inserito come parametro, non e' conforme all'username legato al token.", responseBody.get("message"));
    }

    @Test
    public void updateChats_WhenValidInput_ResponseOk(){
        //Setup
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

        Client client = new Client(clientDto);

        //Test
        ResponseEntity<?> response = chatController.updateChats(username, client);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica della chat avvenuta con successo.", responseBody.get("message"));
    }

    @Test
    public void updateChats_WhenValidInput_ResponseForbidden(){
        //Setup
        String username = "andrysea";
        
        Client client = new Client();
        client.setUsername("admin");

        //Test
        ResponseEntity<?> response = chatController.updateChats(username, client);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Username inserito come parametro, non e' conforme all'username legato al token.", responseBody.get("message"));
    }

    @Test
    public void closeChat_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";

        //Test
        ResponseEntity<?> response = chatController.closeChat(username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Chiusura della chat avvenuta con successo.", responseBody.get("message"));
    }
    
}
