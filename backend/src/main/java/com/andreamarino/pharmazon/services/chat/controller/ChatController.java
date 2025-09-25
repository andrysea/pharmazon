package com.andreamarino.pharmazon.services.chat.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.services.chat.model.ChatDto;
import com.andreamarino.pharmazon.services.chat.service.ChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
@RequestMapping("/chat")
public class ChatController {
    
    private final ChatService chatService;

    /**
     * Gestisce la richiesta HTTP POST per inserire una nuova chat associata a un utente.
     * 
     * @param username l'username dell'utente associato alla chat
     * @return una ResponseEntity contenente un messaggio di conferma se l'inserimento è avvenuto con successo
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertChat(@RequestParam String username){
        ChatDto chatDtoInsert = chatService.insertChat(username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento della chat, associata all'utente (username), avvenuto con successo.", "chatDto", chatDtoInsert));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la chat accettata associata a un utente.
     * 
     * @param username l'username dell'utente di cui si vuole ottenere la chat accettata
     * @return una ResponseEntity contenente la chat accettata
     */
    @GetMapping("/getChatAccepted")
    public ResponseEntity<?> getChatAccepted(@RequestParam String username){
        ChatDto chatDto = chatService.getChatAccepted(username);
        return ResponseEntity.status(HttpStatus.OK).body(chatDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la chat in attesa associata a un utente.
     * 
     * @param username l'username dell'utente di cui si vuole ottenere la chat in attesa
     * @param userDetails le informazioni dell'utente autenticato
     * @return una ResponseEntity contenente la chat in attesa se l'utente è autorizzato, altrimenti restituisce un messaggio di errore
     */
    @GetMapping("/getChatWaiting")
    public ResponseEntity<?> getChatWaiting(@RequestParam String username, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails.getUsername().equals(username)){
            ChatDto chatDto = chatService.getChatWaiting(username);
            return ResponseEntity.status(HttpStatus.OK).body(chatDto);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Username inserito come parametro, non e' conforme all'username legato al token."));
        }
    }
    

    /**
     * Gestisce la richiesta HTTP PUT per aggiornare le chat in attesa associate a un utente.
     * 
     * @param username l'username dell'utente di cui si vogliono aggiornare le chat in attesa
     * @param userDetails le informazioni dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di conferma se l'aggiornamento è avvenuto con successo
     */
    @PutMapping("/updateChats")
    public ResponseEntity<?> updateChats(@RequestParam String username, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails.getUsername().equals(username)){
            chatService.updateChatsWaiting(username);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica della chat avvenuta con successo."));
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Username inserito come parametro, non e' conforme all'username legato al token."));
        }
    }


    /**
     * Gestisce la richiesta HTTP PUT per chiudere una chat associata a un utente.
     * 
     * @param username l'username dell'utente di cui si vuole chiudere la chat
     * @return una ResponseEntity contenente un messaggio di conferma se la chiusura è avvenuta con successo
     */
    @PutMapping("/closeChat")
    public ResponseEntity<?> closeChat(@RequestParam String username){
        chatService.closeChat(username);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Chiusura della chat avvenuta con successo."));
    }
}
