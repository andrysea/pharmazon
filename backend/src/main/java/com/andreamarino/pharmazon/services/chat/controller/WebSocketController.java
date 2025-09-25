package com.andreamarino.pharmazon.services.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.services.chat.model.MessageDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WebSocketController {
    
    /**
     * Gestisce i messaggi inviati dai client al topic WebSocket associato a una specifica chat.
     * 
     * @param code il codice identificativo della chat a cui il messaggio è destinato
     * @param messageDto l'oggetto contenente il messaggio e le informazioni relative al cliente
     * @return l'oggetto contenente il messaggio e le informazioni relative al cliente
     * @throws NotFoundException se il codice, il messaggio, l'oggetto cliente o lo username sono nulli o vuoti
     */
    @MessageMapping("/chat/{code}")
    @SendTo("/topic/{code}")
    public MessageDto chat(@DestinationVariable String code, MessageDto messageDto){
        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice inserito e' nullo o vuoto.");
        }

        if(messageDto == null){
            throw new NotFoundException("L'oggetto relativo al messaggio e' nullo.");
        }

        if(messageDto.getMessage() == null || messageDto.getMessage().isEmpty()){
            throw new NotFoundException("Il messaggio inserito e' nullo o vuoto.");
        }

        if(messageDto.getUserDto() == null){
            throw new NotFoundException("L'oggetto relativo al cliente e' nullo.");
        }

        if(messageDto.getUserDto().getUsername() == null ||messageDto.getUserDto().getUsername().isEmpty()){
            throw new NotFoundException("Lo username inserito e' nullo o vuoto.");
        }

        return messageDto;
    }
}
