package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.BookingRepository;
import com.andreamarino.pharmazon.repository.OrderRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.ChangePasswordRequest;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;

    @Mock 
    private BookingRepository bookingRepository;

    @Mock 
    private OrderRepository orderRepository;
    
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    public void updateUserDto_WhenValidInput_ReturnObject(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        userDto.setUsername("andryseea");
        userDto.setName("Franco");
        userDto.setSurname("Martucci");
        userDto.setEmail("user@userr.it");
        userDto.setBirthDate("2001-11-03");
        userDto.setTaxId("1234567890123457");
        userDto.setNumber("1234567891");
        userDto.setPassword("password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByTaxId(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNumber(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userSaved = invocation.getArgument(0);
            return userSaved;
        });

        //Test
        UserDto userDtoUpdated = userServiceImpl.updateUserDto(userDto, username);
        verify(userRepository, times(1)).findByUsername(userDto.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
        verify(userRepository, times(1)).findByTaxId(userDto.getTaxId());
        verify(userRepository, times(1)).findByNumber(userDto.getNumber());
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        assertEquals(user, userCaptor.getValue());
        assertEquals(userDtoUpdated, userDto);
    }

    @Test
    public void updateUserDto_WhenValidInputNothingToChangeBlank_ReturnObject(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userSaved = invocation.getArgument(0);
            return userSaved;
        });

        //Test
        userServiceImpl.updateUserDto(userDto, username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        user.setPassword(userCaptor.getValue().getPassword());
        assertEquals(user, userCaptor.getValue());
    }

    @Test
    public void updateUserDto_WhenInvalidInputNumberLength_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        userDto.setNumber("12345678901");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Il numero inserito deve avere una lunghezza di 10 cifre.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updateUserDto_WhenValidInputNumberFind_DuplicateException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        userDto.setNumber("12345678901");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByNumber(anyString())).thenReturn(Optional.of(user));

        //Test
        DuplicateException exception =  assertThrows(DuplicateException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Il numero di telefono inserito non e' disponibile.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByNumber(userDto.getNumber());
    }

    @Test
    public void updateUserDto_WhenValidInputTaxIdFind_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        userDto.setTaxId("1234567890123457");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByTaxId(anyString())).thenReturn(Optional.of(user));

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Il codice fiscale inserito e' gia' utilizzato.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByTaxId(userDto.getTaxId());
    }

    @Test
    public void updateUserDto_WhenValidInputEmailFind_DuplicateException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        userDto.setEmail("user@usernew.it");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //Test
        DuplicateException exception =  assertThrows(DuplicateException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("La email inserita non e' disponibile.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
    }

    @Test
    public void updateUserDto_WhenValidInputUsernameFind_DuplicateException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        userDto.setUsername("andryseaa");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));

        //Test
        DuplicateException exception =  assertThrows(DuplicateException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Lo username inserito non e' disponibile.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(userDto.getUsername());
    }

    @Test
    public void updateUserDto_WhenInvaliInputUsernameUpperCase_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        userDto.setUsername("ANDRYSEa");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Lo username inserito non puo' contenere caratteri maiuscoli.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updateUserDto_WhenValidInputNoClientFind_NotFoundException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updateUserDto_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateUserDto_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setName("Andrea");
        userDto.setSurname("Marino");
        userDto.setEmail("user@user.it");
        userDto.setBirthDate("2001-10-03");
        userDto.setTaxId("1234567890123456");
        userDto.setNumber("1234567890");
        userDto.setPassword("password");

        User user = new User(userDto);
        user.setId(1L);

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateUserDto_WhenInvalidInputUserDtoNull_NotFoundException(){
        //Setup
        String username = "andrysea";
        
        UserDto userDto = null;

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.updateUserDto(userDto, username));
        assertEquals("L'oggetto relativo all'utente e' nullo.", exception.getMessage());
    }

    @Test
    public void updatePassword_WhenValidInput_Success(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "password_nuova", "password_nuova");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userSaved = invocation.getArgument(0);
            return userSaved;
        });

        //Test
        userServiceImpl.updatePassword(changePasswordRequest, client);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        assertEquals(user, userCaptor.getValue());
    }

    @Test
    public void updatePassword_NewPasswordEqualToOldPassword_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "password", "password");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La password nuova inserita e' uguale a quella già presente.", exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updatePassword_OldPasswordNotCorrect_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "new_password", "new_password");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La vecchia password inserita non e' corretta.", exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updatePassword_whenInvalidUser_NotFoundException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "new_password", "new_password");

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + user.getUsername(), exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void updatePassword_InvalidNewPasswordsNotEqual_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "new_password1", "new_password");

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La password nuova e la password nuova ripetuta, non sono uguali.", exception.getMessage());
    }

    @Test
    public void updatePassword_InvalidRepeatNewPasswordNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "new_password1", null);

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La password nuova ripetuta inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void updatePassword_InvalidRepeatNewPasswordEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "new_password1", "");

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La password nuova ripetuta inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void updatePassword_InvalidNewPasswordNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", null, "new_password1");

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La password nuova inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void updatePassword_InvalidNewPasswordEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password", "", "new_password1");

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La password nuova inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void updatePassword_InvalidCurrentPasswordNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(null, "new_password1", "new_password1");

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La vecchia password inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void updatePassword_InvalidCurrentPasswordEmpty_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.it");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password1");
        user.setId(1L);

        Client client = new Client(user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("", "new_password1", "new_password1");

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
        () -> userServiceImpl.updatePassword(changePasswordRequest, client));
        assertEquals("La vecchia password inserita non puo' essere nulla o vuota.", exception.getMessage());
    }


    @Test
    public void sendEmail_WhenValidInput_Success(){
        //Setup
        String email = "user@user.com";

        User user = new User();
        user.setId(1L);
        user.setUsername("andrysea");
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail(email);
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //Mock
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userSaved = invocation.getArgument(0);
            return userSaved;
        });

        //Test
        userServiceImpl.sendEmail(email);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).save(userCaptor.capture());
    }

    @Test
    public void sendEmail_WhenValidInputUserNoFind_NotFoundException(){
        //Setup
        String email = "user@user.com";

        User user = new User();
        user.setId(1L);
        user.setUsername("andrysea");
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail(email);
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");

        //Mock
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
 
        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.sendEmail(email));
        assertEquals("Non e' stato trovato nessun utente associato a questa email: " + email, exception.getMessage());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void sendEmail_WhenInvalidInputEmailNull_NotFoundException(){
        //Setup
        String email = null;

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.sendEmail(email));
        assertEquals("La email inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void sendEmail_WhenInvalidInputEmailEmpty_NotFoundException(){
        //Setup
        String email = "";

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.sendEmail(email));
        assertEquals("La email inserita non puo' essere nulla o vuota.", exception.getMessage());
    }

    @Test
    public void findByUsername_WhenValidInput_ReturnObject(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");

        UserDto userDto = new UserDto(user);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        //Test
        UserDto userDtoReturned = userServiceImpl.findByUsername(username);
        verify(userRepository, times(1)).findByUsername(username);

        assertEquals(userDto, userDtoReturned);
    }


    @Test
    public void findByUsername_WhenValidInput_NotFoundException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.findByUsername(username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void findByUsername_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        
        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.findByUsername(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void findByUsername_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () -> userServiceImpl.findByUsername(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getUserListDto_WhenValid_ReturnList(){
        //Setup
        String username = "andrysea";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setRole(Role.CLIENT);

        List<User> listUser = new ArrayList<>();
        listUser.add(user);

        List<UserDto> listUserDto = listUser.stream().map(entity -> new UserDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByRole(Role.CLIENT)).thenReturn(listUser);

        //Test
        List<UserDto> listUserDtoReturned = userServiceImpl.getUserListDto();
        assertEquals(listUserDto, listUserDtoReturned);
    }

    @Test
    public void getUserListDto_WhenValid_NoSuchElementException(){
        //Setup
        List<User> listUser = new ArrayList<>();

        //Mock
        when(userRepository.findByRole(Role.CLIENT)).thenReturn(listUser);

        //Test
        assertThrows(NoSuchElementException.class,
            () -> userServiceImpl.getUserListDto());
    }

    @Test
    public void deleteUserDto_WhenValidInput_Success(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setRole(Role.CLIENT);

        StateWaitingApproval state = new StateWaitingApproval();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByClientUsernameAndAcceptedFalse(anyString())).thenReturn(false);
        when(orderRepository.existsByClientUsernameAndStateId(anyString(), anyLong())).thenReturn(false);
        doNothing().when(userRepository).deleteById(anyLong());

        //Test
        userServiceImpl.deleteUserDto(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(bookingRepository, times(1)).existsByClientUsernameAndAcceptedFalse(username);
        verify(orderRepository, times(1)).existsByClientUsernameAndStateId(username, state.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    public void deleteUserDto_WhenValidInputPrenotationNotAccepted_IllegalStateException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setRole(Role.CLIENT);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByClientUsernameAndAcceptedFalse(anyString())).thenReturn(true);

        //Test
        IllegalStateException exception =  assertThrows(IllegalStateException.class,
            () ->  userServiceImpl.deleteUserDto(username));
        assertEquals("La presenza di prenotazioni non accettate, impedisce l'eliminazione dell'account.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(bookingRepository, times(1)).existsByClientUsernameAndAcceptedFalse(username);
    }

    @Test
    public void deleteUserDto_WhenValidInputOrdersInWaiting_IllegalStateException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setRole(Role.CLIENT);

        StateWaitingApproval state = new StateWaitingApproval();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByClientUsernameAndAcceptedFalse(anyString())).thenReturn(false);
        when(orderRepository.existsByClientUsernameAndStateId(anyString(), anyLong())).thenReturn(true);

        //Test
        IllegalStateException exception =  assertThrows(IllegalStateException.class,
            () ->  userServiceImpl.deleteUserDto(username));
        assertEquals("La presenza di ordini in attesa di approvazione, impedisce l'eliminazione dell'account.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(bookingRepository, times(1)).existsByClientUsernameAndAcceptedFalse(username);
        verify(orderRepository, times(1)).existsByClientUsernameAndStateId(username, state.getId());
    }

    @Test
    public void deleteUserDto_WhenValidInputRoleADMIN_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        //Test
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
            () ->  userServiceImpl.deleteUserDto(username));
        assertEquals("L'utente da eliminare deve avere il ruolo di CLIENT.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void deleteUserDto_WhenValidInput_NotFoundException(){
        //Setup
        String username = "andrysea";
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setName("Andrea");
        user.setSurname("Marino");
        user.setEmail("user@user.com");
        user.setBirthDate("2001-10-03");
        user.setTaxId("1234567890123456");
        user.setNumber("1234567890");
        user.setPassword("password");

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () ->  userServiceImpl.deleteUserDto(username));
        assertEquals("Username inserito non corrisponde a nessun utente.", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void deleteUserDto_WhenInvalidInputusernameNull_NotFoundException(){
        //Setup
        String username = null;
        
        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () ->  userServiceImpl.deleteUserDto(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deleteUserDto_WhenInvalidInputusernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        //Test
        NotFoundException exception =  assertThrows(NotFoundException.class,
        () ->  userServiceImpl.deleteUserDto(username));
        assertEquals("Lo username inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }
}
