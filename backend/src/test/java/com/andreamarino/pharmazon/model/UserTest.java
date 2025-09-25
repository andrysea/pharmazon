package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.security.token.Token;
import com.andreamarino.pharmazon.security.token.TokenType;
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    public void conversionDto(){
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setSurname(surname);
        userDto.setUsername(username);
        userDto.setNumber(number);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setBirthDate(birthdate);
        userDto.setTaxId(tax_id);

        user = new User(userDto);
    }

    @Test
    public void allArgsConstructor(){
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        Timestamp timestamp = new Timestamp(0);
        List<Token> listToken = new ArrayList<>();

        user = new User(id, Role.CLIENT, name, surname, username, email, number, tax_id, password, birthdate, listToken, timestamp);
        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, user);
        listToken.add(token);

        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals(username, user.getUsername());
        assertEquals(number, user.getNumber());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(birthdate, user.getBirthDate());
        assertEquals(tax_id, user.getTaxId());
        assertEquals(listToken, user.getTokens());
        assertEquals(timestamp, user.getDateTimeCreation());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(user);
    }

    @Test
    public void getterAndSetter(){
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        Timestamp timestamp = new Timestamp(0);
        List<Token> listToken = new ArrayList<>();

        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, user);
        listToken.add(token);

        user.setId(id);
        user.setRole(Role.ADMIN);
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setNumber(number);
        user.setEmail(email);
        user.setPassword(password);
        user.setBirthDate(birthdate);
        user.setTaxId(tax_id);
        user.setDateTimeCreation(timestamp);
        user.setTokens(listToken);
        
        user.getAuthorities();
        user.isEnabled();
        user.isAccountNonExpired();
        user.isAccountNonLocked();
        user.isCredentialsNonExpired();

        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals(username, user.getUsername());
        assertEquals(number, user.getNumber());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(birthdate, user.getBirthDate());
        assertEquals(tax_id, user.getTaxId());
        assertEquals(listToken, user.getTokens());
        assertEquals(timestamp, user.getDateTimeCreation());
    }

    @Test
    public void toStringMethod(){
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        Timestamp timestamp = new Timestamp(0);
        List<Token> listToken = new ArrayList<>();

        user = new User(id, Role.CLIENT, name, surname, username, email, number, tax_id, password, birthdate, listToken, timestamp);
        Token token = new Token(1L, "aaa", TokenType.BEARER, false, false, user);
        listToken.add(token);

        user.toString();
    }

    @Test
    void testEqualsAndHashCode() {
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        List<Token> listToken = new ArrayList<>();
        Timestamp timestamp = new Timestamp(0);

        user = new User(id, Role.CLIENT, name, surname, username, email, number, tax_id, password, birthdate, listToken, timestamp);
        
        User sameUser = new User();
        sameUser.setId(id);
        sameUser.setRole(Role.CLIENT);
        sameUser.setName(name);
        sameUser.setSurname(surname);
        sameUser.setUsername(username);
        sameUser.setNumber(number);
        sameUser.setEmail(email);
        sameUser.setPassword(password);
        sameUser.setBirthDate(birthdate);
        sameUser.setTaxId(tax_id);
        sameUser.setDateTimeCreation(timestamp);
       
        assertEquals(user, sameUser);
        assertEquals(user.hashCode(), sameUser.hashCode());

        sameUser.setName("Franco");

        assertNotEquals(user, sameUser);
        assertNotEquals(user.hashCode(), sameUser.hashCode());
    }
    
}
