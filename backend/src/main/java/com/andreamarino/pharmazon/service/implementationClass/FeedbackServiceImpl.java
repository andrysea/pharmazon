package com.andreamarino.pharmazon.service.implementationClass;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.FeedbackDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Feedback;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import com.andreamarino.pharmazon.repository.FeedbackRepository;
import com.andreamarino.pharmazon.repository.ProductRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.service.interfaceForClass.FeedbackService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService{

    @Autowired
    private final FeedbackRepository feedbackRepository;

    @Autowired
    private final CartItemRepository cartItemRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FeedbackDto insertFeedbackDto(FeedbackDto feedbackDto){
        this.checkFeedback(feedbackDto);

        if(feedbackDto.getCartItemDto().getCode() == null || feedbackDto.getCartItemDto().getCode().isEmpty()){
            throw new IllegalArgumentException("Il codice del prodotto del carrello non puo' essere nullo o vuoto.");
        }
        if(feedbackDto.getCartItemDto().getName() == null || feedbackDto.getCartItemDto().getName().isEmpty()){
            throw new IllegalArgumentException("Il nome del prodotto del carrello non puo' essere nullo o vuoto.");
        }
        if(feedbackDto.getClientDto().getUsername() == null || feedbackDto.getClientDto().getUsername().isEmpty()){
            throw new IllegalArgumentException("Lo username non puo' essere nullo o vuoto.");
        }

        Client client = (Client) userRepository.findByUsername(feedbackDto.getClientDto().getUsername())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + feedbackDto.getClientDto().getUsername()));
        
        List<CartItem> listCartItem = cartItemRepository.findByCodeAndNameAndUsername(feedbackDto.getCartItemDto().getCode(), feedbackDto.getCartItemDto().getName(), client.getUsername());

        if(listCartItem.isEmpty()){
            throw new IllegalStateException("Il prodotto non e' stato acquistato in passato.");
        }

        CartItem cartItem = listCartItem.get(0);
        if(cartItem.getFeedbacks().size() > 0){
            throw new IllegalStateException("E' gia' stata inserita una recensione.");
        }

        Feedback feedback = new Feedback();
        feedback.setCode(this.generateCode());
        feedback.setDescription(feedbackDto.getDescription());
        feedback.setClient(client);
        feedback.setCartItem(cartItem);
        return new FeedbackDto(this.feedbackRepository.save(feedback));
    }

    @Override
    @Transactional
    public FeedbackDto updateFeedbackDto(FeedbackDto feedbackDto) {  
        this.checkFeedback(feedbackDto);
        ValidationUtils.validateString(feedbackDto.getCode(), "codice feedback");
        Feedback feedback = feedbackRepository.findByCode(feedbackDto.getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun feedback con questo codice: " + feedbackDto.getCode()));
        feedback.setDescription(feedbackDto.getDescription());
        return new FeedbackDto(this.feedbackRepository.save(feedback));
    }

    private void checkFeedback(FeedbackDto feedbackDto){
        if(feedbackDto == null){
            throw new NotFoundException("Il feedback non puo' essere nullo.");
        }
        if(feedbackDto.getDescription() == null || feedbackDto.getDescription().isBlank()){
            throw new IllegalArgumentException("La descrizione non puo' essere nulla o contenere solo spazi.");
        }
        if(feedbackDto.getDescription().length() > 101){
            throw new IllegalArgumentException("La descrizione deve essere al massimo 100 caratteri.");
        }
        feedbackDto.setDescription(feedbackDto.getDescription().trim());
    }

    @Override
    @Transactional
    public void deleteFeedback(String code) {  
        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice inserito e' nullo o vuoto.");
        }
   
        Feedback feedback = feedbackRepository.findByCode(code)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun feedback con questo codice: " + code));
        feedbackRepository.delete(feedback);
    }

    @Override
    public List<FeedbackDto> getFeedbackListUser(String username) {     
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito è nullo o vuoto.");
        }

        List<Feedback> listFeedback = this.feedbackRepository.findAllByClientUsername(username);
        if (listFeedback.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<FeedbackDto> listFeedbackDto =  listFeedback.stream().map(entity -> new FeedbackDto(entity)).collect(Collectors.toList());
        return listFeedbackDto;
    }

    @Override
    public List<FeedbackDto> getFeedbackList() {  
        List<Feedback> listFeedback = this.feedbackRepository.findAll();
        if (listFeedback.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<FeedbackDto> listFeedbackDto =  listFeedback.stream().map(entity -> new FeedbackDto(entity)).collect(Collectors.toList());
        return listFeedbackDto;
    }

    private String generateCode() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}
