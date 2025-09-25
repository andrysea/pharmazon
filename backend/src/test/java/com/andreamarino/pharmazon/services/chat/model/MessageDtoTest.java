package com.andreamarino.pharmazon.services.chat.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class MessageDtoTest {
    
    private MessageDto messageDto;

    @Test
    public void allArgsConstructor(){
        String message = "Ciao! Avrei bisogno di sapere...";
        boolean lastMessage = false;

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        messageDto = new MessageDto(clientDto, message, lastMessage);

        assertEquals(clientDto, messageDto.getUserDto());
        assertEquals(message, messageDto.getMessage());
        assertEquals(lastMessage, messageDto.isLastMessage());
    }

    @Test
    void noArgsConstructor() {
        messageDto = new MessageDto();
        assertNotNull(messageDto);
    }

    @Test
    public void getterAndSetter(){
        String message = "Ciao! Avrei bisogno di sapere...";
        boolean lastMessage = false;

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        messageDto = new MessageDto();
        messageDto.setUserDto(clientDto);
        messageDto.setMessage(message);
        messageDto.setLastMessage(lastMessage);

        assertEquals(clientDto, messageDto.getUserDto());
        assertEquals(message, messageDto.getMessage());
        assertEquals(lastMessage, messageDto.isLastMessage());
    }

    @Test
    public void toStringMethod(){
        String message = "Ciao! Avrei bisogno di sapere...";
        boolean lastMessage = false;

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setName("Andrea");
        clientDto.setSurname("Marino");
        clientDto.setEmail("user@user.com");
        clientDto.setBirthDate("2001-10-03");
        clientDto.setTaxId("1234567890123456");
        clientDto.setNumber("1234567890");
        clientDto.setPassword("password");
        clientDto.setRole(Role.CLIENT);
        clientDto.setCreditCardsDto(new ArrayList<>());

        messageDto = new MessageDto(clientDto, message, lastMessage);

        String toStringResult = messageDto.toString();
        String expectedToString = String.format("MessageDto(userDto=%s, message=%s, lastMessage=%s)",
        clientDto, message, lastMessage);
        assertEquals(expectedToString, toStringResult);
    }
}
