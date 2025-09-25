package com.andreamarino.pharmazon.dto;

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
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.CreditCard;
import com.andreamarino.pharmazon.model.Order;

@ExtendWith(MockitoExtension.class)
public class CreditCardDtoTest {
    private CreditCardDto creditCardDto;

    @BeforeEach
    void setUp() {
        creditCardDto = new CreditCardDto();
    }

    @Test
    public void conversionDto(){
        CreditCard creditCard = new CreditCard();
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setBalance(100.0);
        creditCard.setClient(new Client());
        creditCard.setDateTimeCreation(new Timestamp(System.currentTimeMillis()));
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());        
        creditCardDto = new CreditCardDto(creditCard);
    }

    @Test
    public void allArgsConstructor(){
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;

        creditCardDto = new CreditCardDto(name, surname, number, cardSecurityCode, expirationDate, balance);

        assertEquals(name, creditCardDto.getName());
        assertEquals(surname, creditCardDto.getSurname());
        assertEquals(number, creditCardDto.getNumber());
        assertEquals(cardSecurityCode, creditCardDto.getCardSecurityCode());
        assertEquals(expirationDate, creditCardDto.getExpirationDate());
        assertEquals(balance, creditCardDto.getBalance());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(creditCardDto);
    }

    @Test
    public void getterAndSetter(){
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;

        creditCardDto.setName(name);
        creditCardDto.setSurname(surname);
        creditCardDto.setNumber(number);
        creditCardDto.setCardSecurityCode(cardSecurityCode);
        creditCardDto.setExpirationDate(expirationDate);
        creditCardDto.setBalance(balance);

        assertEquals(name, creditCardDto.getName());
        assertEquals(surname, creditCardDto.getSurname());
        assertEquals(number, creditCardDto.getNumber());
        assertEquals(cardSecurityCode, creditCardDto.getCardSecurityCode());
        assertEquals(expirationDate, creditCardDto.getExpirationDate());
        assertEquals(balance, creditCardDto.getBalance());
    }

    @Test
    public void toStringMethod(){
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;

        creditCardDto = new CreditCardDto(name, surname, number, cardSecurityCode, expirationDate, balance);
        
        String toStringResult = creditCardDto.toString();
        assertEquals(String.format("CreditCardDto(name=%s, surname=%s, number=%s, cardSecurityCode=%s, expirationDate=%s, balance=%s)",
        creditCardDto.getName(), creditCardDto.getSurname(), creditCardDto.getNumber(), creditCardDto.getCardSecurityCode(), creditCardDto.getExpirationDate(), creditCardDto.getBalance()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;

        creditCardDto = new CreditCardDto(name, surname, number, cardSecurityCode, expirationDate, balance);

        CreditCardDto sameCreditCardDto= new CreditCardDto();
        sameCreditCardDto.setName(name);
        sameCreditCardDto.setSurname(surname);
        sameCreditCardDto.setNumber(number);
        sameCreditCardDto.setCardSecurityCode(cardSecurityCode);
        sameCreditCardDto.setExpirationDate(expirationDate);
        sameCreditCardDto.setBalance(balance);

        assertEquals(creditCardDto, sameCreditCardDto);
        assertEquals(creditCardDto.hashCode(), sameCreditCardDto.hashCode());

        sameCreditCardDto.setName("Luigi");
        assertNotEquals(sameCreditCardDto, creditCardDto);
        assertNotEquals(sameCreditCardDto.hashCode(), creditCardDto.hashCode());
    }
}

