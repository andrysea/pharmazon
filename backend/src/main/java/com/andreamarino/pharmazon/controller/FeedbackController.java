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
import com.andreamarino.pharmazon.dto.FeedbackDto;
import com.andreamarino.pharmazon.service.interfaceForClass.FeedbackService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class FeedbackController {

    private final FeedbackService feedbackService;
    
    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di un nuovo feedback.
     * 
     * @param feedbackDto l'oggetto FeedbackDto contenente i dettagli del feedback da inserire, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto FeedbackDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertFeedbackDto(@RequestBody(required = true) FeedbackDto feedbackDto){
        FeedbackDto feedbackDtoInsert = feedbackService.insertFeedbackDto(feedbackDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento del feedback avvenuto con successo.", "feedbackDto", feedbackDtoInsert));
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare un feedback esistente.
     * 
     * @param feedbackDto l'oggetto FeedbackDto contenente i dettagli del feedback da aggiornare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto FeedbackDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateFeedbackDto(@RequestBody(required = true) FeedbackDto feedbackDto){
        FeedbackDto feedbackDtoUpdated = feedbackService.updateFeedbackDto(feedbackDto);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica del feedback avvenuta con successo.", "feedbackDto", feedbackDtoUpdated));
    }


    /**
     * Gestisce la richiesta HTTP DELETE per eliminare un feedback.
     * 
     * @param code il codice identificativo del feedback da eliminare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFeedback(@RequestParam(required = true) String code){
        feedbackService.deleteFeedback(code);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Eliminazione del feedback avvenuta con successo."));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista dei feedback di un utente specifico.
     * 
     * @param username il nome utente associato ai feedback, non può essere nullo
     * @return una ResponseEntity contenente la lista di tutte le FeedbackDto dell'utente specificato, con uno stato di OK (200)
     */
    @GetMapping("/listFeedback/user")
    public ResponseEntity<?> listFeedbackUser(@RequestParam(required = true) String username){
        List<FeedbackDto> listFeedbackDto = new ArrayList<>();
        listFeedbackDto = feedbackService.getFeedbackListUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(listFeedbackDto);
    }

    
    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista di tutti i feedback.
     * 
     * @return una ResponseEntity contenente la lista di tutte le FeedbackDto, con uno stato di OK (200)
     */
    @GetMapping("/listFeedback")
    public ResponseEntity<?> listFeedback(){
        List<FeedbackDto> listFeedbackDto = new ArrayList<>();
        listFeedbackDto = feedbackService.getFeedbackList();
        return ResponseEntity.status(HttpStatus.OK).body(listFeedbackDto);
    }
}
