package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
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
import com.andreamarino.pharmazon.dto.BookingDto;
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.service.interfaceForClass.BookingService;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    public void insertBookingDto_WhenValidInput_ResponseCreated(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);
        BookingDto bookingDto = new BookingDto("123", serviceDto, timestamp, true);

        //Mock
        when(bookingService.insertBookingDto(any(BookingDto.class), anyString())).thenReturn(bookingDto);

        //Test
        ResponseEntity<?> response = bookingController.insertBookingDto(bookingDto, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento della prenotazione avvenuto con successo. Attendere email di conferma.", responseBody.get("message"));
        assertEquals(bookingDto, responseBody.get("bookingDto"));
        verify(bookingService, times(1)).insertBookingDto(bookingDto, username);
    }

    @Test
    public void getBookingDto_NoInput_ResponseOk(){
        //Setup
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);
        BookingDto bookingDto = new BookingDto("123", serviceDto, timestamp, true);
        
        List<BookingDto> listBookingDto = new ArrayList<>();
        listBookingDto.add(bookingDto);

        //Mock
        when(bookingService.getBookingDto()).thenReturn(listBookingDto);

        //Test
        ResponseEntity<?> response = bookingController.getBookingDto();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listBookingDto, response.getBody());
        verify(bookingService, times(1)).getBookingDto();
    }

    @Test
    public void getBookingDtoNotAccepted_NoInput_ResponseOk(){
        //Setup
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);
        BookingDto bookingDto = new BookingDto("123", serviceDto, timestamp, true);
        
        List<BookingDto> listBookingDto = new ArrayList<>();
        listBookingDto.add(bookingDto);

        //Mock
        when(bookingService.getBookingDtoNotAccepted()).thenReturn(listBookingDto);

        //Test
        ResponseEntity<?> response = bookingController.getBookingDtoNotAccepted();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listBookingDto, response.getBody());
        verify(bookingService, times(1)).getBookingDtoNotAccepted();
    }

    @Test
    public void getBookingDtoAccepted_NoInput_ResponseOk(){
        //Setup
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);
        BookingDto bookingDto = new BookingDto("123", serviceDto, timestamp, true);
        
        List<BookingDto> listBookingDto = new ArrayList<>();
        listBookingDto.add(bookingDto);

        //Mock
        when(bookingService.getBookingDtoAccepted()).thenReturn(listBookingDto);

        //Test
        ResponseEntity<?> response = bookingController.getBookingDtoAccepted();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listBookingDto, response.getBody());
        verify(bookingService, times(1)).getBookingDtoAccepted();
    }

    @Test
    public void updateBookingDto_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);
        BookingDto bookingDto = new BookingDto("123", serviceDto, timestamp, true);

        //Mock
        when(bookingService.updateBookingDto(any(BookingDto.class))).thenReturn(bookingDto);

        //Test
        ResponseEntity<?> response = bookingController.updateBookingDto(bookingDto);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica della prenotazione avvenuta con successo.", responseBody.get("message"));
        assertEquals(bookingDto, responseBody.get("bookingDto"));
        verify(bookingService, times(1)).updateBookingDto(bookingDto);
    }

    @Test
    public void deleteBookingDto_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);
        BookingDto bookingDto = new BookingDto("123", serviceDto, timestamp, true);

        //Mock
        doNothing().when(bookingService).deleteBookingDto(any(BookingDto.class), anyString());

        //Test
        ResponseEntity<?> response = bookingController.deleteBookingDto(bookingDto, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Eliminazione della prenotazione avvenuta con successo.", responseBody.get("message"));
        verify(bookingService, times(1)).deleteBookingDto(bookingDto, username);
    }
}
