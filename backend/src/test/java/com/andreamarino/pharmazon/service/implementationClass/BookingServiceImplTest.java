package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
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
import com.andreamarino.pharmazon.dto.BookingDto;
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Booking;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.ServiceClass;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.BookingRepository;
import com.andreamarino.pharmazon.repository.ServiceRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    @Test
    public void insertBookingDto_WhenValidInput_ReturnedObjectInsert(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setId(1L);

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);
       
        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        serviceClass.setAvailability(2);
        serviceClass.setDateChosen(localDateTime);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        Booking booking = new Booking(bookingDto);
        booking.setClient(client);
        booking.setService(serviceClass);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<ServiceClass> serviceCaptor = ArgumentCaptor.forClass(ServiceClass.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        when(bookingRepository.findByServiceAndClient(anyLong(), anyLong())).thenReturn(Optional.empty());
        
        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking bookingSaved = invocation.getArgument(0);
            return bookingSaved;
        });

        //Test
        BookingDto bookingDtoInsert = bookingServiceImpl.insertBookingDto(bookingDto, username);
        serviceClass.setAvailability(serviceClass.getAvailability() - 1);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(serviceDto.getCode());
        verify(bookingRepository, times(1)).findByServiceAndClient(serviceClass.getId(), client.getId());
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());
        verify(bookingRepository, times(1)).save(bookingCaptor.capture());

        assertEquals(serviceClass, serviceCaptor.getValue());
        assertEquals(bookingDtoInsert.getCode(), bookingCaptor.getValue().getCode());
    }

    @Test
    public void insertBookingDto_WhenInvalidInputBookingDto_NotFoundException(){
        //Setup
        String username = "andrysea";
        BookingDto addressDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(addressDto, username));
        assertEquals("L'oggetto relativo alla prenotazione non può essere nullo.", exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        BookingDto bookingDto = new BookingDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        BookingDto bookingDto = new BookingDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenInvalidService_NotFoundException(){
        //Setup
        String username = "andrysea";
        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(null);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));

        assertEquals("Il servizio associato alla prenotazione, e' nullo.", exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenInvalidServiceCodeNull_NotFoundException(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode(null);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        assertEquals("Il codice del servizio e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenInvalidServiceCodeEmpty_NotFoundException(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        assertEquals("Il codice del servizio e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenInvalidClient_NotFoundException(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenInvalidServiceRepo_NotFoundException(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        Client client = new Client();
        client.setId(1L);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);
        bookingDto.setCode("123");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.empty());
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getCode());
        assertEquals("Non e' stato trovato nessun servizio con questo codice: " + bookingDto.getService().getCode(), exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenBookingExists_IllegalStateException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setId(1L);
       
        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        serviceClass.setAvailability(2);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        Booking booking = new Booking(bookingDto);
        booking.setClient(client);
        booking.setService(serviceClass);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        when(bookingRepository.findByServiceAndClient(anyLong(), anyLong())).thenReturn(Optional.of(booking));
    
        //Test
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        assertEquals("La prenotazione a questo servizio, e' stata gia' inserita.", exception.getMessage());
    }

    @Test
    public void insertBookingDto_WhenAvailabilityLessThanZero_IllegalStateException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        client.setId(1L);
       
        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        serviceClass.setAvailability(0);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        Booking booking = new Booking(bookingDto);
        booking.setClient(client);
        booking.setService(serviceClass);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        when(bookingRepository.findByServiceAndClient(anyLong(), anyLong())).thenReturn(Optional.empty());
    
        //Test
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            bookingServiceImpl.insertBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        verify(bookingRepository, times(1)).findByServiceAndClient(serviceClass.getId(), client.getId());
        assertEquals("A causa del numero dei posti, non e' possibile effettuare la prenotazione.", exception.getMessage());
    }

    @Test
    public void getBookingDto_WhenArePresent_ReturnList(){
        //Setup
        Booking booking = new Booking();
        booking.setService(new ServiceClass());
        
        List<Booking> listBooking = new ArrayList<>();
        listBooking.add(booking);

        List<BookingDto> listBookingDto = listBooking.stream().map(entity -> new BookingDto(entity)).collect(Collectors.toList());
        
        //Mock
        when(bookingRepository.findAll()).thenReturn(listBooking);

        //Test
        List<BookingDto> listBookingDtoRetured = bookingServiceImpl.getBookingDto();
        verify(bookingRepository, times(1)).findAll();
        assertEquals(listBookingDto, listBookingDtoRetured);
    }

    @Test
    public void getBookingDto_WhenAreNotPresent_NoSuchElementException(){
        //Setup        
        List<Booking> listBooking = new ArrayList<>();
    
        //Mock
        when(bookingRepository.findAll()).thenReturn(listBooking);

        //Test
        assertThrows(NoSuchElementException.class, () ->
                bookingServiceImpl.getBookingDto());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    public void getBookingDtoNotAccepted_WhenArePresent_ReturnList(){
        //Setup
        Booking booking = new Booking();
        booking.setService(new ServiceClass());
        
        List<Booking> listBooking = new ArrayList<>();
        listBooking.add(booking);

        List<BookingDto> listBookingDto = listBooking.stream().map(entity -> new BookingDto(entity)).collect(Collectors.toList());
        
        //Mock
        when(bookingRepository.findBookingNotAccepted()).thenReturn(listBooking);

        //Test
        List<BookingDto> listBookingDtoRetured = bookingServiceImpl.getBookingDtoNotAccepted();
        verify(bookingRepository, times(1)).findBookingNotAccepted();
        assertEquals(listBookingDto, listBookingDtoRetured);
    }

    @Test
    public void getBookingDtoNotAccepted_WhenAreNotPresent_NoSuchElementException(){
        //Setup        
        List<Booking> listBooking = new ArrayList<>();
    
        //Mock
        when(bookingRepository.findBookingNotAccepted()).thenReturn(listBooking);

        //Test
        assertThrows(NoSuchElementException.class, () ->
                bookingServiceImpl.getBookingDtoNotAccepted());
        verify(bookingRepository, times(1)).findBookingNotAccepted();
    }

    @Test
    public void getBookingDtoAccepted_WhenArePresent_ReturnList(){
        //Setup
        Booking booking = new Booking();
        booking.setService(new ServiceClass());
        
        List<Booking> listBooking = new ArrayList<>();
        listBooking.add(booking);

        List<BookingDto> listBookingDto = listBooking.stream().map(entity -> new BookingDto(entity)).collect(Collectors.toList());
        
        //Mock
        when(bookingRepository.findBookingAccepted()).thenReturn(listBooking);

        //Test
        List<BookingDto> listBookingDtoRetured = bookingServiceImpl.getBookingDtoAccepted();
        verify(bookingRepository, times(1)).findBookingAccepted();
        assertEquals(listBookingDto, listBookingDtoRetured);
    }

    @Test
    public void getBookingDtoAccepted_WhenAreNotPresent_NoSuchElementException(){
        //Setup        
        List<Booking> listBooking = new ArrayList<>();
    
        //Mock
        when(bookingRepository.findBookingAccepted()).thenReturn(listBooking);

        //Test
        assertThrows(NoSuchElementException.class, () ->
                bookingServiceImpl.getBookingDtoAccepted());
        verify(bookingRepository, times(1)).findBookingAccepted();
    }

    @Test
    public void updateBookingDto_WhenValidInputWithServiceAccepted_ReturnedObjectUpdated(){
        //Setup        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setAvailability(2);
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setAccepted(true);
        bookingDto.setService(serviceDto);

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);
        serviceClass.setDateChosen(localDateTime);
        serviceDto.setDateChosen(localDateTime);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("email@email.com");

        Booking booking = new Booking(bookingDto);
        booking.setId(1L);
        booking.setClient(client);
        booking.setService(serviceClass);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        //Mock
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.of(booking));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking bookingSaved = invocation.getArgument(0);
            return bookingSaved;
        });

        //Test
        BookingDto bookingDtoUpdated = bookingServiceImpl.updateBookingDto(bookingDto);
        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());
        verify(bookingRepository, times(1)).save(bookingCaptor.capture());

        assertEquals(booking, bookingCaptor.getValue());
        assertEquals(bookingDto, bookingDtoUpdated);
    }

    @Test
    public void updateBookingDto_WhenValidInputWithServiceNoAccepted_ReturnedObjectUpdated(){
        //Setup        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setAvailability(2);
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setAccepted(false);
        bookingDto.setService(serviceDto);

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("email@email.com");

        Booking booking = new Booking(bookingDto);
        booking.setId(1L);
        booking.setClient(client);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        //Mock
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.of(booking));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking bookingSaved = invocation.getArgument(0);
            return bookingSaved;
        });

        //Test
        BookingDto bookingDtoUpdated = bookingServiceImpl.updateBookingDto(bookingDto);
        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());
        verify(bookingRepository, times(1)).save(bookingCaptor.capture());
        booking.getService().setAvailability(booking.getService().getAvailability() + 1);

        assertEquals(booking, bookingCaptor.getValue());
        assertEquals(bookingDto.getCode(), bookingDtoUpdated.getCode());
    }

    @Test
    public void updateBookingDto_WhenValidInputAcceptedDateExpired_ReturnedObjectUpdated(){
        //Setup                
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setAccepted(true);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("email@email.com");

        LocalDateTime localDateTime = LocalDateTime.now();
        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setDateChosen(localDateTime);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCode("123");
        booking.setClient(client);
        booking.setService(serviceClass);

        //Mock
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.of(booking));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            bookingServiceImpl.updateBookingDto(bookingDto));
        assertEquals("Il servizio e' scaduto puoi solo rifiutare la prenotazione.", exception.getMessage());

        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());
    }

    @Test
    public void updateBookingDto_WhenValidInputWithoutServiceNoAccepted_ReturnedObjectUpdated(){
        //Setup        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setAccepted(false);
        bookingDto.setService(null);

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.setId(1L);
        serviceClass.setAvailability(2);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("email@email.com");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCode("123");
        booking.setService(serviceClass);
        booking.setClient(client);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<ServiceClass> serviceCaptor = ArgumentCaptor.forClass(ServiceClass.class);

        //Mock
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.of(booking));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking bookingSaved = invocation.getArgument(0);
            return bookingSaved;
        });

        //Test
        BookingDto bookingDtoUpdated = bookingServiceImpl.updateBookingDto(bookingDto);
        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());
        verify(bookingRepository, times(1)).save(bookingCaptor.capture());
        booking.getService().setAvailability(booking.getService().getAvailability() + 1);

        assertEquals(booking, bookingCaptor.getValue());
        assertEquals(serviceClass, serviceCaptor.getValue());
        assertEquals(bookingDto.getCode(), bookingDtoUpdated.getCode());
    }

    @Test
    public void updateBookingDto_WhenInvalidInputBookingDto_NotFoundException(){
        //Setup
        BookingDto bookingDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.updateBookingDto(bookingDto));
        assertEquals("L'oggetto relativo alla prenotazione non può essere nullo.", exception.getMessage());
    }

    @Test
    public void updateBookingDto_WhenInvalidInputNoBookingCodeNull_NotFoundException(){
        //Setup                
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode(null);
     
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.updateBookingDto(bookingDto));

        assertEquals("Il codice della prenotazione e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateBookingDto_WhenInvalidInputNoBookingCodeBlank_NotFoundException(){
        //Setup                
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("");
     
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.updateBookingDto(bookingDto));

        assertEquals("Il codice della prenotazione e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateBookingDto_WhenValidInputNoBookingFind_NotFoundException(){
        //Setup                
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");

        //Mock
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.empty());
     
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.updateBookingDto(bookingDto));
        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());

        assertEquals("Non e' stata trovata nessuna prenotazione con questo codice: " + bookingDto.getCode(), exception.getMessage());
    }

    @Test
    public void updateBookingDto_WhenInvalidInputAccepted_NotFoundException(){
        //Setup                
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setService(null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCode("123");

        //Mock
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.of(booking));
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.updateBookingDto(bookingDto));

        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());
        assertEquals("Non e' inserita l'accettazione o meno della prenotazione.", exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenValidInputClient_Success(){
        //Setup 
        String username = "andrysea";
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CLIENT);
        user.setEmail("user@user.com");
        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        serviceClass.setAvailability(2);
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setService(serviceDto);

        Booking booking = new Booking();
        booking.setCode("123");
        booking.setService(serviceClass);
        booking.setClient(new Client(user));
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        when(bookingRepository.findByServiceAndClient(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        doAnswer(invocation -> {
            Booking bookingDeleted = invocation.getArgument(0);
            return bookingDeleted;
        }).when(bookingRepository).delete(any(Booking.class));

        when(serviceRepository.save(any(ServiceClass.class))).thenAnswer(invocation -> {
            ServiceClass serviceSaved = invocation.getArgument(0);
            return serviceSaved;
        });

        //Test
        bookingServiceImpl.deleteBookingDto(bookingDto, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        verify(bookingRepository, times(1)).findByServiceAndClient(serviceClass.getId(), user.getId());
        verify(bookingRepository, times(1)).delete(bookingCaptor.capture());

        assertEquals(booking, bookingCaptor.getValue());
        assertEquals(bookingDto.getCode(), bookingCaptor.getValue().getCode());
        assertEquals(serviceClass, bookingCaptor.getValue().getService());
    }

    @Test
    public void deleteBookingDto_WhenValidInputAdmin_Success(){
        //Setup 
        String username = "andrysea";
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        Client client = new Client();
        client.setUsername(username);
        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setService(serviceDto);

        Booking booking = new Booking();
        booking.setCode("123");
        booking.setService(serviceClass);
        booking.setClient(client);
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.of(booking));

        doAnswer(invocation -> {
            Booking bookingDeleted = invocation.getArgument(0);
            return bookingDeleted;
        }).when(bookingRepository).delete(any(Booking.class));

        //Test
        bookingServiceImpl.deleteBookingDto(bookingDto, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());
        verify(bookingRepository, times(1)).delete(bookingCaptor.capture());

        assertEquals(booking, bookingCaptor.getValue());
        assertEquals(bookingDto.getCode(), bookingCaptor.getValue().getCode());
        assertEquals(serviceClass, bookingCaptor.getValue().getService());
    }

    @Test
    public void deleteBookingDto_WhenInvalidInputBookingDto_NotFoundException(){
        //Setup
        String username = "andrysea";
        BookingDto addressDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.deleteBookingDto(addressDto, username));
        assertEquals("L'oggetto relativo alla prenotazione non può essere nullo.", exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenInvalidBookingCodeNull_NotFoundException(){
        //Setup 
        String username = "andrysea";
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        Client client = new Client();
        client.setUsername(username);
        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode(null);
        bookingDto.setService(serviceDto);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.deleteBookingDto(bookingDto, username));
    
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        assertEquals("Il codice della prenotazione e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenInvalidBookingCodeBlank_NotFoundException(){
        //Setup 
        String username = "andrysea";
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        Client client = new Client();
        client.setUsername(username);
        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("");
        bookingDto.setService(serviceDto);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.deleteBookingDto(bookingDto, username));
    
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        assertEquals("Il codice della prenotazione e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenValidInputNoBookingFindAdmin_NotFoundException(){
        //Setup 
        String username = "andrysea";
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        Client client = new Client();
        client.setUsername(username);
        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setService(serviceDto);

        Booking booking = new Booking();
        booking.setCode("123");
        booking.setService(serviceClass);
        booking.setClient(client);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        when(bookingRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.deleteBookingDto(bookingDto, username));
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        verify(bookingRepository, times(1)).findByCode(bookingDto.getCode());

        assertEquals("Non e' stata trovata nessuna prenotazione con questo codice: " + bookingDto.getCode(), exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenValidInputNoBookingFindClient_NotFoundException(){
        //Setup 
        String username = "andrysea";
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CLIENT);

        Client client = new Client();
        client.setId(1L);
        client.setUsername(username);
        
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        ServiceClass serviceClass = new ServiceClass(serviceDto);
        serviceClass.setId(1L);
        serviceClass.setCode("123");
        
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCode("123");
        bookingDto.setService(serviceDto);

        Booking booking = new Booking();
        booking.setCode("123");
        booking.setService(serviceClass);
        booking.setClient(client);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findByCode(anyString())).thenReturn(Optional.of(serviceClass));
        when(bookingRepository.findByServiceAndClient(anyLong(), anyLong())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        bookingServiceImpl.deleteBookingDto(bookingDto, username));
        verify(userRepository, times(1)).findByUsername(username);
        verify(serviceRepository, times(1)).findByCode(bookingDto.getService().getCode());
        verify(bookingRepository, times(1)).findByServiceAndClient(serviceClass.getId(), user.getId());

        assertEquals("Non e' stata trovata nessuna prenotazione al servizio con questo codice: " + bookingDto.getService().getCode() + " ed associata a questo utente: " + username, exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        BookingDto bookingDto = new BookingDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.deleteBookingDto(bookingDto, username));
        
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        BookingDto bookingDto = new BookingDto();

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.deleteBookingDto(bookingDto, username));
        
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }
    
    @Test
    public void deleteBookingDto_WhenInvalidClient_NotFoundException(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.deleteBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenInvalidService_NotFoundException(){
        //Setup
        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(null);

        String username = "andrysea";
        User user = new User();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.deleteBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        assertEquals("Il servizio relativo alla prenotazione e' nullo.", exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenInvalidServiceCodeNull_NotFoundException(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode(null);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        User user = new User();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.deleteBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        assertEquals("Il codice del servizio e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deleteBookingDto_WhenInvalidServiceCodeBlank_NotFoundException(){
        //Setup
        String username = "andrysea";
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setService(serviceDto);

        User user = new User();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            bookingServiceImpl.deleteBookingDto(bookingDto, username));
        
        verify(userRepository, times(1)).findByUsername(username);
        assertEquals("Il codice del servizio e' nullo o vuoto.", exception.getMessage());
    }
}
