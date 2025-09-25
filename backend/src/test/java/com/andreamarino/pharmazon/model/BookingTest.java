package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.BookingDto;
import com.andreamarino.pharmazon.dto.ServiceDto;

@ExtendWith(MockitoExtension.class)
public class BookingTest {
    
    private BookingDto bookingDto;
    private Booking booking;
    
    @BeforeEach
    void setUp() {
        booking = new Booking();
    }

    @Test
    public void conversionDto(){
        bookingDto = new BookingDto("123", new ServiceDto(), new Timestamp(0), true);
        booking = new Booking(bookingDto);
    }

    @Test
    public void allArgsConstructor(){
        ServiceClass service = new ServiceClass();
        Client client = new Client();
        Timestamp timestamp = new Timestamp(0);

        booking = new Booking(1L, "123", service, client, timestamp, true);

        assertEquals(1L, booking.getId());
        assertEquals("123", booking.getCode());
        assertEquals(service, booking.getService());
        assertEquals(client, booking.getClient());
        assertEquals(timestamp, booking.getDateTimeCreation());
        assertEquals(true, booking.getAccepted());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(booking);
    }

    @Test
    public void getterAndSetter(){
        ServiceClass service = new ServiceClass();
        Client client = new Client();
        Timestamp timestamp = new Timestamp(0);

        booking.setId(1L);
        booking.setCode("123");
        booking.setService(service);
        booking.setClient(client);
        booking.setDateTimeCreation(timestamp);
        booking.setAccepted(true);

        assertEquals(1L, booking.getId());
        assertEquals("123", booking.getCode());
        assertEquals(service, booking.getService());
        assertEquals(client, booking.getClient());
        assertEquals(timestamp, booking.getDateTimeCreation());
        assertEquals(true, booking.getAccepted());
    }

    @Test
    public void toStringMethod(){
        ServiceClass service = new ServiceClass();
        Client client = new Client();
        Timestamp timestamp = new Timestamp(0);
        
        booking = new Booking(1L, "123", service, client, timestamp, true);
        
        String toStringResult = booking.toString();
        assertEquals(String.format("Booking(id=%s, code=%s, service=%s, client=%s, dateTimeCreation=%s, accepted=%s)",
        booking.getId(), booking.getCode(), booking.getService(), booking.getClient(), booking.getDateTimeCreation(), booking.getAccepted()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        Booking sameBooking = new Booking();
        
        sameBooking.setCode("123");
        booking.setCode("123");

        assertEquals(sameBooking, booking);
        assertEquals(sameBooking.hashCode(), booking.hashCode());

        sameBooking.setCode("1234");
        assertNotEquals(sameBooking, booking);
        assertNotEquals(sameBooking.hashCode(), booking.hashCode());
    }
}
