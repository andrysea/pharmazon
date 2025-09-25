package com.andreamarino.pharmazon.security.auth;

import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.ClientRepository;
import com.andreamarino.pharmazon.repository.PharmacistRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.config.JwtService;
import com.andreamarino.pharmazon.security.token.Token;
import com.andreamarino.pharmazon.security.token.TokenRepository;
import com.andreamarino.pharmazon.security.token.TokenType;
import com.andreamarino.pharmazon.security.user.*;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final ClientRepository clientRepository;
  private final PharmacistRepository pharmacistRepository;
  private final TokenRepository tokenRepository;
  private final JwtService jwtService;

  private void validateUser(UserDto userDto){
    ValidationUtils.containsOnlyLetters(userDto.getName(), "nome utente");
    ValidationUtils.containsOnlyLetters(userDto.getSurname(), "cognome utente");
    ValidationUtils.isValidEmail(userDto.getEmail());
    ValidationUtils.checkTaxId(userDto.getTaxId());
    ValidationUtils.checkAdult(userDto.getBirthDate());
    
    ValidationUtils.validateString(userDto.getUsername(), "username");
    if (userDto.getUsername().chars().anyMatch(Character::isUpperCase)) {
      throw new IllegalArgumentException("Lo username inserito non puo' contenere caratteri maiuscoli.");
    }
    if(userDto.getUsername().length() > 20){
      throw new IllegalArgumentException("Lo username inserito non deve avere una lunghezza maggiore di 20 caratteri.");
    }

    ValidationUtils.containsOnlyNumbers(userDto.getNumber(), "numero di telefono");
    if(userDto.getNumber().length() != 10){
      throw new IllegalArgumentException("Il numero di telefono inserito ha una quantita' di caratteri diversa da 10.");
    }

    ValidationUtils.validateString(userDto.getPassword(), "password");
    if(userDto.getPassword().length() < 8 || userDto.getPassword().length() > 20){
      throw new IllegalArgumentException("La password inserita deve avere minimo 8 caratteri e massimo 20 caratteri.");
    }
    ValidationUtils.isValidRole(userDto.getRole().name());
  }

  @Transactional
  public UserDto register(UserDto userDto) {
    this.validateUser(userDto);
    userDto.setTaxId(userDto.getTaxId().toUpperCase());

    var user = User.builder()
        .name(userDto.getName())
        .surname(userDto.getSurname())
        .username(userDto.getUsername())
        .email(userDto.getEmail())
        .taxId(userDto.getTaxId())
        .birthDate(userDto.getBirthDate())
        .password(userDto.getPassword())
        .number(userDto.getNumber())
        .role(userDto.getRole())
        .build();

    User savedUser = null;

    if(!userRepository.findByUsername(userDto.getUsername()).isEmpty()){
      throw new DuplicateException("Lo username inserito non e' disponibile.");
    }
    if(!userRepository.findByEmail(userDto.getEmail()).isEmpty()){
      throw new DuplicateException("La email inserita non e' disponibile.");
    }
    if(!userRepository.findByTaxId(userDto.getTaxId()).isEmpty()){
      throw new DuplicateException("Il codice fiscale inserito e' gia' utilizzato.");
    }
    if(!userRepository.findByNumber(userDto.getNumber()).isEmpty()){
      throw new DuplicateException("Il numero di telefono inserito non e' disponibile.");
    }
    
    if(user.getRole().equals(Role.ADMIN)){
      savedUser = pharmacistRepository.save(new Pharmacist(user));
    }
    else{
      savedUser = clientRepository.save(new Client(user));
    }

    var jwtToken = jwtService.generateToken(user);
    saveUserToken(savedUser, jwtToken);

    return new UserDto(savedUser);
  }

  @Transactional
  public AuthenticationResponse authenticate(AuthenticationRequest request) { 
    if(request.getUsername() == null || request.getUsername().isEmpty()){
      throw new IllegalArgumentException("Lo username inserito non puo' essere nullo o vuoto.");
    }

    if(request.getUsername().chars().anyMatch(Character::isUpperCase)){
      throw new IllegalArgumentException("Caratteri maiuscoli, per username, non permessi.");
    }

    if(request.getPassword() == null || request.getPassword().isEmpty()){
      throw new IllegalArgumentException("La password inserita non puo' essere nulla o vuota.");
    }
    
    var user = userRepository.findByUsername(request.getUsername())
    .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + request.getUsername()));
    
    if(!user.getPassword().equals(request.getPassword())){
      throw new IllegalArgumentException("Credenziali inserite non corrette.");
    }
    
    var jwtToken = jwtService.generateToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .build();
  }

  public void checkToken(String username){
    if(username == null || username.isEmpty()){
      throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
    }

    tokenRepository.lastToken(username)
    .orElseThrow(() -> new IllegalArgumentException("Non e' stato trovato nessun token associato ad un utente con questo username: " + username));
  }

  public void revokeToken(String username){
    if(username == null || username.isEmpty()){
      throw new NotFoundException("Lo username inserito non puo' essere nullo o vuoto.");
    }

    User user = userRepository.findByUsername(username)
    .orElseThrow(() -> new IllegalArgumentException("Non e' stato trovato nessun utente con questo username: " + username));
    revokeAllUserTokens(user);
  }

  // Creo un nuovo token
  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  // Revoco tutti i token
  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }
}
