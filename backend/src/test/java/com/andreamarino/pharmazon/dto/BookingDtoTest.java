package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Booking;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.ServiceClass;

@ExtendWith(MockitoExtension.class)
public class BookingDtoTest {

    private BookingDto bookingDto;
    private Booking booking;
    
    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
    }

    @Test
    public void conversionDto(){
        booking = new Booking(1L, "123", new ServiceClass(), new Client(), new Timestamp(0), true);
        bookingDto = new BookingDto(booking);
    }

    @Test
    public void allArgsConstructor(){
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);

        bookingDto = new BookingDto("123", serviceDto, timestamp, true);

        assertEquals("123", bookingDto.getCode());
        assertEquals(serviceDto, bookingDto.getService());
        assertEquals(timestamp, bookingDto.getDateTimeCreation());
        assertEquals(true, bookingDto.getAccepted());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(bookingDto);
    }

    @Test
    public void getterAndSetter(){
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);

        bookingDto.setCode("123");
        bookingDto.setService(serviceDto);
        bookingDto.setDateTimeCreation(timestamp);
        bookingDto.setAccepted(true);

        assertEquals("123", bookingDto.getCode());
        assertEquals(serviceDto, bookingDto.getService());
        assertEquals(timestamp, bookingDto.getDateTimeCreation());
        assertEquals(true, bookingDto.getAccepted());
    }

    @Test
    public void toStringMethod(){
        ServiceDto serviceDto = new ServiceDto();
        Timestamp timestamp = new Timestamp(0);
        bookingDto = new BookingDto("123", serviceDto, timestamp, true);
        
        String toStringResult = bookingDto.toString();
        assertEquals(String.format("BookingDto(code=%s, service=%s, dateTimeCreation=%s, accepted=%s)",
        bookingDto.getCode(), bookingDto.getService(), bookingDto.getDateTimeCreation(), bookingDto.getAccepted()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        BookingDto sameBookingDto = new BookingDto();
        
        sameBookingDto.setCode("123");
        bookingDto.setCode("123");

        assertEquals(sameBookingDto, bookingDto);
        assertEquals(sameBookingDto.hashCode(), bookingDto.hashCode());

        sameBookingDto.setCode("1234");
        assertNotEquals(sameBookingDto, bookingDto);
        assertNotEquals(sameBookingDto.hashCode(), bookingDto.hashCode());
    }
}
