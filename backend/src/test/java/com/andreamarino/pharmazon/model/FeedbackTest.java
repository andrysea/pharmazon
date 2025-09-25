package com.andreamarino.pharmazon.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.FeedbackDto;
import com.andreamarino.pharmazon.dto.ProductDto;

@ExtendWith(MockitoExtension.class)
public class FeedbackTest {
    private Feedback feedback;
    
    @BeforeEach
    void setUp() {
        feedback = new Feedback();
    }

    @Test
    public void conversionDto(){
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("123", "Antimicotico");
        ProductDto productDto = new ProductDto();
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        feedback = new Feedback(feedbackDto);
    }

    @Test
    public void allArgsConstructor(){
        Long id = 1L;
        String code = "123";
        String description = "Buon prodotto!";
        CartItem cartItem = new CartItem();
        Client client = new Client();
        Timestamp dateTimeCreation = new Timestamp(0);

        feedback = new Feedback(id, code, description, cartItem, client, dateTimeCreation);

        assertEquals(id, feedback.getId());
        assertEquals(code, feedback.getCode());
        assertEquals(description, feedback.getDescription());
        assertEquals(cartItem, feedback.getCartItem());
        assertEquals(client, feedback.getClient());
        assertEquals(dateTimeCreation, feedback.getDateTimeCreation());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(feedback);
    }

    @Test
    public void getterAndSetter(){
        Long id = 1L;
        String code = "123";
        String description = "Buon prodotto!";
        CartItem cartItem = new CartItem();
        Client client = new Client();
        Timestamp dateTimeCreation = new Timestamp(0);

        feedback.setId(id);
        feedback.setCode(code);
        feedback.setDescription(description);
        feedback.setCartItem(cartItem);
        feedback.setClient(client);
        feedback.setDateTimeCreation(dateTimeCreation);

        assertEquals(id, feedback.getId());
        assertEquals(code, feedback.getCode());
        assertEquals(description, feedback.getDescription());
        assertEquals(cartItem, feedback.getCartItem());
        assertEquals(client, feedback.getClient());
        assertEquals(dateTimeCreation, feedback.getDateTimeCreation());
    }

    @Test
    public void toStringMethod(){
        Long id = 1L;
        String code = "123";
        String description = "Buon prodotto!";
        CartItem cartItem = new CartItem();
        Client client = new Client();
        Timestamp dateTimeCreation = new Timestamp(0);

        feedback = new Feedback(id, code, description, cartItem, client, dateTimeCreation);
        
        String toStringResult = feedback.toString();
        String expectedToString = String.format("Feedback(id=%d, code=%s, description=%s, cartItem=%s, client=%s, dateTimeCreation=%s)",
            id, code, description, cartItem, client, dateTimeCreation);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        Long id = 1L;
        String code = "123";
        String description = "Buon prodotto!";
        CartItem cartItem = new CartItem();
        Client client = new Client();
        Timestamp dateTimeCreation = new Timestamp(0);

        feedback = new Feedback(id, code, description, cartItem, client, dateTimeCreation);
        
        Feedback sameFeedback = new Feedback();
        sameFeedback.setId(id);
        sameFeedback.setCode(code);
        sameFeedback.setDescription(description);
        sameFeedback.setCartItem(cartItem);
        sameFeedback.setClient(client);
        sameFeedback.setDateTimeCreation(dateTimeCreation);

        assertEquals(feedback, sameFeedback);
        assertEquals(feedback.hashCode(), sameFeedback.hashCode());

        sameFeedback.setCode("456");

        assertNotEquals(feedback, sameFeedback);
        assertNotEquals(feedback.hashCode(), sameFeedback.hashCode());
    }
}
