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
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.service.interfaceForClass.CreditCardService;

@ExtendWith(MockitoExtension.class)
public class CreditCardControllerTest {
    
    @Mock
    private CreditCardService creditCardService;

    @InjectMocks
    private CreditCardController creditCardController;

    @Test
    public void insertCreditCardDto_WhenValidInput_ResponseCreated(){
        //Setup
        String username = "andrysea";
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        //Mock
        when(creditCardService.insertCreditCardDto(any(CreditCardDto.class), anyString())).thenReturn(creditCardDto);

        //Test
        ResponseEntity<?> response = creditCardController.insertCreditCardDto(creditCardDto, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(creditCardDto, responseBody.get("creditCardDto"));
        assertEquals("Inserimento della carta di credito avvenuto con successo.", responseBody.get("message"));
        
        verify(creditCardService, times(1)).insertCreditCardDto(creditCardDto, username);
    }

    @Test
    public void updateCreditCardDto_WhenValidInput_ResponseOk(){

        //Setup
        String username = "andrysea";
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        //Mock
        when(creditCardService.updateCreditCardDto(any(CreditCardDto.class), anyString())).thenReturn(creditCardDto);

        //Test
        ResponseEntity<?> response = creditCardController.updateCreditCardDto(creditCardDto, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(creditCardDto, responseBody.get("creditCardDto"));
        assertEquals("Modifica della carta di credito avvenuta con successo.", responseBody.get("message"));
        
        verify(creditCardService, times(1)).updateCreditCardDto(creditCardDto, username);
    }

    @Test
    public void deactivateCreditCardDto_WhenValidInput_ResponseOk(){

        //Setup
        String username = "andrysea";
        String number = "1234567890123456";

        //Mock
        doNothing().when(creditCardService).deactivateCreditCardDto(anyString(), anyString());

        //Test
        ResponseEntity<?> response = creditCardController.deactivate(number, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Eliminazione della carta di credito avvenuta con successo.", responseBody.get("message"));
        
        verify(creditCardService, times(1)).deactivateCreditCardDto(number, username);
    }

    @Test
    public void getCreditCardDto_WhenValidInput_ResponseCreated(){

        //Setup
        String username = "andrysea";

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        //Mock
        when(creditCardService.getCreditCardDto(anyString())).thenReturn(listCreditCardDto);

        //Test
        ResponseEntity<?> response = creditCardController.getCreditCardDto(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listCreditCardDto, response.getBody());
        
        verify(creditCardService, times(1)).getCreditCardDto(username);
    }
}
