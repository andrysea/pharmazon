package com.andreamarino.pharmazon.service.implementationClass;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.CreditCard;
import com.andreamarino.pharmazon.repository.CreditCardRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.service.interfaceForClass.CreditCardService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private final CreditCardRepository creditCardRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CreditCardDto insertCreditCardDto(CreditCardDto creditCardDto, String username) {
        if(creditCardDto == null){
            throw new NotFoundException("L'oggetto relativo alla carta di credito non può essere nullo.");
        }

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username non puo' essere nullo o vuoto.");
        }

        ValidationUtils.containsOnlyLetters(creditCardDto.getName(), "nome");
        ValidationUtils.containsOnlyLetters(creditCardDto.getSurname(), "cognome");
        ValidationUtils.isValidCreditCard(creditCardDto.getNumber(), creditCardDto.getCardSecurityCode(), creditCardDto.getExpirationDate());
        
        Client client = (Client) this.userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        List<CreditCard> listCreditCard = client.getCreditCards();

        //Verifico se è già presente una carta di credito 
        //con il numero a 16 cifre inserito
        for(CreditCard creditCard: listCreditCard){
            if(creditCardDto.getNumber().equals(creditCard.getNumber()) && creditCard.isActive() == true){
                throw new DuplicateException("Il numero a 16 cifre inserito, appartiene ad un'altra carta di credito registrata.");
            }
        }

        CreditCard creditCard = creditCardRepository.findByNumber(creditCardDto.getNumber()).
        orElse(null);

        if(creditCard == null){
            CreditCard creditCardSaved = new CreditCard(creditCardDto);
            creditCardSaved.setBalance(100.0);
            creditCardSaved.setClient(client);
            return new CreditCardDto(creditCardRepository.save(creditCardSaved));
        }
        else {
            if(creditCard.isActive()){
                throw new IllegalArgumentException("La carta di credito e' già registrata con un altro utente.\nPer utilizzarla l'utente deve rimuoverla dal suo account.");
            }
            else{
                creditCard.setCardSecurityCode(creditCardDto.getCardSecurityCode());
                creditCard.setExpirationDate(creditCardDto.getExpirationDate());
                creditCard.setName((creditCardDto.getName()));
                creditCard.setSurname((creditCardDto.getSurname()));
                
                creditCard.setClient(client);
                creditCard.setActive(true);
                return new CreditCardDto(creditCardRepository.save(creditCard));
            }
        }
    }

    @Override
    @Transactional
    public CreditCardDto updateCreditCardDto(CreditCardDto creditCardDto, String username) {
        if(creditCardDto == null){
            throw new NotFoundException("L'oggetto relativo alla carta di credito non può essere nullo.");
        }

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username non puo' essere nullo o vuoto.");
        }

        ValidationUtils.containsOnlyLetters(creditCardDto.getName(), "nome");
        ValidationUtils.containsOnlyLetters(creditCardDto.getSurname(), "cognome");
        ValidationUtils.isValidCreditCard(creditCardDto.getNumber(), creditCardDto.getCardSecurityCode(), creditCardDto.getExpirationDate());

        Client client = (Client) this.userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        List<CreditCard> listCreditCard = client.getCreditCards();

        if(listCreditCard.isEmpty()){
            throw new NoSuchElementException();
        }
        
        for(CreditCard creditCard: listCreditCard){            
            if(creditCardDto.getNumber().equals(creditCard.getNumber())){
                creditCard.setName(creditCardDto.getName());
                creditCard.setSurname(creditCardDto.getSurname());
                creditCard.setExpirationDate(creditCardDto.getExpirationDate());
                return new CreditCardDto(creditCardRepository.save(creditCard));
            }
        }
        throw new NotFoundException("Non e' stata trovata nessuna carta di credito.");
    }

    @Override
    @Transactional
    public void deactivateCreditCardDto(String number, String username) {
        if(number == null || number.isEmpty()){
            throw new NotFoundException("Il numero non puo' essere nullo o vuoto.");
        }

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username non puo' essere nullo o vuoto.");
        }

        Client client = (Client) this.userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        List<CreditCard> listCreditCard = client.getCreditCards();

        for(CreditCard creditCard: listCreditCard){
            if(number.equals(creditCard.getNumber())){
                if(creditCard.isActive()){
                    creditCard.setActive(false);
                    creditCardRepository.save(creditCard);
                }
                else{
                    throw new IllegalStateException("Carta di credito gia' disattivata.");
                }
                return;
            }
        }
        throw new NotFoundException("Non e' stata trovata nessuna carta di credito.");
    }

    @Override
    public List<CreditCardDto> getCreditCardDto(String username) {
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username non puo' essere nullo o vuoto.");
        }

        List<CreditCard> listCreditCard = creditCardRepository.findByUsername(username);
        if (listCreditCard.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            for (int i = 0; i < listCreditCard.size(); i++) {
                listCreditCard.get(i).setBalance(null);
            }
        }
        return listCreditCard.stream().map(entity -> new CreditCardDto(entity)).collect(Collectors.toList());
    }
}
