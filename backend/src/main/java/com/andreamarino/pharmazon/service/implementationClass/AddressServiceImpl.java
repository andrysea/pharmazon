package com.andreamarino.pharmazon.service.implementationClass;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.AddressDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Address;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.repository.AddressRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.service.interfaceForClass.AddressService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
    
    @Autowired
    private final AddressRepository addressRepository;

    @Autowired
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public AddressDto insertAddressDto(AddressDto addressDto, String username) {
        if(addressDto == null){
            throw new NotFoundException("L'oggetto relativo all'indirizzo non può essere nullo.");
        }

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username non puo' essere nullo o vuoto.");
        }

        Client client = (Client) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));
        
        this.checkAddressDto(addressDto);
        Address address = new Address(addressDto);
        address.setCode(this.generateCode());
        address.setClient(client);
        return new AddressDto(addressRepository.save(address));
    }

    private String generateCode() { 
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    } 

    private void checkAddressDto(AddressDto addressDto){
        ValidationUtils.containsOnlyLetters(addressDto.getName(), "nome utente");
        ValidationUtils.containsOnlyLetters(addressDto.getSurname(), "cognome utente"); 
        
        if(addressDto.getProvince() == null || addressDto.getProvince().isBlank()){
            throw new IllegalArgumentException("Il valore della provincia inserita non puo' essere nulla o vuoto.");
        }
        if(addressDto.getProvince().length() > 20){
            throw new IllegalArgumentException("La provincia inserita puo' avere massimo una lunghezza di 20 caratteri.");
        }
        
        ValidationUtils.containsOnlyNumbers(addressDto.getNumber(), "numero di telefono");
        if(addressDto.getNumber().length() != 10){
            throw new IllegalArgumentException("Il numero di telefono inserito ha una quantita' di caratteri diversa da 10.");
        }

        if(addressDto.getAddress() == null || addressDto.getAddress().isBlank()){
            throw new IllegalArgumentException("Il valore dell'indirizzo inserito non puo' essere nullo o vuoto.");
        }

        if(addressDto.getAddress().length() > 50){
            throw new IllegalArgumentException("L'indirizzo inserito puo' avere massimo una lunghezza di 50 caratteri.");
        }

        ValidationUtils.containsOnlyNumbers(addressDto.getCap(), "CAP");
        if(addressDto.getCap().length() != 5){
            throw new IllegalArgumentException("Il CAP inserito ha una quantita' di caratteri diversa da 5.");
        }

        if(addressDto.getCity() == null || addressDto.getCity().isBlank()){
            throw new IllegalArgumentException("Il valore della citta' inserita non puo' essere nulla o vuoto.");
        }
        
        if(addressDto.getCity().length() > 40){
            throw new IllegalArgumentException("La citta' inserita puo' avere massimo una lunghezza di 40 caratteri.");
        }
    }

    @Override
    public List<AddressDto> getAddressDto(String username) {
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username non puo' essere nullo o vuoto.");
        }

        List<Address> listAddress = addressRepository.findAllByUsername(username);
        if (listAddress.isEmpty()) {
            throw new NoSuchElementException();
        }
        
        List<AddressDto> listAddressDto = listAddress.stream().map(entity -> new AddressDto(entity)).collect(Collectors.toList());
        return listAddressDto;
    }

    @Override
    @Transactional
    public AddressDto updateAddressDto(AddressDto addressDto) {  
        if(addressDto == null){
            throw new NotFoundException("L'oggetto relativo all'indirizzo non può essere nullo.");
        }     

        if(addressDto.getCode() == null || addressDto.getCode().isEmpty()){
            throw new NotFoundException("Il codice dell'indirizzo non puo' essere nullo o vuoto.");
        }
         
        Address address = addressRepository.findByCode(addressDto.getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun indirizzo con questo codice: " + addressDto.getCode()));

        if(!address.isActive()){
            throw new IllegalStateException("L'indirizzo è disattivato.");
        }
      
        this.checkAddressDto(addressDto);
        address = this.updateAddress(address, addressDto);
        return new AddressDto(addressRepository.save(address));
    }

    private Address updateAddress(Address address, AddressDto addressDto){
        address.setName(addressDto.getName());
        address.setSurname(addressDto.getSurname());
        address.setNumber(addressDto.getNumber());
        address.setAddress(addressDto.getAddress());
        address.setCap(addressDto.getCap());
        address.setCity(addressDto.getCity());
        address.setProvince(addressDto.getProvince());
        return address;
    }

    @Override
    @Transactional
    public void deactivateAddressDto(String code) {
        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice non puo' essere nullo o vuoto.");
        }

        Address address = addressRepository.findByCode(code)
        .orElseThrow(() ->  new NotFoundException("Non e' stato trovato nessun indirizzo con il codice fornito."));
        
        if(address.isActive()){
            address.setActive(false);
            addressRepository.save(address);
        }
        else{
            throw new IllegalStateException("Indirizzo gia' disattivato.");
        }
    }
}
