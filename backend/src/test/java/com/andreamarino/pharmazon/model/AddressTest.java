package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.AddressDto;

@ExtendWith(MockitoExtension.class)
public class AddressTest {
    
    private Address address;
    private AddressDto addressDto;
    private Client client;
    private Timestamp dataCreazione;

    @BeforeEach
    void setUp() {
        address = new Address();
        dataCreazione = new Timestamp(0);
    }

    @Test
    public void conversionDto(){
        addressDto = new AddressDto("ABC123", "Mario", "Rossi", "123", "Via Roma", "00100", "Roma", "RM");
        address = new Address(addressDto);
    }

    @Test
    public void allArgsConstructor(){
        address = new Address(1L, "ABC123", "Mario", "Rossi", "123", "Via Roma", "00100", "Roma", "RM", true, null, client, dataCreazione);

        assertEquals(1L, address.getId());
        assertEquals("ABC123", address.getCode());
        assertEquals("Mario", address.getName());
        assertEquals("Rossi", address.getSurname());
        assertEquals("123", address.getNumber());
        assertEquals("Via Roma", address.getAddress());
        assertEquals("00100", address.getCap());
        assertEquals("Roma", address.getCity());
        assertEquals("RM", address.getProvince());
        assertEquals(client, address.getClient());
        assertEquals(dataCreazione, address.getDateTimeCreation());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(address);
    }

    @Test
    public void getterAndSetter(){
        Client client = new Client();

        address.setId(1L);
        address.setCode("ABC123");
        address.setName("Mario");
        address.setSurname("Rossi");
        address.setNumber("123");
        address.setAddress("Via Roma");
        address.setCap("00100");
        address.setCity("Roma");
        address.setProvince("RM");
        address.setClient(client);
        address.setDateTimeCreation(dataCreazione);

        assertEquals(1L, address.getId());
        assertEquals("ABC123", address.getCode());
        assertEquals("Mario", address.getName());
        assertEquals("Rossi", address.getSurname());
        assertEquals("123", address.getNumber());
        assertEquals("Via Roma", address.getAddress());
        assertEquals("00100", address.getCap());
        assertEquals("Roma", address.getCity());
        assertEquals("RM", address.getProvince());
        assertEquals(client, address.getClient());
        assertEquals(dataCreazione, address.getDateTimeCreation());
    }

    @Test
    public void toStringMethod(){
        List<Order> listOrder = new ArrayList<>();        
        address = new Address(1L, "ABC123", "Mario", "Rossi", "123", "Via Roma", "00100", "Roma", "RM", true, listOrder, client, dataCreazione);
        
        String toStringResult = address.toString();
        assertEquals(String.format("Address(id=%d, code=%s, name=%s, surname=%s, number=%s, address=%s, cap=%s, city=%s, province=%s, active=%s, orders=%s, client=%s, dateTimeCreation=%s)",
            1L, address.getCode(), address.getName(), address.getSurname(), address.getNumber(), address.getAddress(), address.getCap(), address.getCity(), address.getProvince(), address.isActive(), address.getOrders(), address.getClient(), address.getDateTimeCreation()), toStringResult);
    }

    @Test
    void testAddressEqualsAndHashCode() {
        Address sameAddress = new Address();
        
        address.setName("Andrea");
        sameAddress.setName("Andrea");

        assertEquals(address, sameAddress);
        assertEquals(address.hashCode(), sameAddress.hashCode());

        sameAddress.setName("Giuseppe");
        assertNotEquals(address, sameAddress);
        assertNotEquals(address.hashCode(), sameAddress.hashCode());
    }
}
