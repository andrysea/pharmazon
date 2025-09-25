package com.andreamarino.pharmazon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.security.user.ChangePasswordRequest;
import com.andreamarino.pharmazon.service.interfaceForClass.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "https://localhost:4200")
public class UserController {
    private final UserService userService;

    /**
     * Gestisce la richiesta HTTP PUT per aggiornare le informazioni dell'utente.
     * 
     * @param userDto le informazioni aggiornate dell'utente
     * @param username l'username dell'utente da aggiornare
     * @param userDetails le informazioni dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di conferma se l'aggiornamento è avvenuto con successo o un messaggio di errore se l'username non corrisponde all'utente autenticato
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUserDto(@RequestBody(required = true) UserDto userDto, @RequestParam(required = true) String username, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails.getUsername().equals(username)){
            UserDto userDtoUpdated = userService.updateUserDto(userDto, username);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica dell'utente avvenuta con successo.", "userDto", userDtoUpdated));
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Username inserito come parametro, non e' conforme all'username legato al token."));
        }
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista degli utenti.
     * 
     * @return una ResponseEntity contenente la lista degli oggetti di tipo UserDto, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getUserListDto(){
        List<UserDto> listUserDto = new ArrayList<>();
        listUserDto = userService.getUserListDto();
        return ResponseEntity.status(HttpStatus.OK).body(listUserDto);
    }


    /**
     * Gestisce la richiesta HTTP PUT per inviare un'email di cambio password.
     * 
     * @param email l'email dell'utente per il quale si desidera cambiare la password
     * @return una ResponseEntity contenente un messaggio di conferma se l'email è stata inviata con successo
     */
    @PutMapping("/sendEmail")
    public ResponseEntity<?> changePassword(@RequestParam(required = true) String email) {
        userService.sendEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica della password avvenuta con successo."));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere le informazioni di un utente specifico.
     * 
     * @param username l'username dell'utente di cui si vogliono ottenere le informazioni
     * @return una ResponseEntity contenente un oggetto UserDto corrispondente all'utente specificato, con uno stato di OK (200)
     */
    @GetMapping("/getUser")
    public ResponseEntity<?> getUserDto(@RequestParam(required = true) String username){
        UserDto userDto = userService.findByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare la password di un utente.
     * 
     * @param changePasswordRequest le nuove credenziali dell'utente
     * @param userDetails le informazioni dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di conferma se la password è stata aggiornata con successo
     */
    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest, @AuthenticationPrincipal UserDetails userDetails) {
        userService.updatePassword(changePasswordRequest, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica della password utente avvenuta con successo."));
    }


    /**
     * Gestisce la richiesta HTTP DELETE per eliminare un utente.
     * 
     * @param username l'username dell'utente da eliminare
     * @param userDetails le informazioni dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di conferma se l'eliminazione dell'utente è avvenuta con successo
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserDto(@RequestParam(required = true) String username, @AuthenticationPrincipal UserDetails userDetails){
        userService.deleteUserDto(username);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Eliminazione dell'utente avvenuta con successo."));
    }
}