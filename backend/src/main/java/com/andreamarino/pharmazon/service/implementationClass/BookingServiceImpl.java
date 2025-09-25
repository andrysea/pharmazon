package com.andreamarino.pharmazon.service.implementationClass;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.BookingDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Booking;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.ServiceClass;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.BookingRepository;
import com.andreamarino.pharmazon.repository.ServiceRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.service.interfaceForClass.BookingService;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ServiceRepository serviceRepository;

    @Autowired
    private final EmailService emailService;
    
    @Override
    @Transactional
    public BookingDto insertBookingDto(BookingDto bookingDto, String username) {  
        if(bookingDto == null){
            throw new NotFoundException("L'oggetto relativo alla prenotazione non può essere nullo.");
        }

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        if(bookingDto.getService() == null){
            throw new NotFoundException("Il servizio associato alla prenotazione, e' nullo.");
        }       

        if(bookingDto.getService().getCode() == null  || bookingDto.getService().getCode().isEmpty()){
            throw new NotFoundException("Il codice del servizio e' nullo o vuoto.");
        }       

        Client client = (Client) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        ServiceClass service = serviceRepository.findByCode(bookingDto.getService().getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun servizio con questo codice: " + bookingDto.getService().getCode()));
              
        // Verifico se la prenotazione al servizio è già stata effettuata
        if(!(bookingRepository.findByServiceAndClient(service.getId(), client.getId()).isPresent())){
            if(service.getAvailability() > 0){
                LocalDateTime now = LocalDateTime.now();
                if(service.getDateChosen().isAfter(now)){
                    Booking booking = new Booking();
                    booking.setCode(this.generateCode());
                    booking.setClient(client);
                    service.setAvailability(service.getAvailability() - 1);
                    booking.setService(service);
        
                    serviceRepository.save(service);
                    BookingDto bookingDtoSaved = new BookingDto(bookingRepository.save(booking));
                    return bookingDtoSaved;
                }
                else{
                    throw new IllegalStateException("Non è stato possibile iscriversi al servizio, perche' risulta scaduto.");
                }    
            }
            else{
                throw new IllegalStateException("A causa del numero dei posti, non e' possibile effettuare la prenotazione.");
            }
        }
        else{
            throw new IllegalStateException("La prenotazione a questo servizio, e' stata gia' inserita.");
        }
    }

    @Override
    public List<BookingDto> getBookingDto(){       
        List<Booking> listBooking = bookingRepository.findAll();        
        if (listBooking.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<BookingDto> listBookingDto = listBooking.stream().map(entity -> new BookingDto(entity)).collect(Collectors.toList());
        return listBookingDto;
    }

    @Override
    public List<BookingDto> getBookingDtoNotAccepted(){       
        List<Booking> listBooking = bookingRepository.findBookingNotAccepted();    
        if (listBooking.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<BookingDto> listBookingDto = listBooking.stream().map(entity -> new BookingDto(entity)).collect(Collectors.toList());
        return listBookingDto;
    }

    @Override
    public List<BookingDto> getBookingDtoAccepted(){        
        List<Booking> listBooking = bookingRepository.findBookingAccepted();        
        if (listBooking.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<BookingDto> listBookingDto = listBooking.stream().map(entity -> new BookingDto(entity)).collect(Collectors.toList());
        return listBookingDto;
    }

    @Override
    @Transactional
    public BookingDto updateBookingDto(BookingDto bookingDto) {
        if(bookingDto == null){
            throw new NotFoundException("L'oggetto relativo alla prenotazione non può essere nullo.");
        }

        if(bookingDto.getCode() == null || bookingDto.getCode().isEmpty()){
            throw new NotFoundException("Il codice della prenotazione e' nullo o vuoto.");
        }

        Booking booking = bookingRepository.findByCode(bookingDto.getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stata trovata nessuna prenotazione con questo codice: " + bookingDto.getCode()));
                
        if(bookingDto.getAccepted() == null){
            throw new NotFoundException("Non e' inserita l'accettazione o meno della prenotazione.");
        }
        booking.setAccepted(bookingDto.getAccepted());
                
        if(bookingDto.getAccepted()){
            LocalDateTime now = LocalDateTime.now();
            //Se la data del servizio è scaduta, rimuovo la prenotazione
            if(!booking.getService().getDateChosen().isAfter(now)){
                throw new IllegalArgumentException("Il servizio e' scaduto puoi solo rifiutare la prenotazione.");
            }
            else{
                this.sendEmail(booking.getClient().getEmail(), "Prenotazione - Status Update.", "La tua prenotazione relativa al servizio: "  + booking.getService().getName() + ", in data: " + booking.getService().getDateChosen() + " e' stata confermata.");
            }
        }
        else{
            this.sendEmail(booking.getClient().getEmail(), "Prenotazione - Status Update.", "La tua prenotazione relativa al servizio: "  + booking.getService().getName() + ", in data: " + booking.getService().getDateChosen() + " e' stata cancellata.");
            booking.getService().setAvailability(booking.getService().getAvailability() + 1);
            serviceRepository.save(booking.getService());
        }

        BookingDto bookingDtoUpdated = new BookingDto(bookingRepository.save(booking));
        return bookingDtoUpdated;
    }

    @Override
    @Transactional
    public void deleteBookingDto(BookingDto bookingDto, String username) {      
        Booking booking;
        if(bookingDto == null){
            throw new NotFoundException("L'oggetto relativo alla prenotazione non può essere nullo.");
        }
        
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }
        
        User user = (User) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));
        
        if(bookingDto.getService() !=  null){
            if(bookingDto.getService().getCode() == null || bookingDto.getService().getCode().isEmpty()){
                throw new NotFoundException("Il codice del servizio e' nullo o vuoto.");
            }
        }
        else{
            throw new NotFoundException("Il servizio relativo alla prenotazione e' nullo.");
        }

        ServiceClass service = serviceRepository.findByCode(bookingDto.getService().getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun servizio con questo codice: " + bookingDto.getService().getCode()));

        if(user.getRole().equals(Role.CLIENT)){
             booking = bookingRepository.findByServiceAndClient(service.getId(), user.getId())
            .orElseThrow(() -> new NotFoundException("Non e' stata trovata nessuna prenotazione al servizio con questo codice: " + bookingDto.getService().getCode() + " ed associata a questo utente: " + username));
            
            service.setAvailability(service.getAvailability() + 1);
            serviceRepository.save(service);

            this.sendEmail(booking.getClient().getEmail(), "Prenotazione - Status Update.", "La tua prenotazione relativa al servizio: "  + booking.getService().getName() + ", in data: " + booking.getService().getDateChosen() + " e' stata cancellata.");
        }
        else{
            if(bookingDto.getCode() == null || bookingDto.getCode().isEmpty()){
                throw new NotFoundException("Il codice della prenotazione e' nullo o vuoto.");
            }

            booking = bookingRepository.findByCode(bookingDto.getCode())
            .orElseThrow(() -> new NotFoundException("Non e' stata trovata nessuna prenotazione con questo codice: " + bookingDto.getCode()));
        }
        
        bookingRepository.delete(booking);
    } 

    private String generateCode() { 
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    } 

    private void sendEmail(String email, String subject, String text){
        emailService.sendSimpleEmail(email, subject , text);
    }
}
