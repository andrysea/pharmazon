package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.AddressDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Address;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.repository.AddressRepository;
import com.andreamarino.pharmazon.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {
    
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressServiceImpl;

    @Test
    public void insertAddressDto_WhenValidInput_ReturnedObjectInsert(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            return address;
        });

        //Test
        AddressDto addressDtoInsert = addressServiceImpl.insertAddressDto(addressDto, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(addressRepository, times(1)).save(addressCaptor.capture());

        assertEquals(addressDto.getName(), addressDtoInsert.getName());
        assertEquals(addressDto.getSurname(), addressDtoInsert.getSurname());
        assertEquals(addressDto.getNumber(), addressDtoInsert.getNumber());
        assertEquals(addressDto.getAddress(), addressDtoInsert.getAddress());
        assertEquals(addressDto.getCap(), addressDtoInsert.getCap());
        assertEquals(addressDto.getCity(), addressDtoInsert.getCity());
        assertEquals(addressDto.getProvince(), addressDtoInsert.getProvince());
    }

    @Test
    public void insertAddressDto_WhenInvalidInputAddressDto_NotFoundException(){
        //Setup 
        String username = "andrysea";
        AddressDto addressDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("L'oggetto relativo all'indirizzo non può essere nullo.", exception.getMessage());
    }

    @Test
    public void insertAddressDto_WhenInvalidInputUsername_NotFoundException(){
        //Setup 
        String username = "andrysea";
        AddressDto addressDto = new AddressDto("", "", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup 
        String username = null;
        AddressDto addressDto = new AddressDto("", "", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertAddressDto_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup 
        String username = "";
        AddressDto addressDto = new AddressDto("", "", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertAddressDto_WhenInvalidInputName_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "C1", "Marino", "1234567890", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di nome utente inserito deve contenere solo lettere.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputNameNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", null, "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di nome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputNameBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di nome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }
    @Test
    public void insertAddressDto_WhenInvalidInputNameContainsSpaces_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "C ", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di nome utente inserito non puo' contenere spazi.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputSurname_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "1", "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di cognome utente inserito deve contenere solo lettere.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputSurnameNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", null, "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di cognome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputSurnameBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "", "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di cognome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputSurnameContainsSpaces_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "C ", "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di cognome utente inserito non puo' contenere spazi.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputProvinceNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", null);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore della provincia inserita non puo' essere nulla o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputProvinceBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore della provincia inserita non puo' essere nulla o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputProvinceLength_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "74015", "Martina Franca", "Taranto-Andria-Bari-Barletta");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("La provincia inserita puo' avere massimo una lunghezza di 20 caratteri.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputNumberLength_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "123456", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il numero di telefono inserito ha una quantita' di caratteri diversa da 10.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputNumber_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "C", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di numero di telefono inserito deve contenere solo numeri.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputNumberNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", null, "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di numero di telefono inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputNumberBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di numero di telefono inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputNumberContainsSpaces_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1 ", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di numero di telefono inserito non puo' contenere spazi.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputAddressBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567890", "", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore dell'indirizzo inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputAddressNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567890", null, "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore dell'indirizzo inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputAddressLength_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567890", "Via Roma 123, 00100 Città futura, Provincia ABCDEFGHI", "74015", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("L'indirizzo inserito puo' avere massimo una lunghezza di 50 caratteri.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputCap_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567890", "Via tal dei tali", "", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il valore di CAP inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertAddressDto_WhenInvalidInputCapLength_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername("andrysea");
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567890", "Via tal dei tali", "1", "Martina Franca", "TA");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.insertAddressDto(addressDto, username));
        assertEquals("Il CAP inserito ha una quantita' di caratteri diversa da 5.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getAddress_WhenValidInput_ReturnedList(){
        //Setup
        List<Address> listAddress = new ArrayList<>();
        listAddress.add(new Address());
        List<AddressDto> listAddressDto = listAddress.stream().map(entity -> new AddressDto(entity)).collect(Collectors.toList());
        String username = "andrysea";
        
        //Mock
        when(addressRepository.findAllByUsername(anyString())).thenReturn(listAddress);

        //Test
        List<AddressDto> listAddressDtoReturned = addressServiceImpl.getAddressDto(username);
        verify(addressRepository, times(1)).findAllByUsername(username);
        assertNotNull(listAddressDto);
        assertFalse(listAddressDto.isEmpty());
        assertEquals(listAddressDtoReturned, listAddressDto);
    }

    @Test
    public void getAddress_WhenValidInput_ReturnedListEmpty(){
        //Setup
        List<Address> listAddress = new ArrayList<>();
        String username = "andrysea";
        
        //Mock
        when(addressRepository.findAllByUsername(anyString())).thenReturn(listAddress);

        //Test
        assertThrows(NoSuchElementException.class, () ->
            addressServiceImpl.getAddressDto(username));

        verify(addressRepository, times(1)).findAllByUsername(username);
        
    }

    @Test
    public void getAddress_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        
        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class, () ->
            addressServiceImpl.getAddressDto(username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getAddress_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class, () ->
            addressServiceImpl.getAddressDto(username));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateAddressDto_WhenValidInput_ReturnedObjectUpdated(){
        //Setup
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");
        Address address = new Address();
        address.setActive(true);
        address.setCode("123");
        
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address addressSaved = invocation.getArgument(0);
            return addressSaved;
        });

        //Test
        AddressDto addressDtoReturned = this.addressServiceImpl.updateAddressDto(addressDto);
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
        verify(addressRepository, times(1)).save(addressCaptor.capture());
        assertEquals(addressDtoReturned, addressDto);
    }

    @Test
    public void updateAddressDto_WhenInvalidInputAddressDto_NotFoundException(){
        //Setup
        AddressDto addressDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("L'oggetto relativo all'indirizzo non può essere nullo.", exception.getMessage());
    }

    @Test
    public void updateAddressDto_WhenValidInputAddressIsDeactivate_IllegalStateException(){
        //Setup
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");
        Address address = new Address(addressDto);
        address.setActive(false);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalStateException exception =  assertThrows(IllegalStateException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));

        assertEquals("L'indirizzo è disattivato.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvaliInputCodeNull_IllegalArgumentException(){
        //Setup
        AddressDto addressDto = new AddressDto(null, "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il codice dell'indirizzo non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateAddressDto_WhenInvaliInputCodeBlank_IllegalArgumentException(){
        //Setup
        AddressDto addressDto = new AddressDto("", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il codice dell'indirizzo non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputName_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "1", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di nome utente inserito deve contenere solo lettere.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNameNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", null, "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di nome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNameBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di nome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNameContainsSpaces_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "C ", "Marino", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di nome utente inserito non puo' contenere spazi.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputSurname_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "1", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di cognome utente inserito deve contenere solo lettere.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputSurnameNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", null, "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di cognome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputSurnameBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di cognome utente inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputSurnameContainsSpaces_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Ma ", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di cognome utente inserito non puo' contenere spazi.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNumberLength_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "12", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il numero di telefono inserito ha una quantita' di caratteri diversa da 10.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNumber_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Ma", "1C", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di numero di telefono inserito deve contenere solo numeri.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNumberNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Ma", null, "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di numero di telefono inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNumberBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Ma", "", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di numero di telefono inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputNumberContainsSpaces_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1 ", "Via XX Settembre", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di numero di telefono inserito non puo' contenere spazi.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputAddressBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "", "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore dell'indirizzo inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputAddressNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", null, "74015", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore dell'indirizzo inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCapLength_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "7401", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il CAP inserito ha una quantita' di caratteri diversa da 5.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCap_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "7401C", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di CAP inserito deve contenere solo numeri.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCapNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Ma", "1234567890", "Via XX Settembre", null, "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di CAP inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCapBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di CAP inserito non puo' essere nullo o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCapContainsSpaces_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "7401 ", "Martina Franca", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore di CAP inserito non puo' contenere spazi.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCityLength_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "74015", "San Valentino in Abruzzo Citeriore  Citta' più lunga", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("La citta' inserita puo' avere massimo una lunghezza di 40 caratteri.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCityNull_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "74015", null, "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore della citta' inserita non puo' essere nulla o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void updateAddressDto_WhenInvalidInputCityBlank_IllegalArgumentException(){
        //Setup 
        String username = "andrysea";
        Client client = new Client(); 
        client.setUsername(username);

        Timestamp timestamp = new Timestamp(0);
        Address address = new Address(1L, "123", "Andrea", "Marino", "1234567890", "Via XX Settembre", "74015", "", "TA", true, null, client, timestamp);
        AddressDto addressDto = new AddressDto(address);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Il valore della citta' inserita non puo' essere nulla o vuoto.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }


    @Test
    public void updateAddressDto_WhenObjectNotFound_NotFoundException(){
        //Setup
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class, () ->
            addressServiceImpl.updateAddressDto(addressDto));
        assertEquals("Non e' stato trovato nessun indirizzo con questo codice: " + addressDto.getCode(), exception.getMessage());
        verify(addressRepository, times(1)).findByCode(addressDto.getCode());
    }

    @Test
    public void deactivateAddressDto_WhenValidInput_ObjectDeleted(){
        //Setup
        String code = "123";
        Address address = new Address();
        address.setActive(true);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        this.addressServiceImpl.deactivateAddressDto(code);
        verify(addressRepository, times(1)).findByCode(code);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    public void deactivateAddressDto_WhenValidInput_IllegalStateException(){
        //Setup
        String code = "123";
        Address address = new Address();
        address.setActive(false);

        //Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        //Test
        IllegalStateException exception =  assertThrows(IllegalStateException.class, () ->
            addressServiceImpl.deactivateAddressDto(code));
        assertEquals("Indirizzo gia' disattivato.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(code);
    }

    @Test
    public void deactivateAddressDto_WhenObjectNotFound_NotFoundException(){
        //Setup
        String code = "123";

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class, () ->
            addressServiceImpl.deactivateAddressDto(code));
        assertEquals("Non e' stato trovato nessun indirizzo con il codice fornito.", exception.getMessage());
        verify(addressRepository, times(1)).findByCode(code);
    }

    @Test
    public void deactivateAddressDto_WhenInputInvalidCodeNull_NotFoundException(){
        //Setup
        String code = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            addressServiceImpl.deactivateAddressDto(code));
        assertEquals("Il codice non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deactivateAddressDto_WhenInputInvalidCodeEmpty_NotFoundException(){
        //Setup
        String code = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            addressServiceImpl.deactivateAddressDto(code));
        assertEquals("Il codice non puo' essere nullo o vuoto.", exception.getMessage());
    }
}