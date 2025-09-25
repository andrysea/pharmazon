package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class ClientTest {
    
    private Client client;

    @Mock
    private EmailService emailService;

    @Test
    public void updateProduct_WhenInvalidQuantity_IllegalArgumentException(){
        //Setup
        client = new Client();

        Product product = new Product();
        product.setQuantity(-1);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            client.updateProduct(product, emailService));
        assertEquals("Il prodotto ha una quantita' non valida < 0.", exception.getMessage());
    }

    @Test
    public void updateProduct_WhenInvalidQuantityNull_NotFoundException(){
        //Setup
        client = new Client();

        Product product = new Product();
        product.setQuantity(null);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            client.updateProduct(product, emailService));
        assertEquals("Il prodotto ha una quantita' nulla.", exception.getMessage());
    }

    @Test
    public void updateProduct_WhenInvalidProductNull_NotFoundException(){
        //Setup
        client = new Client();

        Product product = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            client.updateProduct(product, emailService));
        assertEquals("L'oggetto prodotto inserito e' nullo.", exception.getMessage());
    }

    @Test
    public void updateProduct_WhenInvalidEmailNull_NotFoundException(){
        //Setup
        client = new Client();
        Product product = new Product();
        emailService = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            client.updateProduct(product, emailService));
        assertEquals("L'oggetto relativo alla email inserito e' nullo.", exception.getMessage());
    }
}
