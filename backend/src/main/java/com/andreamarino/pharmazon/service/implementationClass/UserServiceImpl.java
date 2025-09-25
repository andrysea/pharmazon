package com.andreamarino.pharmazon.service.implementationClass;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.BookingRepository;
import com.andreamarino.pharmazon.repository.OrderRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import com.andreamarino.pharmazon.security.user.ChangePasswordRequest;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.service.interfaceForClass.UserService;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    @Autowired
    private final UserRepository userRepository;
    
    @Autowired
    private final EmailService emailService;

    @Autowired 
    private final BookingRepository bookingRepository;

    @Autowired 
    private final OrderRepository orderRepository;
    
    @Override
    @Transactional
    public UserDto updateUserDto(UserDto userDtoNew, String username) {
        if(userDtoNew == null){
            throw new NotFoundException("L'oggetto relativo all'utente e' nullo.");
        }

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }

        User userOld = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));

        this.updateNewUser(userOld, userDtoNew);
        return new UserDto(userRepository.save(userOld));
    }

    private void updateNewUser(User userOld, UserDto userDtoNew){    
        if (ValidationUtils.validateString(userDtoNew.getUsername(), "username")) {
            if (userDtoNew.getUsername().chars().anyMatch(Character::isUpperCase)) {
                throw new IllegalArgumentException("Lo username inserito non puo' contenere caratteri maiuscoli.");
            }
            if(!userDtoNew.getUsername().equals(userOld.getUsername())){
                if(!userRepository.findByUsername(userDtoNew.getUsername()).isEmpty()){
                    throw new DuplicateException("Lo username inserito non e' disponibile.");
                }
                userOld.setUsername(userDtoNew.getUsername());
            }
        }
        
        if (ValidationUtils.containsOnlyLetters(userDtoNew.getName(), "nome utente")) {
            userOld.setName(userDtoNew.getName());
        }
        
        if (ValidationUtils.containsOnlyLetters(userDtoNew.getSurname(), "cognome utente")) {
            userOld.setSurname(userDtoNew.getSurname());
        }

        if (ValidationUtils.isValidEmail(userDtoNew.getEmail())) {
            if(!userDtoNew.getEmail().equals(userOld.getEmail())){
                if(!userRepository.findByEmail(userDtoNew.getEmail()).isEmpty()){
                    throw new DuplicateException("La email inserita non e' disponibile.");
                }
                userOld.setEmail(userDtoNew.getEmail());
            }
        }
        
        if (ValidationUtils.checkAdult(userDtoNew.getBirthDate())) {
            userOld.setBirthDate(userDtoNew.getBirthDate());
        }
        
        if (ValidationUtils.checkTaxId(userDtoNew.getTaxId())) {  
            if(!userDtoNew.getTaxId().equals(userOld.getTaxId())){
                if(!userRepository.findByTaxId(userDtoNew.getTaxId()).isEmpty()){
                    throw new IllegalArgumentException("Il codice fiscale inserito e' gia' utilizzato.");
                }
                userOld.setTaxId(userDtoNew.getTaxId());
            }
        }
        
        if (ValidationUtils.containsOnlyNumbers(userDtoNew.getNumber(), "numero di telefono")) {
            if(!userDtoNew.getNumber().equals(userOld.getNumber())){
                if(!userRepository.findByNumber(userDtoNew.getNumber()).isEmpty()){
                    throw new DuplicateException("Il numero di telefono inserito non e' disponibile.");
                }
                if (userDtoNew.getNumber().length() != 10) {
                    throw new IllegalArgumentException("Il numero inserito deve avere una lunghezza di 10 cifre.");
                }
                userOld.setNumber(userDtoNew.getNumber());
            }
        }
    }

    @Override
    @Transactional
    public void sendEmail(String email) {
        if(email == null || email.isEmpty()){
            throw new NotFoundException("La email inserita non puo' essere nulla o vuota.");
        }

        String newPassword = this.generateCode();
        ValidationUtils.isValidEmail(email);

        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente associato a questa email: " + email));   
        user.setPassword(newPassword);
        
        userRepository.save(user);
        emailService.sendSimpleEmail(email, "Modifica Password." , "Ciao, ecco la tua nuova password: " + newPassword);
    }

    @Override
    public UserDto findByUsername(String username) {    
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));
        UserDto userDto = new UserDto(user);
        return userDto;
    }

    @Override
    public List<UserDto> getUserListDto() {    
       List<User> listUser = userRepository.findByRole(Role.CLIENT);
        if (listUser.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<UserDto> listUserDto = listUser.stream().map(entity -> new UserDto(entity)).collect(Collectors.toList());
        return listUserDto;
    }

    @Transactional
    @Override
    public void updatePassword(ChangePasswordRequest changePasswordRequest, UserDetails userDetails) {

        if(changePasswordRequest.getCurrentPassword() == null || changePasswordRequest.getCurrentPassword().isEmpty()){
            throw new IllegalArgumentException("La vecchia password inserita non puo' essere nulla o vuota.");
        }

        if(changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().isEmpty()){
            throw new IllegalArgumentException("La password nuova inserita non puo' essere nulla o vuota.");
        }

        if(changePasswordRequest.getConfirmationPassword() == null || changePasswordRequest.getConfirmationPassword().isEmpty()){
            throw new IllegalArgumentException("La password nuova ripetuta inserita non puo' essere nulla o vuota.");
        }

        if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmationPassword())){
            throw new IllegalArgumentException("La password nuova e la password nuova ripetuta, non sono uguali.");
        }

        User user = (User) userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + userDetails.getUsername()));

        if(!changePasswordRequest.getCurrentPassword().equals(user.getPassword())){
            throw new IllegalArgumentException("La vecchia password inserita non e' corretta.");
        }

        if(changePasswordRequest.getNewPassword().equals(user.getPassword())){
            throw new IllegalArgumentException("La password nuova inserita e' uguale a quella già presente.");
        }

        if(changePasswordRequest.getNewPassword().length() < 8 || changePasswordRequest.getNewPassword().length() > 20){
            throw new IllegalArgumentException("La password inserita deve avere minimo 8 caratteri e massimo 20 caratteri.");
        }
        
        ValidationUtils.validateString(changePasswordRequest.getNewPassword(), "password");
        user.setPassword(changePasswordRequest.getNewPassword());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserDto(String username) {      
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
        }

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("Username inserito non corrisponde a nessun utente."));
        
        if(!user.getRole().equals(Role.CLIENT)){
            throw new IllegalArgumentException("L'utente da eliminare deve avere il ruolo di CLIENT.");
        }

        if(bookingRepository.existsByClientUsernameAndAcceptedFalse(username)){
            throw new IllegalStateException("La presenza di prenotazioni non accettate, impedisce l'eliminazione dell'account.");
        }
        
        StateWaitingApproval state = new StateWaitingApproval();
        if(orderRepository.existsByClientUsernameAndStateId(username, state.getId())){
            throw new IllegalStateException("La presenza di ordini in attesa di approvazione, impedisce l'eliminazione dell'account.");
        }

        userRepository.deleteById(user.getId());
        emailService.sendSimpleEmail(user.getEmail(), "Eliminazione utenza." , "Ciao, l'account associato al tuo username " + username + ", e' stato cancellato.\n Per ricevere aggiornamenti sulle prenotazioni accettate e sullo stato degli ordini non consegnati, rivolgiti alla farmacia.");
    }

    private String generateCode() { 
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    } 
}
