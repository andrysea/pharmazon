package com.andreamarino.pharmazon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.BookingDto;
import com.andreamarino.pharmazon.service.interfaceForClass.BookingService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class BookingController {
    private final BookingService bookingService;
        
    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di una nuova prenotazione.
     * 
     * @param bookingDto l'oggetto BookingDto contenente i dettagli della prenotazione da inserire, non può essere nullo
     * @param username il nome utente associato alla prenotazione, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto BookingDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertBookingDto(@RequestBody(required = true) BookingDto bookingDto, @RequestParam(required = true) String username){
        BookingDto bookingDtoInsert = bookingService.insertBookingDto(bookingDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento della prenotazione avvenuto con successo. Attendere email di conferma.", "bookingDto", bookingDtoInsert));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere tutte le prenotazioni.
     * 
     * @return una ResponseEntity contenente la lista di tutte le BookingDto, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getBookingDto(){
        List<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto = bookingService.getBookingDto();
        return ResponseEntity.status(HttpStatus.OK).body(bookingsDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere tutte le prenotazioni non accettate.
     * 
     * @return una ResponseEntity contenente la lista delle BookingDto non accettate, con uno stato di OK (200)
     */
    @GetMapping("/list/notAccepted")
    public ResponseEntity<?> getBookingDtoNotAccepted(){
        List<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto = bookingService.getBookingDtoNotAccepted();
        return ResponseEntity.status(HttpStatus.OK).body(bookingsDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere tutte le prenotazioni accettate.
     * 
     * @return una ResponseEntity contenente la lista delle BookingDto accettate, con uno stato di OK (200)
     */
    @GetMapping("/list/accepted")
    public ResponseEntity<?> getBookingDtoAccepted(){
        List<BookingDto> bookingsDto = new ArrayList<>();
        bookingsDto = bookingService.getBookingDtoAccepted();
        return ResponseEntity.status(HttpStatus.OK).body(bookingsDto);
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare una prenotazione esistente.
     * 
     * @param bookingDto l'oggetto BookingDto contenente i dettagli della prenotazione da aggiornare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto BookingDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")    
    public ResponseEntity<?> updateBookingDto(@RequestBody(required = true) BookingDto bookingDto){
        BookingDto bookingDtoUpdated = bookingService.updateBookingDto(bookingDto);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica della prenotazione avvenuta con successo.", "bookingDto", bookingDtoUpdated));
    }

    
    /**
     * Gestisce la richiesta HTTP DELETE per eliminare una prenotazione esistente.
     * 
     * @param bookingDto l'oggetto BookingDto contenente i dettagli della prenotazione da eliminare, non può essere nullo
     * @param username il nome utente associato alla prenotazione, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBookingDto(@RequestBody(required = true) BookingDto bookingDto, @RequestParam(required = true) String username){
        bookingService.deleteBookingDto(bookingDto, username);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Eliminazione della prenotazione avvenuta con successo."));
    }
}
