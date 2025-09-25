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
public class ChatTest {

    private Chat chat;
    
    @Test
    public void conversionDto(){
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
        
        ChatDto chatDto = new ChatDto();
        chatDto.setCode(code);
        chatDto.setClientDto(clientDto);
        chatDto.setActiveChat(activeChat);
        chatDto.setAccepted(accepted);

        chat = new Chat(chatDto);
    }

    @Test
    public void allArgsConstructor(){
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

        chat = new Chat(id, code, client, pharmacist, activeChat, accepted, timestamp);
        assertEquals(id, chat.getId());
        assertEquals(code, chat.getCode());
        assertEquals(client, chat.getClient());
        assertEquals(pharmacist, chat.getPharmacist());
        assertEquals(timestamp, chat.getDateTimeCreation());
        assertEquals(activeChat, chat.getActiveChat());
        assertEquals(accepted, chat.getAccepted());
    }

    @Test
    void noArgsConstructor() {
        chat = new Chat();
        assertNotNull(chat);
    }

    @Test
    public void getterAndSetter(){

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
        
        chat = new Chat();
        chat.setId(id);
        chat.setCode(code);
        chat.setClient(client);
        chat.setPharmacist(pharmacist);
        chat.setActiveChat(activeChat);
        chat.setAccepted(accepted);
        chat.setDateTimeCreation(timestamp);

        assertEquals(id, chat.getId());
        assertEquals(code, chat.getCode());
        assertEquals(client, chat.getClient());
        assertEquals(pharmacist, chat.getPharmacist());
        assertEquals(timestamp, chat.getDateTimeCreation());
        assertEquals(activeChat, chat.getActiveChat());
        assertEquals(accepted, chat.getAccepted());
    }

    @Test
    public void toStringMethod(){
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

        chat = new Chat(id, code, client, pharmacist, activeChat, accepted, timestamp);

        String toStringResult = chat.toString();
        String expectedToString = String.format("Chat(id=%s, code=%s, client=%s, pharmacist=%s, activeChat=%s, accepted=%s, dateTimeCreation=%s)",
        id, code, client, pharmacist, activeChat, accepted, timestamp);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
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

        chat = new Chat(id, code, client, pharmacist, activeChat, accepted, timestamp);

        Chat sameChat = new Chat();
        sameChat.setId(id);
        sameChat.setCode(code);
        sameChat.setClient(client);
        sameChat.setPharmacist(pharmacist);
        sameChat.setActiveChat(activeChat);
        sameChat.setAccepted(accepted);
        sameChat.setDateTimeCreation(timestamp);

        assertEquals(chat, sameChat);
        assertEquals(chat.hashCode(), sameChat.hashCode());

        sameChat.setCode("456");
        
        assertNotEquals(chat, sameChat);
        assertNotEquals(chat.hashCode(), sameChat.hashCode());
    }
}
