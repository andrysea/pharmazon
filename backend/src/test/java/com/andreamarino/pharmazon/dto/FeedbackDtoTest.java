package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Feedback;
import com.andreamarino.pharmazon.model.Product;

@ExtendWith(MockitoExtension.class)
public class FeedbackDtoTest {

    private FeedbackDto feedbackDto;

    @BeforeEach
    void setUp() {
        feedbackDto = new FeedbackDto();
    }

    @Test
    public void conversionFromFeedback() {
        Client client = new Client();
        client.setUsername("andrysea");
        client.setCreditCards(new ArrayList<>());

        Category category = new Category(1L, "123", "Antimicotico", new ArrayList<>());
        Product product = new Product();
        product.setCategory(category);

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        cartItem.setCode("123");
        cartItem.setProduct(product);

        Feedback feedback = new Feedback();
        feedback.setCode("123");
        feedback.setDescription("Descrizione...");
        feedback.setClient(client);
        feedback.setCartItem(cartItem);

        feedbackDto = new FeedbackDto(feedback);
    }

    @Test
    public void allArgsConstructor() {
        String code = "123";
        String description = "Buon prodotto!";
        CartItemDto cartItemDto = new CartItemDto();
        ClientDto clientDto = new ClientDto();

        feedbackDto = new FeedbackDto(code, description, cartItemDto, clientDto);

        assertEquals(code, feedbackDto.getCode());
        assertEquals(description, feedbackDto.getDescription());
        assertEquals(cartItemDto, feedbackDto.getCartItemDto());
        assertEquals(clientDto, feedbackDto.getClientDto());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(feedbackDto);
    }

    @Test
    public void getterAndSetter() {
        String code = "123";
        String description = "Buon prodotto!";
        CartItemDto cartItemDto = new CartItemDto();
        ClientDto clientDto = new ClientDto();

        feedbackDto.setCode(code);
        feedbackDto.setDescription(description);
        feedbackDto.setCartItemDto(cartItemDto);
        feedbackDto.setClientDto(clientDto);

        assertEquals(code, feedbackDto.getCode());
        assertEquals(description, feedbackDto.getDescription());
        assertEquals(cartItemDto, feedbackDto.getCartItemDto());
        assertEquals(clientDto, feedbackDto.getClientDto());
    }

    @Test
    void toStringMethod() {
        String code = "123";
        String description = "Buon prodotto!";
        CartItemDto cartItemDto = new CartItemDto();
        ClientDto clientDto = new ClientDto();

        feedbackDto = new FeedbackDto(code, description, cartItemDto, clientDto);

        String toStringResult = feedbackDto.toString();
        String expectedToString = String.format("FeedbackDto(code=%s, description=%s, cartItemDto=%s, clientDto=%s)",
            code, description, cartItemDto, clientDto);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        String code = "123";
        String description = "Buon prodotto!";
        CartItemDto cartItemDto = new CartItemDto();
        ClientDto clientDto = new ClientDto();

        feedbackDto = new FeedbackDto(code, description, cartItemDto, clientDto);
        FeedbackDto sameFeedbackDto = new FeedbackDto(code, description, cartItemDto, clientDto);

        assertEquals(feedbackDto.hashCode(), sameFeedbackDto.hashCode());
    }
}

