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
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.service.interfaceForClass.CreditCardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/creditCard")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class CreditCardController {

    private final CreditCardService creditCardService;
    
    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di una nuova carta di credito.
     * 
     * @param creditCardDto l'oggetto CreditCardDto contenente i dettagli della carta di credito da inserire, non può essere nullo
     * @param username il nome utente associato alla carta di credito, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto CreditCardDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertCreditCardDto(@RequestBody(required = true) CreditCardDto creditCardDto, @RequestParam(required = true) String username){
        CreditCardDto creditCardDtoInsert = creditCardService.insertCreditCardDto(creditCardDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento della carta di credito avvenuto con successo.", "creditCardDto", creditCardDtoInsert));
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare una carta di credito esistente.
     * 
     * @param creditCardDto l'oggetto CreditCardDto contenente i dettagli della carta di credito da aggiornare, non può essere nullo
     * @param username il nome utente associato alla carta di credito, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto CreditCardDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCreditCardDto(@RequestBody(required = true) CreditCardDto creditCardDto, @RequestParam(required = true) String username){
        CreditCardDto creditCardDtoUpdated = creditCardService.updateCreditCardDto(creditCardDto, username);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica della carta di credito avvenuta con successo.", "creditCardDto", creditCardDtoUpdated));
    }

    
    /**
     * Gestisce la richiesta HTTP PUT per disattivare una carta di credito.
     * 
     * @param number il numero della carta di credito da eliminare, non può essere nullo
     * @param username il nome utente associato alla carta di credito, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @PutMapping("/deactivate")
    public ResponseEntity<?> deactivate(@RequestBody(required = true) String number, @RequestParam(required = true) String username){
        creditCardService.deactivateCreditCardDto(number, username);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Eliminazione della carta di credito avvenuta con successo."));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere tutte le carte di credito di un utente.
     * 
     * @param username il nome utente associato alle carte di credito, non può essere nullo
     * @return una ResponseEntity contenente la lista di tutte le CreditCardDto associate all'utente, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getCreditCardDto(@RequestParam(required = true) String username){
        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
            listCreditCardDto = creditCardService.getCreditCardDto(username);
            return ResponseEntity.status(HttpStatus.OK).body(listCreditCardDto);
    }    
}
