package com.andreamarino.pharmazon.service.implementationClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Booking;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.ServiceClass;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.ServiceRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.service.interfaceForClass.ServiceService;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService{

    @Autowired
    private final ServiceRepository serviceRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final EmailService emailService;

    @Override
    @Transactional
    public ServiceDto insertServiceDto(ServiceDto serviceDto, String username) { 
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        this.checkService(serviceDto);
        Pharmacist pharmacist = (Pharmacist) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        if(serviceRepository.findByCode(serviceDto.getCode()).isPresent()){
            throw new IllegalArgumentException("Il nuovo codice relativo al servizio e' gia' associato ad un altro servizio.");
        }

        List<ServiceClass> listServiceDb =  serviceRepository.findByNameList(serviceDto.getName());

        //Controllo, nel caso di nome del servizio uguale, se le date sono uguali 
        if(!listServiceDb.isEmpty()){
            for(ServiceClass serviceDb :listServiceDb){
                if (serviceDb.getDateChosen().toLocalDate().equals(serviceDto.getDateChosen().toLocalDate())) {
                    Duration duration = Duration.between(serviceDb.getDateChosen(), serviceDto.getDateChosen());
                    long hours = Math.abs(duration.toHours());
            
                    if (hours < 1) {
                        throw new IllegalArgumentException("Il nome e' gia' associato ad un altro servizio per questo giorno e orario inseriti.\nModifica l' orario di almeno un'ora se vuoi mantenere questo nome.");
                    }
                }
            }
        }

        ServiceClass service = new ServiceClass(serviceDto);
        service.setPharmacist(pharmacist);
        return new ServiceDto(serviceRepository.save(service));
    }

    private void checkService(ServiceDto serviceDto) { 
        if(serviceDto == null){
            throw new NotFoundException("Il service inserito non puo' essere nullo.");
        }

        ValidationUtils.validateString(serviceDto.getCode(), "codice servizio");
        if(serviceDto.getCode().length() > 40){
            throw new IllegalArgumentException("Il codice del servizio puo' avere massimo 40 caratteri.");
        }

        ValidationUtils.isValidBase64(serviceDto.getImage());

        if(serviceDto.getDateChosen() == null){
            throw new IllegalArgumentException("La data fornita non puo' essere nulla.");
        }

        ValidationUtils.checkDateEarly(serviceDto.getDateChosen().toString());
        
        LocalDateTime now = LocalDateTime.now();

        if (!serviceDto.getDateChosen().isAfter(now.plusHours(1))) {
            throw new IllegalArgumentException("La data e l'ora fornite devono essere almeno un'ora più avanti rispetto alla data e all'ora attuale.");
        } 

        now = now.plusYears(2);

        if (serviceDto.getDateChosen().isAfter(now)) {
            throw new IllegalArgumentException("La data fornita non puo' superare i 2 anni rispetto alla data attuale.");
        }
  
        if(serviceDto.getName() == null || serviceDto.getName().isBlank()){
            throw new IllegalArgumentException("Il nome del servizio inserito non puo' essere nullo o vuoto.");
        }

        //Rimuovo spazio iniziale dalla stringa
        if (serviceDto.getName().startsWith(" ")) {
            serviceDto.setName(serviceDto.getName().substring(1));
        }

        if(serviceDto.getName().length() > 40){
            throw new IllegalArgumentException("Il nome del servizio puo' avere massimo 40 caratteri.");
        }

        if(serviceDto.getDescription() == null || serviceDto.getDescription().isBlank()){
            throw new IllegalArgumentException("La descrizione del servizio inserito non puo' essere nulla o vuota.");
        }

        //Rimuovo spazio iniziale dalla stringa
        if (serviceDto.getDescription().startsWith(" ")) {
            serviceDto.setDescription(serviceDto.getDescription().substring(1));
        }

        if(serviceDto.getDescription().length() > 100){
            throw new IllegalArgumentException("La descrizione del servizio puo' avere massimo 100 caratteri.");
        }
        
        if(serviceDto.getPrice() == null || serviceDto.getPrice() <= 0){
            throw new IllegalArgumentException("Il prezzo del prodotto inserito non puo' essere nullo o <= 0.");
        }

        if(serviceDto.getAvailability() == null || serviceDto.getAvailability() < 0){
            throw new IllegalArgumentException("La disponibilita' del prodotto inserita non puo' essere nullo o < 0.");
        }
    }

    @Override
    @Transactional
    public ServiceDto updateServiceDto(ServiceDto serviceDtoNew, String oldCode, UserDetails userDetails) {
        String username = userDetails.getUsername();
        boolean flag = false;

        if(oldCode == null || oldCode.isEmpty()){
            throw new NotFoundException("Il codice inserito e' nullo o vuoto.");
        }

        this.checkService(serviceDtoNew);
        
        ServiceClass serviceOld = serviceRepository.findByCode(oldCode)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun servizio con questo codice: " + oldCode));

        Pharmacist pharmacist = (Pharmacist) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + userDetails.getUsername()));
        serviceOld.setPharmacist(pharmacist);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String nameService = serviceOld.getName();
        String date = serviceOld.getDateChosen().format(formatter);

        if(!serviceDtoNew.getCode().equals(serviceOld.getCode())){
            if(serviceRepository.findByCode(serviceDtoNew.getCode()).isPresent()){
                throw new IllegalArgumentException("Il nuovo codice relativo al servizio e' gia' associato ad un altro servizio.");
            }
            serviceOld.setCode(serviceDtoNew.getCode());
        }

        List<ServiceClass> listServiceDb =  serviceRepository.findByNameList(serviceDtoNew.getName());

        //Controllo, nel caso di nome del servizio uguale, se le date sono uguali 
        if(!listServiceDb.isEmpty()){
            for(ServiceClass serviceDb :listServiceDb){
                if ((serviceDb.getDateChosen().toLocalDate().equals(serviceDtoNew.getDateChosen().toLocalDate())) && (serviceDb.getId() != serviceOld.getId())) {
                    Duration duration = Duration.between(serviceDb.getDateChosen(), serviceDtoNew.getDateChosen());
                    long hours = Math.abs(duration.toHours());
            
                    if (hours < 1) {
                        throw new IllegalArgumentException("Il nome e' gia' associato ad un altro servizio per questo giorno e orario inseriti.\nModifica l' orario di almeno un'ora se vuoi mantenere questo nome.");
                    }
                }
            }
        }

        if(!serviceDtoNew.getName().equals(serviceOld.getName())){
            flag = true;
            serviceOld.setName(serviceDtoNew.getName());    
        }

        if(!serviceOld.getDescription().equals(serviceDtoNew.getDescription())){
            flag = true;
            serviceOld.setDescription(serviceDtoNew.getDescription());
        }

        if(!serviceOld.getAvailability().equals(serviceDtoNew.getAvailability())){
            flag = true;
            serviceOld.setAvailability(serviceDtoNew.getAvailability());
        }

        if(!serviceOld.getPrice().equals(serviceDtoNew.getPrice())){
            flag = true;
            serviceOld.setPrice(serviceDtoNew.getPrice());
        }

        if(!serviceOld.getImage().equals(serviceDtoNew.getImage())){
            serviceOld.setImage(serviceDtoNew.getImage());
        }

        if(!serviceOld.getDateChosen().equals(serviceDtoNew.getDateChosen())){
            flag = true;
            serviceOld.setDateChosen(serviceDtoNew.getDateChosen());
        }

        if(flag){
            //Pattern observer
            serviceOld.getBookings().stream()
            .map(Booking::getClient)
            .forEach(serviceOld::addObserver);
            serviceOld.addObserver(serviceOld.getPharmacist());
            serviceOld.notifyObservers(emailService, nameService, date);
            serviceOld.getBookings().clear();
        }
        serviceRepository.save(serviceOld);
        return new ServiceDto(serviceOld);
    }

    @Override
    public List<ServiceDto> getServiceListDto(String username) {      
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        List<ServiceClass> listServices = new ArrayList<>();
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        if(user.getRole().equals(Role.ADMIN)){
            listServices = serviceRepository.findAll();
          }
          else{
            listServices = serviceRepository.findByDateChosenAfter(LocalDateTime.now());
          }
        
        if (listServices.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<ServiceDto> listServiceDto = listServices.stream().map(entity -> new ServiceDto(entity)).collect(Collectors.toList());
        return listServiceDto;
    }

    @Override
    public ServiceDto getServiceDto(String code) {
        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice inserito e' nullo o vuoto.");
        }

        ServiceClass service = serviceRepository.findByCode(code)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun servizio con questo codice: " + code));
        
        ServiceDto serviceDto = new ServiceDto(service);
        return serviceDto;
    }

    @Override
    public List<ServiceDto> getServiceListDtoName(String username, String name) {
        List<ServiceClass> listServices = null;

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        if(name == null){
            throw new NotFoundException("Il nome del servizio e' nullo.");
        }

        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        if(user.getRole().equals(Role.ADMIN)){
            listServices = serviceRepository.findByNameList(name);
          }
          else{
            listServices = serviceRepository.findByNameListAndDateChosenAfter(name, LocalDateTime.now());
          }
        
        if (listServices.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<ServiceDto> listServiceDto = listServices.stream().map(entity -> new ServiceDto(entity)).collect(Collectors.toList());
        return listServiceDto;
    }

    @Override
    @Transactional
    public void deleteServiceDto(String code) { 
        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice inserito e' nullo o vuoto.");
        }

        ServiceClass service = serviceRepository.findByCode(code)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun servizio con questo codice."));
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String nameService = service.getName();
        String date = service.getDateChosen().format(formatter);

        //Pattern observer
        if(!service.getDateChosen().isBefore(now)){
            service.getBookings().stream()
            .map(Booking::getClient)
            .forEach(service::addObserver);

            service.addObserver(service.getPharmacist());
            service.notifyObservers(emailService, nameService, date);
        }
        serviceRepository.delete(service);
    }
}
