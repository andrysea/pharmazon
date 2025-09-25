package com.andreamarino.pharmazon.services.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.chat.model.Chat;
import com.andreamarino.pharmazon.services.chat.model.ChatDto;
import com.andreamarino.pharmazon.services.chat.repository.ChatRepository;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatServiceImpl chatServiceImpl;

    @Test
    public void insertChat_WhenValidInput_ReturnObject(){
        //Setup
        String username = "andrysea";

        Client client = new Client();
        client.setId(1L);
        client.setUsername(username);
        client.setName("Andrea");
        client.setSurname("Marino");
        client.setEmail("user@user.com");
        client.setBirthDate("2001-10-03");
        client.setTaxId("1234567890123456");
        client.setNumber("1234567890");
        client.setPassword("password");
        client.setRole(Role.CLIENT);

        ClientDto clientDto = new ClientDto(client);
        
        ChatDto chatDto = new ChatDto();
        chatDto.setClientDto(clientDto);
        chatDto.setActiveChat(true);
        chatDto.setAccepted(false);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
      
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat chatSaved = invocation.getArgument(0);
            return chatSaved;
        });

        //Test
        ChatDto chatDtoInsert = chatServiceImpl.insertChat(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(chatRepository, times(1)).save(chatCaptor.capture());
        
        chatDto.setCode(chatDtoInsert.getCode());
        assertEquals(chatDto, chatDtoInsert);
    }

    @Test
    public void insertChat_WhenValidInput_NotFoundException(){
        //Setup
        String username = "andrysea";
      
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> chatServiceImpl.insertChat(username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertChat_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;
      
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> chatServiceImpl.insertChat(username));
        
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    
    @Test
    public void insertChat_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
      
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> chatServiceImpl.insertChat(username));
        
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getChatAccepted_WhenValidInput_ReturnObject(){
        //Setup
        String username = "andrysea";

        Client client = new Client();
        client.setId(1L);
        client.setUsername(username);
        client.setName("Andrea");
        client.setSurname("Marino");
        client.setEmail("user@user.com");
        client.setBirthDate("2001-10-03");
        client.setTaxId("1234567890123456");
        client.setNumber("1234567890");
        client.setPassword("password");
        client.setRole(Role.CLIENT);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setName("Andrea");
        pharmacist.setSurname("Marino");
        pharmacist.setEmail("user@user.com");
        pharmacist.setBirthDate("2001-10-03");
        pharmacist.setTaxId("1234567890123456");
        pharmacist.setNumber("1234567890");
        pharmacist.setPassword("password");
        pharmacist.setRole(Role.ADMIN);

        Chat chat = new Chat();
        chat.setId(1L);
        chat.setCode("123");
        chat.setClient(client);
        chat.setPharmacist(pharmacist);
        chat.setAccepted(true);

        ChatDto chatDto = new ChatDto(chat);

        //Mock
        when(chatRepository.findChatAccepted(anyString())).thenReturn(Optional.of(chat));

        //Test
        ChatDto chatDtoAccepted = chatServiceImpl.getChatAccepted(username);
        verify(chatRepository, times(1)).findChatAccepted(username);
        assertEquals(chatDto, chatDtoAccepted);
    }

    @Test
    public void getChatAccepted_WhenValidInput_ReturnNull(){
        //Setup
        String username = "andrysea";

        Client client = new Client();
        client.setId(1L);
        client.setUsername(username);
        client.setName("Andrea");
        client.setSurname("Marino");
        client.setEmail("user@user.com");
        client.setBirthDate("2001-10-03");
        client.setTaxId("1234567890123456");
        client.setNumber("1234567890");
        client.setPassword("password");
        client.setRole(Role.CLIENT);

        Chat chat = new Chat();
        chat.setId(1L);
        chat.setCode("123");
        chat.setClient(client);
        chat.setAccepted(true);

        //Mock
        when(chatRepository.findChatAccepted(anyString())).thenReturn(Optional.of(chat));

        //Test
        ChatDto chatDtoInsert = chatServiceImpl.getChatAccepted(username);
        verify(chatRepository, times(1)).findChatAccepted(username);
        assertEquals(null, chatDtoInsert);
    }

    @Test
    public void getChatAccepted_WhenValidInput_NoSuchElementException(){
        //Setup 
        String username = "andrysea";

        //Mock
        when(chatRepository.findChatAccepted(anyString())).thenReturn(Optional.empty());

        //Test
        assertThrows(NoSuchElementException.class,
            () -> chatServiceImpl.getChatAccepted(username));

        verify(chatRepository, times(1)).findChatAccepted(username);
    }

    @Test
    public void getChatAccepted_WhenInvalidInputNameNull_NotFoundException(){
        //Setup 
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> chatServiceImpl.getChatAccepted(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getChatAccepted_WhenInvalidInputNameEmpty_NotFoundException(){
        //Setup 
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> chatServiceImpl.getChatAccepted(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void closeChat_WhenValidInputClient_Success(){
        //Setup
        String username = "andrysea";

        Client client = new Client();
        client.setId(1L);
        client.setUsername(username);
        client.setName("Andrea");
        client.setSurname("Marino");
        client.setEmail("user@user.com");
        client.setBirthDate("2001-10-03");
        client.setTaxId("1234567890123456");
        client.setNumber("1234567890");
        client.setPassword("password");
        client.setRole(Role.CLIENT);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        doNothing().when(chatRepository).closeChatClient(anyString());

        //Test
        chatServiceImpl.closeChat(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(chatRepository, times(1)).closeChatClient(username);
    }

    @Test
    public void closeChat_WhenValidInputAdmin_Success(){
        //Setup
        String username = "andrysea";

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setName("Andrea");
        pharmacist.setSurname("Marino");
        pharmacist.setEmail("user@user.com");
        pharmacist.setBirthDate("2001-10-03");
        pharmacist.setTaxId("1234567890123456");
        pharmacist.setNumber("1234567890");
        pharmacist.setPassword("password");
        pharmacist.setRole(Role.ADMIN);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));
        doNothing().when(chatRepository).closeChat(anyString());

        //Test
        chatServiceImpl.closeChat(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(chatRepository, times(1)).closeChat(username);
    }

    @Test
    public void closeChat_WhenValidInputUserNoFind_NotFoundException(){
        //Setup
        String username = "andrysea";

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> chatServiceImpl.closeChat(username));

        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    public void closeChat_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> chatServiceImpl.closeChat(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void closeChat_WhenValidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> chatServiceImpl.closeChat(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getChatWaiting_WhenValidInput_ReturnObject(){
        String username = "andrysea";
        
        Client client = new Client();
        client.setId(1L);
        client.setUsername(username);
        client.setName("Andrea");
        client.setSurname("Marino");
        client.setEmail("user@user.com");
        client.setBirthDate("2001-10-03");
        client.setTaxId("1234567890123456");
        client.setNumber("1234567890");
        client.setPassword("password");
        client.setRole(Role.CLIENT);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setName("Andrea");
        pharmacist.setSurname("Marino");
        pharmacist.setEmail("user@user.com");
        pharmacist.setBirthDate("2001-10-03");
        pharmacist.setTaxId("1234567890123456");
        pharmacist.setNumber("1234567890");
        pharmacist.setPassword("password");
        pharmacist.setRole(Role.ADMIN);

        Chat chat = new Chat();
        chat.setId(1L);
        chat.setClient(client);
        chat.setPharmacist(pharmacist);
        chat.setActiveChat(true);
        chat.setAccepted(true);

        ChatDto chatDto = new ChatDto(chat);
        
        //Mock
        when(chatRepository.findLastAcceptedChat(anyString())).thenReturn(Optional.of(chat));

        //Test
        ChatDto chatDtoReturned = chatServiceImpl.getChatWaiting(username);
        verify(chatRepository, times(1)).findLastAcceptedChat(username);
        assertEquals(chatDto, chatDtoReturned);
    }

    @Test
    public void getChatWaiting_WhenValidInput_ReturnNull(){
        String username = "andrysea";
 
        //Mock
        when(chatRepository.findLastAcceptedChat(anyString())).thenReturn(Optional.empty());

        //Test
        ChatDto chatDtoReturned = chatServiceImpl.getChatWaiting(username);
        verify(chatRepository, times(1)).findLastAcceptedChat(username);
        assertEquals(null, chatDtoReturned);
    }

    @Test
    public void getChatWaiting_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> chatServiceImpl.getChatWaiting(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getChatWaiting_WhenValidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> chatServiceImpl.getChatWaiting(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateChatsWaiting_WhenValidInput_Success(){
        //Setup
        String username = "andrysea";

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setName("Andrea");
        pharmacist.setSurname("Marino");
        pharmacist.setEmail("user@user.com");
        pharmacist.setBirthDate("2001-10-03");
        pharmacist.setTaxId("1234567890123456");
        pharmacist.setNumber("1234567890");
        pharmacist.setPassword("password");
        pharmacist.setRole(Role.ADMIN);

        Client client = new Client();
        client.setId(1L);
        client.setUsername(username);
        client.setName("Andrea");
        client.setSurname("Marino");
        client.setEmail("user@user.com");
        client.setBirthDate("2001-10-03");
        client.setTaxId("1234567890123456");
        client.setNumber("1234567890");
        client.setPassword("password");
        client.setRole(Role.CLIENT);

        Chat chat = new Chat();
        chat.setId(1L);
        chat.setCode("123");
        chat.setClient(client);
        chat.setAccepted(true);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));
        when(chatRepository.findLastAcceptedChat()).thenReturn(Optional.of(chat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat chatSaved = invocation.getArgument(0);
            return chatSaved;
        });

        //Test
        chatServiceImpl.updateChatsWaiting(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(chatRepository, times(1)).findLastAcceptedChat();
        verify(chatRepository, times(1)).save(chatCaptor.capture());

        chat.setPharmacist(pharmacist);
        assertEquals(chat, chatCaptor.getValue());
    }

    @Test
    public void updateChatsWaiting_WhenValidInputChatNull_Success(){
        //Setup
        String username = "andrysea";

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setName("Andrea");
        pharmacist.setSurname("Marino");
        pharmacist.setEmail("user@user.com");
        pharmacist.setBirthDate("2001-10-03");
        pharmacist.setTaxId("1234567890123456");
        pharmacist.setNumber("1234567890");
        pharmacist.setPassword("password");
        pharmacist.setRole(Role.ADMIN);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));
        when(chatRepository.findLastAcceptedChat()).thenReturn(Optional.empty());

        //Test
        chatServiceImpl.updateChatsWaiting(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(chatRepository, times(1)).findLastAcceptedChat();
    }

    @Test
    public void updateChatsWaiting_WhenValidInputPharmacistNoFind_NotFoundException(){
        //Setup
        String username = "andrysea";

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> chatServiceImpl.updateChatsWaiting(username));

        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updateChatsWaiting_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> chatServiceImpl.updateChatsWaiting(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateChatsWaiting_WhenValidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> chatServiceImpl.updateChatsWaiting(username));

        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }
    
}
