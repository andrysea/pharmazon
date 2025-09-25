package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
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
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Booking;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.ServiceClass;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.BookingRepository;
import com.andreamarino.pharmazon.repository.ServiceRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceImplTest {
    
    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ServiceServiceImpl serviceServiceImpl;

    @Test
    public void insertServiceDto_WhenValidInputServiceNotFound_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);  //Giorno dopo ad oggi

        LocalDateTime localDateTime2 = LocalDateTime.now();
        localDateTime2 = localDateTime2.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);
        
        ServiceClass serviceClass = new ServiceClass(serviceDto);
    
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        ArgumentCaptor<ServiceClass> serviceCaptor = ArgumentCaptor.forClass(ServiceClass.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByNameList(anyString())).thenReturn(new ArrayList<>());
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });

        //Test
        ServiceDto serviceDtoInsert = serviceServiceImpl.insertServiceDto(serviceDto, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(serviceDto.getCode());
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());
        verify(serviceRepository, times(1)).findByNameList(serviceDto.getName());
        
        serviceClass.setPharmacist(pharmacist);
        assertEquals(serviceDto, serviceDtoInsert);
        assertEquals(serviceClass, serviceCaptor.getValue());
    }

    @Test
    public void insertServiceDto_WhenValidInputServiceNotFound_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);  //Giorno dopo ad oggi

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);
        
        ServiceClass serviceClass = new ServiceClass(serviceDto);
        
        List<ServiceClass> listService = new ArrayList<>();
        listService.add(serviceClass);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByNameList(anyString())).thenReturn(listService);
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il nome e' gia' associato ad un altro servizio per questo giorno e orario inseriti.\nModifica l' orario di almeno un'ora se vuoi mantenere questo nome.", exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(serviceDto.getCode());
        verify(serviceRepository, times(1)).findByNameList(serviceDto.getName());
    }

    @Test
    public void insertServiceDto_WhenValidInputServiceFound_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);  //Giorno dopo ad oggi

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);
        
        ServiceClass serviceClass = new ServiceClass(serviceDto);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il nuovo codice relativo al servizio e' gia' associato ad un altro servizio.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(serviceDto.getCode());
    }

    @Test
    public void insertServiceDto_WhenValidInputPharmacistNotFound_NotFoundException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);  //Giorno dopo ad oggi

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);
    
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertServiceDto_WhenInvalidInputAvailabilityNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(null);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La disponibilita' del prodotto inserita non puo' essere nullo o < 0.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputAvailabilityLessThan0_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(-1);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La disponibilita' del prodotto inserita non puo' essere nullo o < 0.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputPriceNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(null);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il prezzo del prodotto inserito non puo' essere nullo o <= 0.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputPriceLessThan1_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription(" Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(0.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il prezzo del prodotto inserito non puo' essere nullo o <= 0.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputDescriptionNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription(null);
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La descrizione del servizio inserito non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputDescriptionBlank_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName(" Test glicemia");
        serviceDto.setDescription("");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La descrizione del servizio inserito non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputDescriptionLength_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Il nostro servizio offre monitoraggio accurato del livello di glicemia per una gestione ottimale della salute.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La descrizione del servizio puo' avere massimo 100 caratteri.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputNameNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName(null);
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il nome del servizio inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputNameBlank_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il nome del servizio inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputNameLength_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Monitoraggio Completo del Livello di Glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il nome del servizio puo' avere massimo 40 caratteri.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputDateChosenNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(null);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La data fornita non puo' essere nulla.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputDateChosenBeforeNow_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La data e/o l'orario inseriti non sono corretti.\n Ricorda che la data e l'ora non possono essere antecedenti o uguali alla data e all'orario attuale.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputDateChosenAfterOneHour_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusMinutes(20);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La data e l'ora fornite devono essere almeno un'ora più avanti rispetto alla data e all'ora attuale.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputDateChosenAfter2Years_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusYears(3);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("La data fornita non puo' superare i 2 anni rispetto alla data attuale.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputImageNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(null);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("L'immagine della prescrizione, non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputImageLength0_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        byte[] imageJpg = {};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("L'immagine della prescrizione, non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputImage_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        byte[] imageJpg = {(byte) 0xFF};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Immagine non valida.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputCodeNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        byte[] imageJpg = {(byte) 0xFF};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode(null);
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il valore di codice servizio inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputCodeEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        byte[] imageJpg = {(byte) 0xFF};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il valore di codice servizio inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidInputCodeLength_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        byte[] imageJpg = {(byte) 0xFF};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("ABCD1234EFGH5678IJKL9012MNOP3456QRST7890U-V");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(10.0);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il codice del servizio puo' avere massimo 40 caratteri.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidServiceDtoNull_NotFoundException(){
        //Setup
        String username = "andrysea";        
        ServiceDto serviceDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Il service inserito non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;        
        ServiceDto serviceDto = new ServiceDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertServiceDto_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";        
        ServiceDto serviceDto = new ServiceDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.serviceServiceImpl.insertServiceDto(serviceDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateServiceDto_WhenValidInput_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = "123";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Client client = new Client();
        client.setUsername("andrysea");
        client.setEmail("user@client.com");

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        ServiceDto serviceDtoOld = new ServiceDto();
        serviceDtoOld.setCode(oldCode);
        serviceDtoOld.setName("Test glicemia");
        serviceDtoOld.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDtoOld.setPrice(12.5);
        serviceDtoOld.setAvailability(5);
        serviceDtoOld.setImage(imageJpg);
        serviceDtoOld.setDateChosen(localDateTime);

        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        ServiceClass serviceClassOld = new ServiceClass(serviceDtoOld);
        serviceClassOld.setId(1L);

        Booking booking = new Booking();
        booking.setClient(client);
        serviceClassOld.getBookings().add(booking);
        
        ServiceClass serviceClassNew = new ServiceClass(serviceDtoNew);
        serviceClassNew.setId(1L);
        serviceClassNew.setPharmacist(pharmacist);
        serviceClassNew.addObserver(client);
        serviceClassNew.addObserver(pharmacist);
        
        ArgumentCaptor<ServiceClass> serviceCaptor = ArgumentCaptor.forClass(ServiceClass.class);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByCode(oldCode)).thenReturn(Optional.of(serviceClassOld));
        when(serviceRepository.findByCode(serviceDtoNew.getCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByNameList(anyString())).thenReturn(new ArrayList<>());
        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });

        //Test
        ServiceDto serviceDtoUpdated = serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist);
        verify(serviceRepository, times(1)).findByCode(oldCode);
        verify(serviceRepository, times(1)).findByCode(serviceDtoNew.getCode());
        verify(serviceRepository, times(1)).findByNameList(serviceDtoNew.getName());
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());
        
        assertEquals(serviceClassNew, serviceCaptor.getValue());
        assertEquals(serviceDtoNew, serviceDtoUpdated);
    }

    @Test
    public void updateServiceDto_WhenValidInputListClientEmpty_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = "123";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDtoOld = new ServiceDto();
        serviceDtoOld.setCode(oldCode);
        serviceDtoOld.setName("Test glicemia");
        serviceDtoOld.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDtoOld.setPrice(12.5);
        serviceDtoOld.setAvailability(5);
        serviceDtoOld.setImage(imageJpg);
        serviceDtoOld.setDateChosen(localDateTime);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        ServiceClass serviceClassOld = new ServiceClass(serviceDtoOld);
        serviceClassOld.setId(1L);
        
        ServiceClass serviceClassNew = new ServiceClass(serviceDtoNew);
        serviceClassNew.setId(1L);
        serviceClassNew.setPharmacist(pharmacist);
        serviceClassNew.addObserver(pharmacist);
        
        ArgumentCaptor<ServiceClass> serviceCaptor = ArgumentCaptor.forClass(ServiceClass.class);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByCode(oldCode)).thenReturn(Optional.of(serviceClassOld));
        when(serviceRepository.findByCode(serviceDtoNew.getCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByNameList(anyString())).thenReturn(new ArrayList<>());
        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });

        //Test
        ServiceDto serviceDtoUpdated = serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist);
        verify(serviceRepository, times(1)).findByCode(oldCode);
        verify(serviceRepository, times(1)).findByCode(serviceDtoNew.getCode());
        verify(serviceRepository, times(1)).findByNameList(serviceDtoNew.getName());
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());
        
        assertEquals(serviceClassNew, serviceCaptor.getValue());
        assertEquals(serviceDtoNew, serviceDtoUpdated);
    }

    @Test
    public void updateServiceDto_WhenValidInputListElementsEquals_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String oldCode = "123";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        ServiceDto serviceDtoOld = new ServiceDto();
        serviceDtoOld.setCode(oldCode);
        serviceDtoOld.setName("Test glicemia");
        serviceDtoOld.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDtoOld.setPrice(12.5);
        serviceDtoOld.setAvailability(5);
        serviceDtoOld.setImage(imageJpg);
        serviceDtoOld.setDateChosen(localDateTime);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription(serviceDtoOld.getDescription());
        serviceDtoNew.setPrice(serviceDtoOld.getPrice());
        serviceDtoNew.setAvailability(serviceDtoOld.getAvailability());
        serviceDtoNew.setImage(imageJpg);
        serviceDtoNew.setDateChosen(localDateTime);

        ServiceClass serviceClassOld = new ServiceClass(serviceDtoOld);
        serviceClassOld.setId(1L);
        
        ServiceClass serviceClassNew = new ServiceClass(serviceDtoNew);
        serviceClassNew.setId(1L);
        serviceClassNew.setPharmacist(pharmacist);
        serviceClassNew.addObserver(pharmacist);
        
        ArgumentCaptor<ServiceClass> serviceCaptor = ArgumentCaptor.forClass(ServiceClass.class);

        //Mock 
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByCode(oldCode)).thenReturn(Optional.of(serviceClassOld));
        when(serviceRepository.findByCode(serviceDtoNew.getCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByNameList(anyString())).thenReturn(new ArrayList<>());
        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });

        //Test
        ServiceDto serviceDtoUpdated = serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist);
        verify(serviceRepository, times(1)).findByCode(oldCode);
        verify(serviceRepository, times(1)).findByCode(serviceDtoNew.getCode());
        verify(serviceRepository, times(1)).findByNameList(serviceDtoNew.getName());
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());
        
        assertEquals(serviceClassNew, serviceCaptor.getValue());
        assertEquals(serviceDtoNew, serviceDtoUpdated);
    }

    @Test
    public void updateServiceDto_WhenValidInputSameName_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = "123";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        Client client = new Client();
        client.setUsername("andrysea");
        client.setEmail("user@client.com");

        ServiceDto serviceDtoOld = new ServiceDto();
        serviceDtoOld.setCode(oldCode);
        serviceDtoOld.setName("Test glicemia");
        serviceDtoOld.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDtoOld.setPrice(12.5);
        serviceDtoOld.setAvailability(5);
        serviceDtoOld.setImage(imageJpg);
        serviceDtoOld.setDateChosen(localDateTime);

        Booking booking = new Booking();
        booking.setClient(client);

        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        localDateTime = localDateTime.plusDays(1);

        ServiceClass serviceClassDb = new ServiceClass();
        serviceClassDb.setId(5L);
        serviceClassDb.setName(serviceDtoNew.getName());
        serviceClassDb.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClassDb.setPrice(11.5);
        serviceClassDb.setAvailability(9);
        serviceClassDb.setImage(imageJpeg);
        serviceClassDb.setDateChosen(localDateTime);

        ServiceClass serviceClassOld = new ServiceClass(serviceDtoOld);
        serviceClassOld.setId(1L);
        serviceClassOld.getBookings().add(booking);
        
        ServiceClass serviceClassNew = new ServiceClass(serviceDtoNew);
        serviceClassNew.setId(1L);
        serviceClassNew.addObserver(client);
        serviceClassNew.setPharmacist(pharmacist);
        serviceClassNew.addObserver(pharmacist);
        
        ArgumentCaptor<ServiceClass> serviceCaptor = ArgumentCaptor.forClass(ServiceClass.class);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByCode(oldCode)).thenReturn(Optional.of(serviceClassOld));
        when(serviceRepository.findByCode(serviceDtoNew.getCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByNameList(anyString())).thenReturn(new ArrayList<>());
        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });

        //Test
        ServiceDto serviceDtoUpdated = serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist);
        verify(serviceRepository, times(1)).findByCode(oldCode);
        verify(serviceRepository, times(1)).findByCode(serviceDtoNew.getCode());
        verify(serviceRepository, times(1)).findByNameList(serviceDtoNew.getName());
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());
        
        assertEquals(serviceClassNew, serviceCaptor.getValue());
        assertEquals(serviceDtoNew, serviceDtoUpdated);
    }

    @Test
    public void updateServiceDto_WhenValidInputSameNameAndDate_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = "123";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        ServiceDto serviceDtoOld = new ServiceDto();
        serviceDtoOld.setCode(oldCode);
        serviceDtoOld.setName("Test glicemia");
        serviceDtoOld.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDtoOld.setPrice(12.5);
        serviceDtoOld.setAvailability(5);
        serviceDtoOld.setImage(imageJpg);
        serviceDtoOld.setDateChosen(localDateTime);

        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        ServiceClass serviceClassDb = new ServiceClass();
        serviceClassDb.setId(5L);
        serviceClassDb.setName(serviceDtoNew.getName());
        serviceClassDb.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClassDb.setPrice(11.5);
        serviceClassDb.setAvailability(9);
        serviceClassDb.setImage(imageJpeg);
        serviceClassDb.setDateChosen(localDateTime);

        ServiceClass serviceClassOld = new ServiceClass(serviceDtoOld);
        serviceClassOld.setId(1L);
        
        ServiceClass serviceClassNew = new ServiceClass(serviceDtoNew);
        serviceClassNew.setId(1L);

        List<ServiceClass> listServiceDb = new ArrayList<>();
        listServiceDb.add(serviceClassDb);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByCode(oldCode)).thenReturn(Optional.of(serviceClassOld));
        when(serviceRepository.findByCode(serviceDtoNew.getCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByNameList(anyString())).thenReturn(listServiceDb);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist));
        assertEquals("Il nome e' gia' associato ad un altro servizio per questo giorno e orario inseriti.\nModifica l' orario di almeno un'ora se vuoi mantenere questo nome.", exception.getMessage());

        verify(serviceRepository, times(1)).findByCode(oldCode);
        verify(serviceRepository, times(1)).findByCode(serviceDtoNew.getCode());
        verify(serviceRepository, times(1)).findByNameList(serviceDtoNew.getName());
    }

    @Test
    public void updateServiceDto_WhenValidInputSameCode_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = "123";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        Client client = new Client();
        client.setUsername("andrysea");
        client.setEmail("user@client.com");

        ServiceDto serviceDtoOld = new ServiceDto();
        serviceDtoOld.setCode(oldCode);
        serviceDtoOld.setName("Test glicemia");
        serviceDtoOld.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDtoOld.setPrice(12.5);
        serviceDtoOld.setAvailability(5);
        serviceDtoOld.setImage(imageJpg);
        serviceDtoOld.setDateChosen(localDateTime);

        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        ServiceClass serviceClassDb = new ServiceClass();
        serviceClassDb.setId(5L);
        serviceClassDb.setCode(serviceDtoNew.getCode());
        serviceClassDb.setName(serviceDtoNew.getName());
        serviceClassDb.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClassDb.setPrice(11.5);
        serviceClassDb.setAvailability(9);
        serviceClassDb.setImage(imageJpeg);
        serviceClassDb.setDateChosen(localDateTime);

        ServiceClass serviceClassOld = new ServiceClass(serviceDtoOld);
        serviceClassOld.setId(1L);
        
        ServiceClass serviceClassNew = new ServiceClass(serviceDtoNew);
        serviceClassNew.setId(1L);
        serviceClassNew.addObserver(client);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(serviceRepository.findByCode(oldCode)).thenReturn(Optional.of(serviceClassOld));
        when(serviceRepository.findByCode(serviceDtoNew.getCode())).thenReturn(Optional.of(serviceClassDb));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist));
        assertEquals("Il nuovo codice relativo al servizio e' gia' associato ad un altro servizio.", exception.getMessage());

        verify(serviceRepository, times(1)).findByCode(oldCode);
        verify(serviceRepository, times(1)).findByCode(serviceDtoNew.getCode());

    }

    @Test
    public void updateServiceDto_WhenValidInputService_NotFoundException(){
        //Setup
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = "123";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        //Mock
        when(serviceRepository.findByCode(oldCode)).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist));
        assertEquals("Non e' stato trovato nessun servizio con questo codice: " + oldCode, exception.getMessage());
    }

    @Test
    public void updateServiceDto_WhenInvalidServiceDtoNull_NotFoundException(){
        //Setup
        String oldCode = "123";
        String username = "andrysea";
        ServiceDto serviceDtoNew = null;

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist));
        assertEquals("Il service inserito non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void updateServiceDto_WhenInvalidOldCodeNull_NotFoundException(){
        //Setup
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = null;
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateServiceDto_WhenInvalidOldCodeEmpty_NotFoundException(){
        //Setup
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String oldCode = "";
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        ServiceDto serviceDtoNew = new ServiceDto();
        serviceDtoNew.setCode("456");
        serviceDtoNew.setName("Test glicemia e insulina.");
        serviceDtoNew.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceDtoNew.setPrice(13.5);
        serviceDtoNew.setAvailability(10);
        serviceDtoNew.setImage(imageJpeg);
        serviceDtoNew.setDateChosen(localDateTime);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.updateServiceDto(serviceDtoNew, oldCode, pharmacist));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getServiceListDto_WhenValidInputAdmin_ReturnList(){
        //Setup
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.ADMIN);
        
        LocalDateTime localDateTime = LocalDateTime.now();

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(5L);
        serviceClass.setCode("123");
        serviceClass.setName("Test Glicemia e insulina");
        serviceClass.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClass.setPrice(11.5);
        serviceClass.setAvailability(9);
        serviceClass.setImage(imageJpeg);
        serviceClass.setDateChosen(localDateTime);

        List<ServiceClass> listServices = new ArrayList<>();
        listServices.add(serviceClass);

        List<ServiceDto> listServiceDto = listServices.stream().map(entity -> new ServiceDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findAll()).thenReturn(listServices);

        //Test
        List<ServiceDto> listServiceDtoReturned = serviceServiceImpl.getServiceListDto(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findAll();
        assertEquals(listServiceDto, listServiceDtoReturned);
    }

    @Test
    public void getServiceListDto_WhenValidInputClient_ReturnList(){
        //Setup
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.CLIENT);
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(5L);
        serviceClass.setCode("123");
        serviceClass.setName("Test Glicemia e insulina");
        serviceClass.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClass.setPrice(11.5);
        serviceClass.setAvailability(9);
        serviceClass.setImage(imageJpeg);
        serviceClass.setDateChosen(localDateTime);

        List<ServiceClass> listServices = new ArrayList<>();
        listServices.add(serviceClass);

        List<ServiceDto> listServiceDto = listServices.stream().map(entity -> new ServiceDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByDateChosenAfter(any(LocalDateTime.class))).thenReturn(listServices);

        //Test
        List<ServiceDto> listServiceDtoReturned = serviceServiceImpl.getServiceListDto(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByDateChosenAfter(any(LocalDateTime.class));
        assertEquals(listServiceDto, listServiceDtoReturned);
    }

    @Test
    public void getServiceListDto_WhenValidInputAdmin_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.ADMIN);
        
        List<ServiceClass> listServices = new ArrayList<>();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findAll()).thenReturn(listServices);

        //Test
        assertThrows(NoSuchElementException.class,
            () -> serviceServiceImpl.getServiceListDto(username));
    
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findAll();
    }

    
    @Test
    public void getServiceListDto_WhenValidInputNoUserFind_NotFoundException(){
        //Setup
        String username = "andrysea";
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceListDto(username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);        
    }

    @Test
    public void getServiceListDto_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceListDto(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());     
    }

    @Test
    public void getServiceListDto_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceListDto(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());     
    } 

    @Test
    public void getServiceDto_WhenValidInput_ReturnObject(){
        //Setup
        String code = "123";
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(5L);
        serviceClass.setCode("123");
        serviceClass.setName("Test Glicemia e insulina");
        serviceClass.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClass.setPrice(11.5);
        serviceClass.setAvailability(9);
        serviceClass.setImage(imageJpeg);
        serviceClass.setDateChosen(localDateTime);

        ServiceDto serviceDto = new ServiceDto(serviceClass);

        //Mock
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));

        //Test
        ServiceDto serviceDtoReturned = serviceServiceImpl.getServiceDto(code);
        verify(serviceRepository, times(1)).findByCode(code);  
        assertEquals(serviceDto, serviceDtoReturned);
    }

    @Test
    public void getServiceDto_WhenValidInputNoServiceFind_NotFoundException(){
        //Setup
        String code = "123";

        //Mock
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceDto(code));
        assertEquals("Non e' stato trovato nessun servizio con questo codice: " + code, exception.getMessage());  

        verify(serviceRepository, times(1)).findByCode(code);  
    }

    @Test
    public void getServiceDto_WhenInvalidInputCodeNull_NotFoundException(){
        //Setup
        String code = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceDto(code));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void getServiceDto_WhenInvalidInputCodeEmpty_NotFoundException(){
        //Setup
        String code = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceDto(code));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void getServiceListDtoName_WhenValidInputAdmin_ReturnList(){
        //Setup
        String name = "Test Glicemia e insulina";
        String username = "andrysea";
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.ADMIN);
        
        LocalDateTime localDateTime = LocalDateTime.now();

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(5L);
        serviceClass.setCode("123");
        serviceClass.setName("Test Glicemia e insulina");
        serviceClass.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClass.setPrice(11.5);
        serviceClass.setAvailability(9);
        serviceClass.setImage(imageJpeg);
        serviceClass.setDateChosen(localDateTime);

        List<ServiceClass> listServices = new ArrayList<>();
        listServices.add(serviceClass);

        List<ServiceDto> listServiceDto = listServices.stream().map(entity -> new ServiceDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByNameList(anyString())).thenReturn(listServices);

        //Test
        List<ServiceDto> listServiceDtoReturned = serviceServiceImpl.getServiceListDtoName(username, name);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByNameList(name);
        assertEquals(listServiceDto, listServiceDtoReturned);
    }

    @Test
    public void getServiceListDtoName_WhenValidInputClient_ReturnList(){
        //Setup
        String name = "Test Glicemia e insulina";
        String username = "andrysea";
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};
        
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.CLIENT);
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(5L);
        serviceClass.setCode("123");
        serviceClass.setName("Test Glicemia e insulina");
        serviceClass.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClass.setPrice(11.5);
        serviceClass.setAvailability(9);
        serviceClass.setImage(imageJpeg);
        serviceClass.setDateChosen(localDateTime);

        List<ServiceClass> listServices = new ArrayList<>();
        listServices.add(serviceClass);

        List<ServiceDto> listServiceDto = listServices.stream().map(entity -> new ServiceDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByNameListAndDateChosenAfter(anyString(), any(LocalDateTime.class))).thenReturn(listServices);

        //Test
        List<ServiceDto> listServiceDtoReturned = serviceServiceImpl.getServiceListDtoName(username, name);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByNameListAndDateChosenAfter(anyString(), any(LocalDateTime.class));
        assertEquals(listServiceDto, listServiceDtoReturned);
    }

    @Test
    public void getServiceListDtoName_WhenValidInputAdmin_NoSuchElementException(){
        //Setup
        String name = "Test Glicemia e insulina";
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setRole(Role.ADMIN);
        
        List<ServiceClass> listServices = new ArrayList<>();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByNameList(anyString())).thenReturn(listServices);

        //Test
        assertThrows(NoSuchElementException.class,
            () -> serviceServiceImpl.getServiceListDtoName(username, name));
        
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByNameList(name);
    }

    @Test
    public void getServiceListDtoName_WhenValidInputAdminNoUserFind_NotFoundException(){
        //Setup
        String name = "Test Glicemia e insulina";
        String username = "andrysea";

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceListDtoName(username, name));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getServiceListDtoName_WhenInvalidInputNameNull_NotFoundException(){
        //Setup
        String name = null;
        String username = "andrysea";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceListDtoName(username, name));
        assertEquals("Il nome del servizio e' nullo.", exception.getMessage());
    }

    @Test
    public void getServiceListDtoName_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String name = "Test Glicemia";
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceListDtoName(username, name));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getServiceListDtoName_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String name = "Test Glicemia";
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.getServiceListDtoName(username, name));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }


    @Test
    public void deleteServiceDto_WhenValidInput_Success(){
        //Setup
        String code = "123";
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setEmail("pharmacist@pharmacist.it");

        Client client = new Client();
        client.setId(2L);
        client.setUsername("andrysea");
        client.setEmail("client@client.it");

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(5L);
        serviceClass.setCode("123");
        serviceClass.setName("Test Glicemia e insulina");
        serviceClass.setDescription("Questo test serve a misurare il livello di glucosio nel sangue e insulina.");
        serviceClass.setPrice(11.5);
        serviceClass.setAvailability(9);
        serviceClass.setImage(imageJpeg);
        serviceClass.setDateChosen(localDateTime);
        serviceClass.setPharmacist(pharmacist);

        serviceClass.addObserver(client);

        //Mock
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        doNothing().when(serviceRepository).delete(any(ServiceClass.class));

        //Test
        serviceServiceImpl.deleteServiceDto(code);
        verify(serviceRepository, times(1)).findByCode(code);
        verify(serviceRepository, times(1)).delete(serviceClass);
    }

    @Test
    public void deleteServiceDto_WhenValidInputNoServiceFind_NotFoundException(){
        //Setup
        String code = "123";

        //Mock
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.deleteServiceDto(code));
        assertEquals("Non e' stato trovato nessun servizio con questo codice.", exception.getMessage());

        verify(serviceRepository, times(1)).findByCode(code);
    }

    @Test
    public void deleteServiceDto_WhenInvalidInputCodeNull_NotFoundException(){
        //Setup
        String code = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.deleteServiceDto(code));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deleteServiceDto_WhenInvalidInputCodeEmpty_NotFoundException(){
        //Setup
        String code = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> serviceServiceImpl.deleteServiceDto(code));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }
}
