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
import com.andreamarino.pharmazon.dto.AddressDto;
import com.andreamarino.pharmazon.service.interfaceForClass.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
@CrossOrigin(origins = "https://localhost:4200")
public class AddressController {
    private final AddressService addressService;

    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di un nuovo indirizzo.
     * 
     * @param addressDto l'oggetto AddressDto contenente i dettagli dell'indirizzo da inserire, non può essere nullo
     * @param username il nome utente associato all'indirizzo, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto AddressDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertAddressDto(@RequestBody(required = true) AddressDto addressDto, @RequestParam(required = true) String username){
        AddressDto addressDtoInsert = addressService.insertAddressDto(addressDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento dell'indirizzo avvenuto con successo.", "addressDto", addressDtoInsert));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere gli indirizzi associati a un nome utente.
     * 
     * @param username il nome utente per cui recuperare gli indirizzi, non può essere nullo
     * @return una ResponseEntity contenente la lista degli AddressDto associati al nome utente, con uno stato di OK (200)
     */
    @GetMapping("/getAddress")
    public ResponseEntity<?> getAddressDto(@RequestParam(required = true) String username){
        List<AddressDto> listAddressDto = new ArrayList<>();
        listAddressDto = addressService.getAddressDto(username);
        return ResponseEntity.status(HttpStatus.OK).body(listAddressDto);
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare un indirizzo esistente.
     * 
     * @param addressDto l'oggetto AddressDto contenente i dettagli dell'indirizzo da aggiornare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto AddressDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateAddressDto(@RequestBody(required = true) AddressDto addressDto){
        AddressDto addressDtoUpdated = addressService.updateAddressDto(addressDto);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica dell'indirizzo avvenuto con successo.", "addressDto", addressDtoUpdated));
    }

    
    /**
     * Gestisce la richiesta HTTP PUT per eliminare un indirizzo esistente.
     * 
     * @param code il codice dell'indirizzo da eliminare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @PutMapping("/deactivate")
    public ResponseEntity<?> deactivateAddressDto(@RequestBody(required = true) String code){
        addressService.deactivateAddressDto(code);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Eliminazione dell' indirizzo avvenuto con successo."));
    }
}
