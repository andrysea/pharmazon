package com.andreamarino.pharmazon.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private PharmacistRepository pharmacistRepository;
    
    @Mock
    private TokenRepository tokenRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    public void register_WhenValidInputClient_ReturnObject(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        Client client = new Client(user);

        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, client);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

        //Mock 
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByTaxId(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNumber(anyString())).thenReturn(Optional.empty());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token.getToken());
        
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            User userSaved = invocation.getArgument(0);
            return userSaved;
        });

        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> {
            Token tokenSaved = invocation.getArgument(0);
            return tokenSaved;
        });

        //Test
        UserDto userDtoInsert = authenticationService.register(userDto);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).findByTaxId(tax_id);
        verify(userRepository, times(1)).findByNumber(number);
        verify(clientRepository, times(1)).save(clientCaptor.capture());
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        tokenCaptor.getValue().setId(token.getId());
        assertEquals(token, tokenCaptor.getValue());
        assertEquals(userDto, userDtoInsert);
    }

    @Test
    public void register_WhenValidInputAdmin_ReturnObject(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        Pharmacist pharmacist = new Pharmacist(user);

        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, pharmacist);
        
        ArgumentCaptor<Pharmacist> pharmacistCaptor = ArgumentCaptor.forClass(Pharmacist.class);
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

        //Mock 
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByTaxId(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNumber(anyString())).thenReturn(Optional.empty());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token.getToken());
        
        when(pharmacistRepository.save(any(Pharmacist.class))).thenAnswer(invocation -> {
            User userSaved = invocation.getArgument(0);
            return userSaved;
        });

        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> {
            Token tokenSaved = invocation.getArgument(0);
            return tokenSaved;
        });

        //Test
        UserDto userDtoInsert = authenticationService.register(userDto);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).findByTaxId(tax_id);
        verify(userRepository, times(1)).findByNumber(number);
        verify(pharmacistRepository, times(1)).save(pharmacistCaptor.capture());
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        tokenCaptor.getValue().setId(token.getId());
        assertEquals(userDto, userDtoInsert);
    }

    @Test
    public void register_WhenValidInputNumberFind_DuplicateException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);

        //Mock 
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByTaxId(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNumber(anyString())).thenReturn(Optional.of(user));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class, () ->
        authenticationService.register(userDto));
        assertEquals("Il numero di telefono inserito non e' disponibile.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).findByTaxId(tax_id);
        verify(userRepository, times(1)).findByNumber(number);
    }

    @Test
    public void register_WhenValidInputTaxIdFind_DuplicateException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);

        //Mock 
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByTaxId(anyString())).thenReturn(Optional.of(user));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class, () ->
        authenticationService.register(userDto));
        assertEquals("Il codice fiscale inserito e' gia' utilizzato.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).findByTaxId(tax_id);
    }

    @Test
    public void register_WhenValidInputEmailFind_DuplicateException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);

        //Mock 
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class, () ->
        authenticationService.register(userDto));
        assertEquals("La email inserita non e' disponibile.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void register_WhenValidInputUsernameFind_DuplicateException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);

        //Mock 
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class, () ->
        authenticationService.register(userDto));
        assertEquals("Lo username inserito non e' disponibile.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void register_WhenInvalidInputUsernameUpperCase_IllegalArgumentException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "ANDRYSEA";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.register(userDto));
        assertEquals("Lo username inserito non puo' contenere caratteri maiuscoli.", exception.getMessage());
    }

    @Test
    public void register_WhenInvalidInputUsernameLength_IllegalArgumentException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "user123456789012345678";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.register(userDto));
        assertEquals("Lo username inserito non deve avere una lunghezza maggiore di 20 caratteri.", exception.getMessage());
    }

    @Test
    public void register_WhenInvalidInputNumber_IllegalArgumentException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "123456789";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.register(userDto));
        assertEquals("Il numero di telefono inserito ha una quantita' di caratteri diversa da 10.", exception.getMessage());
    }

    @Test
    public void register_WhenInvalidInputPasswordLengthLess8_IllegalArgumentException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "123456";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.register(userDto));
        assertEquals("La password inserita deve avere minimo 8 caratteri e massimo 20 caratteri.", exception.getMessage());
    }

    @Test
    public void register_WhenInvalidInputPasswordLengthMore20_IllegalArgumentException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "123456789012345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.ADMIN, name, surname, username, number, email, password, birthdate, tax_id);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.register(userDto));
        assertEquals("La password inserita deve avere minimo 8 caratteri e massimo 20 caratteri.", exception.getMessage());
    }

    @Test
    public void authenticate_WhenValidInputUserTokensNoEmpty_ReturnObject(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        user.setId(1L);

        AuthenticationRequest request = new AuthenticationRequest(username, password);
        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, user);
        
        List<Token> listToken = new ArrayList<>();
        listToken.add(token);
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token.getToken());
        when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(listToken);
        when(tokenRepository.saveAll(anyList())).thenReturn(listToken);

        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> {
            Token tokenSaved = invocation.getArgument(0);
            return tokenSaved;
        });

        //Test
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        assertEquals(authenticationResponse.getAccessToken(), token.getToken());

        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, times(1)).generateToken(user);
        verify(tokenRepository, times(1)).findAllValidTokenByUser(user.getId());
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
    }

    @Test
    public void authenticate_WhenValidInputUserTokensEmpty_ReturnObject(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        user.setId(1L);

        AuthenticationRequest request = new AuthenticationRequest(username, password);
        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, user);
        
        List<Token> listToken = new ArrayList<>();
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token.getToken());
        when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(listToken);

        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> {
            Token tokenSaved = invocation.getArgument(0);
            return tokenSaved;
        });

        //Test
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        assertEquals(authenticationResponse.getAccessToken(), token.getToken());

        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, times(1)).generateToken(user);
        verify(tokenRepository, times(1)).findAllValidTokenByUser(user.getId());
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
    }

    @Test
    public void authenticate_WhenValidInput_NotFoundException(){
        //Setup
        String username = "andrysea";
        AuthenticationRequest request = new AuthenticationRequest("andrysea", "12345678");
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        authenticationService.authenticate(request));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + request.getUsername(), exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void authenticate_WhenInvalidInputPasswordNull_IllegalArgumentException(){
        //Setup
        AuthenticationRequest request = new AuthenticationRequest("andrysea", null);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.authenticate(request));
        assertEquals("La password inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void authenticate_WhenInvalidInputPasswordEmpty_IllegalArgumentException(){
        //Setup
        AuthenticationRequest request = new AuthenticationRequest("andrysea", "");
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.authenticate(request));
        assertEquals("La password inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void authenticate_WhenInvalidInputUsernameNull_IllegalArgumentException(){
        //Setup
        AuthenticationRequest request = new AuthenticationRequest(null, "123");
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.authenticate(request));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void authenticate_WhenInvalidInputUsernameEmpty_IllegalArgumentException(){
        //Setup
        AuthenticationRequest request = new AuthenticationRequest("", "123");
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.authenticate(request));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void authenticate_WhenInvalidInputUsernameUpperCase_IllegalArgumentException(){
        //Setup
        AuthenticationRequest request = new AuthenticationRequest("ANDRYSEA", "123");
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.authenticate(request));
        assertEquals("Caratteri maiuscoli, per username, non permessi.", exception.getMessage());
    }

    @Test
    public void checkToken_WhenValidInput_Success(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        user.setId(1L);

        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, user);

        //Mock
        when(tokenRepository.lastToken(anyString())).thenReturn(Optional.of(token));

        //Test
        authenticationService.checkToken(username);
    }

    @Test
    public void checkToken_WhenValidInput_IllegalArgumentException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        user.setId(1L);

        //Mock
        when(tokenRepository.lastToken(anyString())).thenReturn(Optional.empty());

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.checkToken(username));
        assertEquals("Non e' stato trovato nessun token associato ad un utente con questo username: " + username, exception.getMessage());
    }

    @Test
    public void checkToken_WhenInvalidInputUsernameNull_IllegalArgumentException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        authenticationService.checkToken(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void checkToken_WhenInvalidInputUsernameEmpty_IllegalArgumentException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        authenticationService.checkToken(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void revokeToken_WhenValidInput_Success(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        user.setId(1L);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        authenticationService.revokeToken(username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());
    }

    @Test
    public void revokeToken_WhenValidInput_IllegalArgumentException(){
        //Setup
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "1234567890";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        User user = new User(userDto);
        user.setId(1L);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        //Test
        authenticationService.revokeToken(username);
    }

    @Test
    public void revokeToken_WhenInvalidInputUsernameNull_IllegalArgumentException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        authenticationService.revokeToken(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void revokeToken_WhenInvalidInputUsernameEmpty_IllegalArgumentException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        authenticationService.revokeToken(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

}
