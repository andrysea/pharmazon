package com.andreamarino.pharmazon.services.chat.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class ChatDtoTest {

    private ChatDto chatDto;

    @Test
    public void conversionDto(){
        Long id = 1L;
        String code = "123";
        Boolean activeChat = true;
        Boolean accepted = true;

        Client client = new Client();
        client.setId(1L);
        client.setUsername("andrysea");
        client.setName("Andrea");
        client.setSurname("Marino");
        client.setEmail("user@user.com");
        client.setBirthDate("2001-10-03");
        client.setTaxId("1234567890123455");
        client.setNumber("1234567890");
        client.setPassword("password");
        client.setRole(Role.CLIENT);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername("admin");
        pharmacist.setName("Andrea");
        pharmacist.setSurname("Marino");
        pharmacist.setEmail("admin@admin.com");
        pharmacist.setBirthDate("2001-10-03");
        pharmacist.setTaxId("1234567890123456");
        pharmacist.setNumber("1234567890");
        pharmacist.setPassword("password");
        pharmacist.setRole(Role.ADMIN);

        Timestamp timestamp = new Timestamp(0);

        Chat chat = new Chat();
        chat.setId(id);
        chat.setCode(code);
        chat.setClient(client);
        chat.setPharmacist(pharmacist);
        chat.setActiveChat(activeChat);
        chat.setAccepted(accepted);
        chat.setDateTimeCreation(timestamp);

        chatDto = new ChatDto(chat);
    }

    @Test
    public void allArgsConstructor(){
        String code = "123";
        Boolean activeChat = true;
        Boolean accepted = true;

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
        
        chatDto = new ChatDto(code, clientDto, activeChat, accepted);

        assertEquals(code, chatDto.getCode());
        assertEquals(clientDto, chatDto.getClientDto());
        assertEquals(activeChat, chatDto.getActiveChat());
        assertEquals(accepted, chatDto.getAccepted());
    }

    @Test
    void noArgsConstructor() {
        chatDto = new ChatDto();
        assertNotNull(chatDto);
    }

    @Test
    public void getterAndSetter(){
        String code = "123";
        Boolean activeChat = true;
        Boolean accepted = true;

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

        chatDto = new ChatDto();
        chatDto.setCode(code);
        chatDto.setClientDto(clientDto);
        chatDto.setAccepted(accepted);
        chatDto.setActiveChat(activeChat);

        assertEquals(code, chatDto.getCode());
        assertEquals(clientDto, chatDto.getClientDto());
        assertEquals(activeChat, chatDto.getActiveChat());
        assertEquals(accepted, chatDto.getAccepted());
    }

    @Test
    public void toStringMethod(){
        String code = "123";
        Boolean activeChat = true;
        Boolean accepted = true;

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
        
        chatDto = new ChatDto(code, clientDto, activeChat, accepted);
        
        String toStringResult = chatDto.toString();
        String expectedToString = String.format("ChatDto(code=%s, clientDto=%s, activeChat=%s, accepted=%s)",
        code, clientDto, activeChat, accepted);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        String code = "123";
        Boolean activeChat = true;
        Boolean accepted = true;

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
        
        chatDto = new ChatDto(code, clientDto, activeChat, accepted);

        ChatDto sameChatDto = new ChatDto();
        sameChatDto.setCode(code);
        sameChatDto.setClientDto(clientDto);
        sameChatDto.setAccepted(accepted);
        sameChatDto.setActiveChat(activeChat);

        assertEquals(chatDto, sameChatDto);
        assertEquals(chatDto.hashCode(), sameChatDto.hashCode());

        sameChatDto.setCode("456");

        assertNotEquals(chatDto, sameChatDto);
        assertNotEquals(chatDto.hashCode(), sameChatDto.hashCode());
    }
}
