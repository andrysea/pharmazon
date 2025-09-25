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
import com.andreamarino.pharmazon.dto.AddressDto;
import com.andreamarino.pharmazon.service.interfaceForClass.AddressService;

@ExtendWith(MockitoExtension.class)
public class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @Test
    public void insertAddressDto_WhenValidInput_ResponseCreated(){
        //Setup
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");
        String username = "andrysea";

        //Mock
        when(addressService.insertAddressDto(any(AddressDto.class), anyString())).thenReturn(addressDto);

        //Test
        ResponseEntity<?> response = addressController.insertAddressDto(addressDto, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento dell'indirizzo avvenuto con successo.", responseBody.get("message"));
        assertEquals(addressDto, responseBody.get("addressDto"));
        verify(addressService, times(1)).insertAddressDto(addressDto, username);
    }

    @Test
    public void getAddressDto_WhenValidInput_ResponseOk(){
        //Setup
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");
        String username = "andrysea";

        List<AddressDto> listAddressDto = new ArrayList<>();
        listAddressDto.add(addressDto);
    
        //Mock
        when(addressService.getAddressDto(anyString())).thenReturn(listAddressDto);

        //Test
        ResponseEntity<?> response = addressController.getAddressDto(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listAddressDto, response.getBody());
        verify(addressService, times(1)).getAddressDto(username);
    }

    @Test
    public void updateAddressDto_WhenValidInput_ResponseOk(){
        //Setup
        AddressDto addressDto = new AddressDto("123", "Andrea", "Marino", "1234567891", "Via XX Settembre", "74015", "Martina Franca", "TA");

        //Mock
        when(addressService.updateAddressDto(any(AddressDto.class))).thenReturn(addressDto);

        //Test
        ResponseEntity<?> response = addressController.updateAddressDto(addressDto);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica dell'indirizzo avvenuto con successo.", responseBody.get("message"));
        assertEquals(addressDto, responseBody.get("addressDto"));
        verify(addressService, times(1)).updateAddressDto(addressDto);
    }

    @Test
    public void deleteAdressDto_WhenValidInput_ResponseOk(){
        //Setup
        String code = "123";

        //Mock
        doNothing().when(addressService).deactivateAddressDto(anyString());

        //Test
        ResponseEntity<?> response = addressController.deactivateAddressDto(code);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Eliminazione dell' indirizzo avvenuto con successo.", responseBody.get("message"));
        verify(addressService, times(1)).deactivateAddressDto(code);
    }
}
