package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class UserDtoTest {

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
    }

    @Test
    public void conversionDto(){
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setNumber(number);
        user.setEmail(email);
        user.setPassword(password);
        user.setBirthDate(birthdate);
        user.setTaxId(tax_id);

        userDto = new UserDto(user);
    }

    @Test
    public void allArgsConstructor(){
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        
        assertEquals(name, userDto.getName());
        assertEquals(surname, userDto.getSurname());
        assertEquals(username, userDto.getUsername());
        assertEquals(number, userDto.getNumber());
        assertEquals(email, userDto.getEmail());
        assertEquals(password, userDto.getPassword());
        assertEquals(birthdate, userDto.getBirthDate());
        assertEquals(tax_id, userDto.getTaxId());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(userDto);
    }

    @Test
    public void getterAndSetter(){
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";
    
        userDto.setRole(Role.ADMIN);
        userDto.setName(name);
        userDto.setSurname(surname);
        userDto.setUsername(username);
        userDto.setNumber(number);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setBirthDate(birthdate);
        userDto.setTaxId(tax_id);

        assertEquals(name, userDto.getName());
        assertEquals(surname, userDto.getSurname());
        assertEquals(username, userDto.getUsername());
        assertEquals(number, userDto.getNumber());
        assertEquals(email, userDto.getEmail());
        assertEquals(password, userDto.getPassword());
        assertEquals(birthdate, userDto.getBirthDate());
        assertEquals(tax_id, userDto.getTaxId());
    }

    @Test
    public void toStringMethod(){
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        
        String toStringResult = userDto.toString();
        String expectedToString = String.format("UserDto(role=%s, name=%s, surname=%s, username=%s, number=%s, email=%s, password=%s, birthDate=%s, taxId=%s)",
        Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        String name = "Andrea";
        String surname = "Marino";
        String username = "andrysea";
        String number = "12345678901";
        String email = "user@user.com";
        String password = "12345678901";
        String birthdate = "2001-10-03";
        String tax_id = "1234567890123456";

        userDto = new UserDto(Role.CLIENT, name, surname, username, number, email, password, birthdate, tax_id);
        
        UserDto sameUserDto = new UserDto();
        sameUserDto.setRole(Role.CLIENT);
        sameUserDto.setName(name);
        sameUserDto.setSurname(surname);
        sameUserDto.setUsername(username);
        sameUserDto.setNumber(number);
        sameUserDto.setEmail(email);
        sameUserDto.setPassword(password);
        sameUserDto.setBirthDate(birthdate);
        sameUserDto.setTaxId(tax_id);

        assertEquals(userDto, sameUserDto);
        assertEquals(userDto.hashCode(), sameUserDto.hashCode());

        sameUserDto.setName("Franco");

        assertNotEquals(userDto, sameUserDto);
        assertNotEquals(userDto.hashCode(), sameUserDto.hashCode());
    }
    
}
