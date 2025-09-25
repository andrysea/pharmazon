package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.ServiceClass;

@ExtendWith(MockitoExtension.class)
public class ServiceDtoTest {
    
    private ServiceDto serviceDto;

    @BeforeEach
    void setUp() {
        serviceDto = new ServiceDto();
    }

    @Test
    public void conversionDto(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setCode("123");
        serviceClass.setName("Test glicemia");
        serviceClass.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceClass.setPrice(12.5);
        serviceClass.setAvailability(5);
        serviceClass.setImage(image);
        serviceClass.setDateChosen(localDateTime);

        serviceDto = new ServiceDto(serviceClass);
    }

    @Test
    public void allArgsConstructor(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;

        serviceDto = new ServiceDto(name, code, description, price, image, availability, localDateTime);

        assertEquals(name, serviceDto.getName());
        assertEquals(code, serviceDto.getCode());
        assertEquals(description, serviceDto.getDescription());
        assertEquals(price, serviceDto.getPrice());
        assertEquals(image, serviceDto.getImage());
        assertEquals(availability, serviceDto.getAvailability());
        assertEquals(localDateTime, serviceDto.getDateChosen());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(serviceDto);
    }

    @Test
    public void getterAndSetter(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;

        serviceDto.setCode(code);
        serviceDto.setName(name);
        serviceDto.setDescription(description);
        serviceDto.setPrice(price);
        serviceDto.setImage(image);
        serviceDto.setAvailability(availability);
        serviceDto.setDateChosen(localDateTime);
 
        assertEquals(name, serviceDto.getName());
        assertEquals(code, serviceDto.getCode());
        assertEquals(description, serviceDto.getDescription());
        assertEquals(price, serviceDto.getPrice());
        assertEquals(image, serviceDto.getImage());
        assertEquals(availability, serviceDto.getAvailability());
        assertEquals(localDateTime, serviceDto.getDateChosen());
    }

    @Test
    public void toStringMethod(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;

        serviceDto = new ServiceDto(name, code, description, price, image, availability, localDateTime);
        
        String toStringResult = serviceDto.toString();
        String expectedToString = String.format("ServiceDto(name=%s, code=%s, description=%s, price=%s, image=%s, availability=%s, dateChosen=%s)",
        name, code, description, price, Arrays.toString(image), availability, localDateTime);
        assertEquals(expectedToString, toStringResult);
    }

     @Test
    void testEqualsAndHashCode() {
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;

        serviceDto = new ServiceDto(name, code, description, price, image, availability, localDateTime);
        
        ServiceDto sameServiceDto = new ServiceDto();
        sameServiceDto.setCode(code);
        sameServiceDto.setName(name);
        sameServiceDto.setDescription(description);
        sameServiceDto.setPrice(price);
        sameServiceDto.setImage(image);
        sameServiceDto.setAvailability(availability);
        sameServiceDto.setDateChosen(localDateTime);
       
        assertEquals(serviceDto, sameServiceDto);
        assertEquals(serviceDto.hashCode(), sameServiceDto.hashCode());

        sameServiceDto.setCode("456");

        assertNotEquals(serviceDto, sameServiceDto);
        assertNotEquals(serviceDto.hashCode(), sameServiceDto.hashCode());
    }




    
}
