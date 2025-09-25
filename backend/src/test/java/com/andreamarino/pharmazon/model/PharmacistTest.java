package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.PharmacistDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class PharmacistTest {

    private Pharmacist pharmacist;

    @Mock
    private EmailService emailService;

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

        PharmacistDto pharmacistDto = new PharmacistDto();

        pharmacistDto.setName(name);
        pharmacistDto.setSurname(surname);
        pharmacistDto.setUsername(username);
        pharmacistDto.setNumber(number);
        pharmacistDto.setEmail(email);
        pharmacistDto.setPassword(password);
        pharmacistDto.setBirthDate(birthdate);
        pharmacistDto.setTaxId(tax_id);

        pharmacist = new Pharmacist(pharmacistDto);
    }

    @Test
    public void updateProduct_WhenInvalidQuantity_IllegalArgumentException(){
        //Setup
        pharmacist = new Pharmacist();

        Product product = new Product();
        product.setQuantity(-1);
        product.setPharmacist(pharmacist);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            pharmacist.updateProduct(product, emailService));
        assertEquals("Il prodotto ha una quantita' non valida < 0.", exception.getMessage());
    }

    @Test
    public void updateProduct_WhenInvalidQuantityNull_NotFoundException(){
        //Setup
        pharmacist = new Pharmacist();

        Product product = new Product();
        product.setQuantity(null);
        product.setPharmacist(pharmacist);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            pharmacist.updateProduct(product, emailService));
        assertEquals("Il prodotto ha una quantita' nulla.", exception.getMessage());
    }

    @Test
    public void updateProduct_WhenInvalidProductNull_NotFoundException(){
        //Setup
        pharmacist = new Pharmacist();

        Product product = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            pharmacist.updateProduct(product, emailService));
        assertEquals("L'oggetto prodotto inserito e' nullo.", exception.getMessage());
    }

    @Test
    public void updateProduct_WhenInvalidEmailNull_NotFoundException(){
        //Setup
        pharmacist = new Pharmacist();
        Product product = new Product();
        emailService = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            pharmacist.updateProduct(product, emailService));
        assertEquals("L'oggetto relativo alla email inserito e' nullo.", exception.getMessage());
    }
}
