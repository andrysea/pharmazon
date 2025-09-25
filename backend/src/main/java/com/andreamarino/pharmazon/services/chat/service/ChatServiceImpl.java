package com.andreamarino.pharmazon.services.chat.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.chat.model.Chat;
import com.andreamarino.pharmazon.services.chat.model.ChatDto;
import com.andreamarino.pharmazon.services.chat.repository.ChatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    @Autowired
    private final ChatRepository chatRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ChatDto insertChat(String username){
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }
        
        Client client = (Client) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        Chat chat = new Chat();
        chat.setCode(this.generateCode());
        chat.setClient(client);
        chat.setActiveChat(true);
        return new ChatDto(chatRepository.save(chat));    
    }

    @Override
    public ChatDto getChatAccepted(String username) {
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }

        Chat chat = chatRepository.findChatAccepted(username)
        .orElseThrow(() -> new NoSuchElementException());

        if(chat.getPharmacist() != null)
            return new ChatDto(chat);
           
        return null;
    }

    @Override
    @Transactional
    public void updateChatsWaiting(String username){
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }

        Pharmacist pharmacist = (Pharmacist) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));
                
        Chat chat = chatRepository.findLastAcceptedChat()
        .orElse(null);

        if(chat != null){
            chat.setAccepted(true);
            chat.setPharmacist(pharmacist); 
            chatRepository.save(chat);  
        }
    }

    @Override
    public ChatDto getChatWaiting(String username) {
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }
        
        Chat chat = chatRepository.findLastAcceptedChat(username)
        .orElse(null);

        if(chat!=null){
            return new ChatDto(chat);
        }
        return null;
    }

    @Override
    @Transactional
    public void closeChat(String username) {    
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));
        
        if(user.getRole().equals(Role.ADMIN)){
            chatRepository.closeChat(username);
        }
        else{
            chatRepository.closeChatClient(username);
        }
    }

    private String generateCode() { 
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    } 
}
