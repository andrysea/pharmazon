package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Address;
import com.andreamarino.pharmazon.model.Client;

@ExtendWith(MockitoExtension.class)
public class AddressDtoTest {
    
    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        addressDto = new AddressDto();
    }

    @Test
    public void conversionDto(){
        address = new Address(1L, "ABC123", "Mario", "Rossi", "123", "Via Roma", "00100", "Roma", "RM", true, null, new Client(), new Timestamp(0));
        addressDto = new AddressDto(address);
    }

    @Test
    public void allArgsConstructor(){
        addressDto = new AddressDto("ABC123", "Mario", "Rossi", "123", "Via Roma", "00100", "Roma", "RM");

        assertEquals("ABC123", addressDto.getCode());
        assertEquals("Mario", addressDto.getName());
        assertEquals("Rossi", addressDto.getSurname());
        assertEquals("123", addressDto.getNumber());
        assertEquals("Via Roma", addressDto.getAddress());
        assertEquals("00100", addressDto.getCap());
        assertEquals("Roma", addressDto.getCity());
        assertEquals("RM", addressDto.getProvince());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(addressDto);
    }

    @Test
    public void getterAndSetter(){
        addressDto.setCode("ABC123");
        addressDto.setName("Mario");
        addressDto.setSurname("Rossi");
        addressDto.setNumber("123");
        addressDto.setAddress("Via Roma");
        addressDto.setCap("00100");
        addressDto.setCity("Roma");
        addressDto.setProvince("RM");

        assertEquals("ABC123", addressDto.getCode());
        assertEquals("Mario", addressDto.getName());
        assertEquals("Rossi", addressDto.getSurname());
        assertEquals("123", addressDto.getNumber());
        assertEquals("Via Roma", addressDto.getAddress());
        assertEquals("00100", addressDto.getCap());
        assertEquals("Roma", addressDto.getCity());
        assertEquals("RM", addressDto.getProvince());
    }

    @Test
    public void toStringMethod(){
        addressDto = new AddressDto("ABC123", "Mario", "Rossi", "123", "Via Roma", "00100", "Roma", "RM");
        
        String toStringResult = addressDto.toString();
        assertEquals(String.format("AddressDto(code=%s, name=%s, surname=%s, number=%s, address=%s, cap=%s, city=%s, province=%s)",
        addressDto.getCode(), addressDto.getName(), addressDto.getSurname(), addressDto.getNumber(), addressDto.getAddress(), addressDto.getCap(), addressDto.getCity(), addressDto.getProvince()), toStringResult);
    }

    @Test
    void testAddressEqualsAndHashCode() {
        AddressDto sameAddressDto = new AddressDto();
        
        addressDto.setName("Andrea");
        sameAddressDto.setName("Andrea");

        assertEquals(addressDto, sameAddressDto);
        assertEquals(addressDto.hashCode(), sameAddressDto.hashCode());

        sameAddressDto.setName("Giuseppe");
        assertNotEquals(addressDto, sameAddressDto);
        assertNotEquals(addressDto.hashCode(), sameAddressDto.hashCode());
    }
}
