package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.CreditCard;
import com.andreamarino.pharmazon.repository.CreditCardRepository;
import com.andreamarino.pharmazon.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CreditCardServiceImplTest {
    
    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreditCardServiceImpl creditCardServiceImpl;

    @Test
    public void insertCreditCardDto_WhenValidInput_Success(){
        //Setup   
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);
        
        ArgumentCaptor<CreditCard> creditCardCaptor = ArgumentCaptor.forClass(CreditCard.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.empty());
        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(invocation -> {
            CreditCard creditCardSaved = invocation.getArgument(0);
            return creditCardSaved;
        });

        //Test
        this.creditCardServiceImpl.insertCreditCardDto(creditCardDto, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(creditCardRepository, times(1)).findByNumber(creditCard.getNumber());
        verify(creditCardRepository, times(1)).save(creditCardCaptor.capture());

        assertEquals(creditCard, creditCardCaptor.getValue());
    }

    @Test
    public void insertCreditCardDto_WhenValidInputCreditCardExist_DuplicateException(){
        //Setup   
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        CreditCard creditCard2 = new CreditCard(creditCardDto);
        creditCard.setNumber("1234 ");
        creditCard.setClient(client);

        List<CreditCard> listCreditCards = new ArrayList<>();
        listCreditCards.add(creditCard2);
        listCreditCards.add(creditCard);
        client.setCreditCards(listCreditCards);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il numero a 16 cifre inserito, appartiene ad un'altra carta di credito registrata.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertCreditCardDto_WhenValidInputCreditCardExistOtherUser_DuplicateException(){
        //Setup   
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123459");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard2 = new CreditCard(creditCardDto);
        creditCard2.setNumber("1234567890123458");
        creditCard2.setClient(client);

        List<CreditCard> listCreditCards = new ArrayList<>();
        listCreditCards.add(creditCard2);
        client.setCreditCards(listCreditCards);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard2));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("La carta di credito e' già registrata con un altro utente.\nPer utilizzarla l'utente deve rimuoverla dal suo account.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertCreditCardDto_WhenValidInputCreditCardExistOtherUserIsDeactivate_ReturnObject() {
        // Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123459");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123458");
        creditCard.setClient(client);
        creditCard.setActive(false);

        List<CreditCard> listCreditCards = new ArrayList<>();
        listCreditCards.add(creditCard);
        client.setCreditCards(listCreditCards);

        ArgumentCaptor<CreditCard> creditCardCaptor = ArgumentCaptor.forClass(CreditCard.class);

        // Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(client));
        when(creditCardRepository.findByNumber(creditCardDto.getNumber())).thenReturn(Optional.empty());
        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(invocation -> {
            CreditCard creditCardSaved = invocation.getArgument(0);
            return creditCardSaved;
        });

        // Test
        CreditCardDto result = creditCardServiceImpl.insertCreditCardDto(creditCardDto, username);

        // Verify
        verify(userRepository, times(1)).findByUsername(username);
        verify(creditCardRepository, times(1)).findByNumber(creditCardDto.getNumber());
        verify(creditCardRepository, times(1)).save(creditCardCaptor.capture());

        CreditCard capturedCreditCard = creditCardCaptor.getValue();

        // Asserts
        assertNotNull(result);
        assertEquals(creditCardDto.getNumber(), capturedCreditCard.getNumber());
        assertTrue(capturedCreditCard.isActive());
        assertEquals(creditCardDto.getName(), capturedCreditCard.getName());
        assertEquals(creditCardDto.getSurname(), capturedCreditCard.getSurname());
        assertEquals(client, capturedCreditCard.getClient());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidClient_NotFoundException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty()); 

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardSecurityCodeNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode(null);
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);
 
        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di codice di sicurezza inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardSecurityCodeEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di codice di sicurezza inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardSecurityCodeContainsLetters_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
      
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("12L");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di codice di sicurezza inserito deve contenere solo numeri.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardSecurityCodeLength_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("1234");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il codice di sicurezza deve essere a tre cifre.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditNumberNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber(null);
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di numero carta di credito inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditNumberEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di numero carta di credito inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardNumberContainsLetters_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("123456789012345L");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di numero carta di credito inserito deve contenere solo numeri.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardNumberLength_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("12345678901234567");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il numero associato alla carta di credito deve avere 16 caratteri.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardExpirationDateNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate(null);
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("La data di scadenza non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardExpirationDateEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("La data di scadenza non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardExpirationDateFormat_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2025");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("La data di scadenza della carta di credito non e' valida. Giusto formato in cui inserirla: (yyyy-MM).", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidNameNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName(null);
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di nome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidNameEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
            assertEquals("Il valore di nome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidNameContainsSpaces_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea ");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di nome inserito non puo' contenere spazi.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidSurnameNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname(null);
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di cognome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidSurnameEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
            assertEquals("Il valore di cognome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidSurnameContainsSpaces_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino ");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di cognome inserito non puo' contenere spazi.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCreditCardDto_WhenInvalidCreditCardNull_NotFoundException(){
        //Setup
        String username = "andrysea";
        CreditCardDto creditCardDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.insertCreditCardDto(creditCardDto, username));
        assertEquals("L'oggetto relativo alla carta di credito non può essere nullo.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenValidInput_Success(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        CreditCard creditCard2 = new CreditCard(creditCardDto);
        creditCard.setNumber("1234 ");
        creditCard.setClient(client);

        List<CreditCard> listCreditCards = new ArrayList<>();
        listCreditCards.add(creditCard2);
        listCreditCards.add(creditCard);
        client.setCreditCards(listCreditCards);
        ArgumentCaptor<CreditCard> creditCardCaptor = ArgumentCaptor.forClass(CreditCard.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(invocation -> {
            CreditCard creditCardSaved = invocation.getArgument(0);
            return creditCardSaved;
        });

        //Test
        CreditCardDto CreditCardDtoUpdated = creditCardServiceImpl.updateCreditCardDto(creditCardDto, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(creditCardRepository, times(1)).save(creditCardCaptor.capture());

        assertEquals(creditCardDto, CreditCardDtoUpdated);
    }

    @Test
    public void updateCreditCardDto_WhenValidInput_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        List<CreditCard> listCreditCards = new ArrayList<>();
        client.setCreditCards(listCreditCards);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        assertThrows(NoSuchElementException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    public void updateCreditCardDto_WhenInvalidCreditCard_NotFoundException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setNumber("1234 ");
        creditCard.setClient(client);

        List<CreditCard> listCreditCards = new ArrayList<>();
        listCreditCards.add(creditCard);
        client.setCreditCards(listCreditCards);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Non e' stata trovata nessuna carta di credito.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updateCreditCardDto_WhenInvalidNameNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName(null);
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di nome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidNameEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di nome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidNameContainsSpaces_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea ");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di nome inserito non puo' contenere spazi.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidSurnameNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname(null);
        creditCardDto.setCardSecurityCode("123");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di cognome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidSurnameEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("");
        creditCardDto.setCardSecurityCode("123");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di cognome inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidSurnameContainsSpaces_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino ");
        creditCardDto.setCardSecurityCode("123");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di cognome inserito non puo' contenere spazi.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }
    
    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardSecurityCodeNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);     

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode(null);
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di codice di sicurezza inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardSecurityCodeEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di codice di sicurezza inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardSecurityCodeContainsLetters_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("12L");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di codice di sicurezza inserito deve contenere solo numeri.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardSecurityCodeLength_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("1234");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il codice di sicurezza deve essere a tre cifre.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditNumberNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber(null);
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di numero carta di credito inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditNumberEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di numero carta di credito inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardNumberContainsLetters_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("12345678901234L");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il valore di numero carta di credito inserito deve contenere solo numeri.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardNumberLength_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("12345678901234567");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("Il numero associato alla carta di credito deve avere 16 caratteri.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardExpirationDateNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate(null);
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("La data di scadenza non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardExpirationDateEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("La data di scadenza non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidCreditCardExpirationDateFormat_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2025");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("La data di scadenza della carta di credito non e' valida. Giusto formato in cui inserirla: (yyyy-MM).", exception.getMessage());
    }

    @Test
    public void updateCreditCardDto_WhenInvalidInputCreditCard_NotFoundException(){
        //Setup
        String username = "andrysea";    
        CreditCardDto creditCardDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.updateCreditCardDto(creditCardDto, username));
        assertEquals("L'oggetto relativo alla carta di credito non può essere nullo.", exception.getMessage());
    }

    @Test
    public void deactivateCreditCardDto_WhenValidInput_Success(){
        //Setup
        String number = "1234567890123456";
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);
        client.setCreditCards(listCreditCard);

        ArgumentCaptor<CreditCard> creditCardCaptor = ArgumentCaptor.forClass(CreditCard.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(invocation -> {
            CreditCard creditCardSaved = invocation.getArgument(0);
            return creditCardSaved;
        });

        //Test
        creditCardServiceImpl.deactivateCreditCardDto(number, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(creditCardRepository, times(1)).save(creditCardCaptor.capture());
        assertEquals(creditCard, creditCardCaptor.getValue());
    }

    @Test
    public void deactivateCreditCardDto_WhenValidInput_IllegalStateException(){
        //Setup
        String number = "1234567890123456";
        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setBalance(100.0);
        creditCard.setClient(client);
        creditCard.setActive(false);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);
        client.setCreditCards(listCreditCard);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
        creditCardServiceImpl.deactivateCreditCardDto(number, username));
        assertEquals("Carta di credito gia' disattivata.", exception.getMessage());
    
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void deactivateCreditCardDto_WhenInvalidCreditCard_NotFoundException(){
       //Setup
       String number = "1234567890123456";
       String username = "andrysea";
       Client client = new Client();
       client.setUsername(username);
       
       CreditCardDto creditCardDto = new CreditCardDto();
       creditCardDto.setNumber("1234567890123456");
       creditCardDto.setExpirationDate("");
       creditCardDto.setName("Andrea");
       creditCardDto.setSurname("Marino");
       creditCardDto.setCardSecurityCode("123");

       CreditCard creditCard = new CreditCard(creditCardDto);
       creditCard.setBalance(100.0);
       creditCard.setClient(client);

       List<CreditCard> listCreditCard = new ArrayList<>();
       creditCard.setNumber("12L");
       listCreditCard.add(creditCard);
       client.setCreditCards(listCreditCard);
       
       //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.deactivateCreditCardDto(number, username));
        assertEquals("Non e' stata trovata nessuna carta di credito.", exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void deactivateCreditCardDto_WhenInvalidClient_NotFoundException(){
        //Setup
        String number = "1234567890123456";
        String username = "andrysea";

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.deactivateCreditCardDto(number, username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
    }

    @Test
    public void deactivateCreditCardDto_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String number = "1234567890123456";
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.deactivateCreditCardDto(number, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deactivateCreditCardDto_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String number = "1234567890123456";
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.deactivateCreditCardDto(number, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deactivateCreditCardDto_WhenInvalidNumberNull_NotFoundException(){
        //Setup
        String number = null;
        String username = "andrysea";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.deactivateCreditCardDto(number, username));
        assertEquals("Il numero non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deactivateCreditCardDto_WhenInvalidNumberEmpty_NotFoundException(){
        //Setup
        String number = "";
        String username = "andrysea";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.deactivateCreditCardDto(number, username));
        assertEquals("Il numero non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getCreditCardDto_WhenValidInput_ReturnedList(){
        //Setup
        String username = "andrysea";
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2030-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setBalance(100.0);
        
        CreditCard creditCard = new CreditCard(creditCardDto);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        //Mock
        when(creditCardRepository.findByUsername(anyString())).thenReturn(listCreditCard);

        //Test
        creditCardServiceImpl.getCreditCardDto(username);
        verify(creditCardRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getCreditCardDto_WhenValid_ReturnedList(){
        //Setup
        String username = "andrysea";
        
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setExpirationDate("2025-02");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setBalance(null);
        
        CreditCard creditCard = new CreditCard(creditCardDto);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        //Mock
        when(creditCardRepository.findByUsername(anyString())).thenReturn(listCreditCard);

        //Test
        List<CreditCardDto> listCardDtoGet = creditCardServiceImpl.getCreditCardDto(username);
        verify(creditCardRepository, times(1)).findByUsername(username);
        assertEquals(listCreditCardDto, listCardDtoGet);
    }

    @Test
    public void getCreditCardDto_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.getCreditCardDto(username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    
    @Test
    public void getCreditCardDto_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            creditCardServiceImpl.getCreditCardDto(username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getCreditCardDto_WhenValidInput_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        List<CreditCard> listCreditCard = new ArrayList<>();

        //Mock
        when(creditCardRepository.findByUsername(anyString())).thenReturn(listCreditCard);

        //Test
        assertThrows(NoSuchElementException.class, () ->
            creditCardServiceImpl.getCreditCardDto(username));
        verify(creditCardRepository, times(1)).findByUsername(username);
    }
}
