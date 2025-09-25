package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.model.designPattern.observer.service.ObserverService;
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    private ServiceClass serviceClass;

    @BeforeEach
    void setUp() {
        serviceClass = new ServiceClass();
    }

    @Test
    public void conversionDto(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;

        ServiceDto serviceDto = new ServiceDto();

        serviceDto.setCode(code);
        serviceDto.setName(name);
        serviceDto.setDescription(description);
        serviceDto.setPrice(price);
        serviceDto.setImage(image);
        serviceDto.setAvailability(availability);
        serviceDto.setDateChosen(localDateTime);

        serviceClass = new ServiceClass(serviceDto);
    }

    @Test
    public void allArgsConstructor(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        Long id = 1L;
        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;
        Timestamp dateTimeCreation = new Timestamp(0);
        
        Client client = new Client();
        client.setRole(Role.CLIENT);

        List<ObserverService> listClientObserver = new ArrayList<>();
        listClientObserver.add(client);
        
        Booking booking = new Booking(1L, "123", serviceClass, client, dateTimeCreation, true);
        List<Booking> listBookings = new ArrayList<>();
        listBookings.add(booking);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername("admin");
        pharmacist.setRole(Role.ADMIN);
        
        serviceClass = new ServiceClass(id, code, name, description, price, localDateTime, availability, image, listBookings, pharmacist, listClientObserver, dateTimeCreation);

        assertEquals(id, serviceClass.getId());
        assertEquals(code, serviceClass.getCode());
        assertEquals(name, serviceClass.getName());
        assertEquals(description, serviceClass.getDescription());
        assertEquals(price, serviceClass.getPrice());
        assertEquals(localDateTime, serviceClass.getDateChosen());
        assertEquals(availability, serviceClass.getAvailability());
        assertEquals(image, serviceClass.getImage());
        assertEquals(listBookings, serviceClass.getBookings());
        assertEquals(pharmacist, serviceClass.getPharmacist());
        assertEquals(listClientObserver, serviceClass.getObserversClientService());
        assertEquals(dateTimeCreation, serviceClass.getDateTimeCreation());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(serviceClass);
    }

    @Test
    public void getterAndSetter(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        Long id = 1L;
        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;
        Timestamp dateTimeCreation = new Timestamp(0);
        
        Client client = new Client();
        client.setRole(Role.CLIENT);

        List<ObserverService> listClientObserver = new ArrayList<>();
        listClientObserver.add(client);
        
        Booking booking = new Booking(1L, "123", serviceClass, client, dateTimeCreation, true);
        List<Booking> listBookings = new ArrayList<>();
        listBookings.add(booking);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername("admin");
        pharmacist.setRole(Role.ADMIN);
        listClientObserver.add(pharmacist);
        serviceClass.removeObserver(client);

        serviceClass.setId(id);
        serviceClass.setCode(code);
        serviceClass.setName(name);
        serviceClass.setDescription(description);
        serviceClass.setPrice(price);
        serviceClass.setDateTimeCreation(dateTimeCreation);
        serviceClass.setAvailability(availability);
        serviceClass.setImage(image);
        serviceClass.setBookings(listBookings);
        serviceClass.setPharmacist(pharmacist);
        serviceClass.setObserversClientService(listClientObserver);
        serviceClass.setDateChosen(localDateTime);
 
        assertEquals(id, serviceClass.getId());
        assertEquals(code, serviceClass.getCode());
        assertEquals(name, serviceClass.getName());
        assertEquals(description, serviceClass.getDescription());
        assertEquals(price, serviceClass.getPrice());
        assertEquals(localDateTime, serviceClass.getDateChosen());
        assertEquals(availability, serviceClass.getAvailability());
        assertEquals(image, serviceClass.getImage());
        assertEquals(listBookings, serviceClass.getBookings());
        assertEquals(pharmacist, serviceClass.getPharmacist());
        assertEquals(listClientObserver, serviceClass.getObserversClientService());
        assertEquals(dateTimeCreation, serviceClass.getDateTimeCreation());
    }

    @Test
    public void toStringMethod(){
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
                
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        Long id = 1L;
        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;
        Timestamp dateTimeCreation = new Timestamp(0);
        
        Client client = new Client();
        client.setRole(Role.CLIENT);

        List<ObserverService> listClientObserver = new ArrayList<>();
        listClientObserver.add(client);
        
        Booking booking = new Booking(1L, "123", serviceClass, client, dateTimeCreation, true);
        List<Booking> listBookings = new ArrayList<>();
        listBookings.add(booking);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername("admin");
        pharmacist.setRole(Role.ADMIN);
        
        serviceClass = new ServiceClass(id, code, name, description, price, localDateTime, availability, image, listBookings, pharmacist, listClientObserver, dateTimeCreation);
        
        String toStringResult = serviceClass.toString();
        String expectedToString = String.format("ServiceClass(id=%s, code=%s, name=%s, description=%s, price=%s, dateChosen=%s, availability=%s, image=%s, bookings=%s, pharmacist=%s, observersClientService=%s, dateTimeCreation=%s)",
        id, code, name, description, price, localDateTime, availability, Arrays.toString(image), listBookings, pharmacist, listClientObserver, dateTimeCreation);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        byte[] image = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
                
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1); 

        Long id = 1L;
        String code = "123";
        String name = "Test glicemia";
        String description = "Questo test serve a misurare il livello di glucosio nel sangue.";
        Double price = 12.5;
        Integer availability = 5;
        Timestamp dateTimeCreation = new Timestamp(0);
        
        Client client = new Client();
        client.setRole(Role.CLIENT);

        List<ObserverService> listClientObserver = new ArrayList<>();
        listClientObserver.add(client);
        
        Booking booking = new Booking(1L, "123", serviceClass, client, dateTimeCreation, true);
        List<Booking> listBookings = new ArrayList<>();
        listBookings.add(booking);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername("admin");
        pharmacist.setRole(Role.ADMIN);
        
        serviceClass = new ServiceClass(id, code, name, description, price, localDateTime, availability, image, listBookings, pharmacist, listClientObserver, dateTimeCreation);
        
        ServiceClass sameServiceClass = new ServiceClass();
        sameServiceClass.setId(id);
        sameServiceClass.setCode(code);
        sameServiceClass.setName(name);
        sameServiceClass.setDescription(description);
        sameServiceClass.setPrice(price);
        sameServiceClass.setImage(image);
        sameServiceClass.setAvailability(availability);
        sameServiceClass.setDateChosen(localDateTime);
        sameServiceClass.setBookings(listBookings);
        sameServiceClass.setPharmacist(pharmacist);
        sameServiceClass.setObserversClientService(listClientObserver);
        sameServiceClass.setDateTimeCreation(dateTimeCreation);
       
        assertEquals(serviceClass, sameServiceClass);
        assertEquals(serviceClass.hashCode(), sameServiceClass.hashCode());

        sameServiceClass.setCode("456");

        assertNotEquals(serviceClass, sameServiceClass);
        assertNotEquals(serviceClass.hashCode(), sameServiceClass.hashCode());
    }
}
