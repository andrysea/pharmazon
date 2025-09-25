package com.andreamarino.pharmazon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.service.interfaceForClass.ServiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class ServiceController {
    
    private final ServiceService serviceService;
    
    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di un nuovo servizio.
     * 
     * @param username il nome utente associato al servizio, non può essere nullo
     * @param serviceDto l'oggetto ServiceDto contenente i dettagli del servizio da inserire, non può essere nullo
     * @param userDetails i dettagli dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto ServiceDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertServiceDto(@RequestParam(required = true) String username, @RequestBody(required = true) ServiceDto serviceDto, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails.getUsername().equals(username)){
            ServiceDto serviceDtoInsert = serviceService.insertServiceDto(serviceDto, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento del servizio avvenuto con successo.", "serviceDto", serviceDtoInsert));
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Username inserito come parametro, non e' conforme all'username legato al token."));
        }
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare un servizio esistente.
     * 
     * @param serviceDto l'oggetto ServiceDto contenente i nuovi dettagli del servizio, non può essere nullo
     * @param oldCode il vecchio codice identificativo del servizio, non può essere nullo
     * @param userDetails i dettagli dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto ServiceDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")    
    public ResponseEntity<?> updateServiceDto(@RequestBody(required = true) ServiceDto serviceDto, @RequestParam(required = true) String oldCode,  @AuthenticationPrincipal UserDetails userDetails){
        ServiceDto serviceDtoUpdated = serviceService.updateServiceDto(serviceDto, oldCode, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica del servizio avvenuta con successo.", "serviceDto", serviceDtoUpdated));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista dei servizi associati ad un utente specifico.
     * 
     * @param username il nome utente associato ai servizi, non può essere nullo
     * @return una ResponseEntity contenente la lista di ServiceDto dell'utente specificato, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getServiceDto(@RequestParam(required = true) String username){
        List<ServiceDto> listServiceDto = new ArrayList<>();
        listServiceDto = serviceService.getServiceListDto(username);
        return ResponseEntity.status(HttpStatus.OK).body(listServiceDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere i dettagli di un servizio tramite il suo codice.
     * 
     * @param code il codice identificativo del servizio, non può essere nullo
     * @return una ResponseEntity contenente l'oggetto ServiceDto del servizio trovato, con uno stato di OK (200)
     */
    @GetMapping("/getService")
    public ResponseEntity<?> getServiceDtoByCode(@RequestParam(required = true) String code){
        ServiceDto serviceDto = serviceService.getServiceDto(code);
        return ResponseEntity.status(HttpStatus.OK).body(serviceDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista dei servizi per nome.
     * 
     * @param username il nome utente associato ai servizi, non può essere nullo
     * @param name il nome dei servizi da cercare, non può essere nullo
     * @return una ResponseEntity contenente la lista di ServiceDto dei servizi trovati, con uno stato di OK (200)
     */
    @GetMapping("/getService/name")
    public ResponseEntity<?> getServiceDtoByName(@RequestParam(required = true) String username, @RequestParam(required = true) String name){
        List<ServiceDto> listServiceDto = serviceService.getServiceListDtoName(username, name);
        return ResponseEntity.status(HttpStatus.OK).body(listServiceDto);
    }


    /**
     * Gestisce la richiesta HTTP DELETE per eliminare un servizio.
     * 
     * @param code il codice identificativo del servizio da eliminare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteServiceDto(@RequestParam(required = true) String code){
        serviceService.deleteServiceDto(code);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Eliminazione del servizio avvenuta con successo."));
    }
}
