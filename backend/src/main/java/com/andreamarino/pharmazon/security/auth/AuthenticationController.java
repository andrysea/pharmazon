package com.andreamarino.pharmazon.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.security.user.ChangePasswordRequest;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class AuthenticationController {
  private final AuthenticationService service;

   /**
   * Gestisce la richiesta HTTP POST per registrare un nuovo utente.
   * 
   * @param request l'oggetto UserDto contenente le informazioni dell'utente da registrare
   * @return una ResponseEntity contenente un messaggio di conferma se la registrazione è avvenuta con successo
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserDto request) {
    UserDto userDtoInsert = service.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Registrazione dell'utente avvenuta con successo.", "userDto", userDtoInsert));
  }
  

  /**
   * Gestisce la richiesta HTTP POST per autenticare un utente.
   * 
   * @param request l'oggetto AuthenticationRequest contenente le credenziali di accesso dell'utente
   * @return una ResponseEntity contenente un messaggio di conferma se l'autenticazione è avvenuta con successo
   */
  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
    AuthenticationResponse token = service.authenticate(request);
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Autenticazione dell'utente avvenuta con successo.", "token", token));
  }


  /**
   * Gestisce la richiesta HTTP GET per verificare la validità di un token.
   * 
   * @param username l'username dell'utente di cui si vuole verificare il token
   * @return una ResponseEntity contenente un messaggio di conferma se il token è valido
   */
  @GetMapping("/checkToken")
  public ResponseEntity<?> checkToken(@RequestParam String username) {
    service.checkToken(username);
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Token valido."));
  }


  /**
   * Gestisce la richiesta HTTP PUT per revocare un token.
   * 
   * @param username l'username dell'utente di cui si vuole revocare il token
   * @return una ResponseEntity contenente un messaggio di conferma se il token è stato revocato
   */
  @PutMapping("/revokeToken")
  public ResponseEntity<?> revokeToken(@RequestParam String username) {
    service.revokeToken(username);
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Token revocati."));
  }
}
