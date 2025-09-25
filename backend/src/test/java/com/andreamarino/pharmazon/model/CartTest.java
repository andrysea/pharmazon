package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.CartDto;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.dto.ProductDto;

@ExtendWith(MockitoExtension.class)
public class CartTest {
    
    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    public void conversionDto(){
        List<CartItemDto> listCartItemsDto = new ArrayList<>();
        
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Antinfiammatorio");
        
        ProductDto productDto = new ProductDto();
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setImagePrescription(null);
        cartItemDto.setQuantity(2);
        listCartItemsDto.add(cartItemDto);

        CreditCardDto creditCardDto = new CreditCardDto();
        List<CreditCardDto> creditCardDtos = new ArrayList<>();
        creditCardDtos.add(creditCardDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setCreditCardsDto(creditCardDtos);

        cartDto = new CartDto(listCartItemsDto, clientDto);
        cart = new Cart(cartDto);
    }

    @Test
    public void allArgsConstructor(){
        List<CartItem> listCartItems = new ArrayList<>();
        List<Order> listOrders = new ArrayList<>();
        Client client = new Client();

        cart = new Cart(1L, listCartItems, listOrders, client, true);
        assertEquals(listCartItems, cart.getCartItems());
        assertEquals(listOrders, cart.getOrders());
        assertEquals(client, cart.getClient());
    }

    
    @Test
    void noArgsConstructor() {
        assertNotNull(cart);
    }

    @Test
    public void getterAndSetter(){
        List<CartItem> listCartItems = new ArrayList<>();
        Client client = new Client();

        cart.setCartItems(listCartItems);
        cart.setClient(client);

        assertEquals(listCartItems, cart.getCartItems());
        assertEquals(client, cart.getClient());
    }

    @Test
    public void toStringMethod(){
        List<CartItem> listCartItems = new ArrayList<>();
        List<Order> listOrders = new ArrayList<>();

        Client client = new Client();
        cart = new Cart(1L, listCartItems, listOrders, client, true);
        
        String toStringResult = cart.toString();
        assertEquals(String.format("Cart(id=%s, cartItems=%s, orders=%s, client=%s, activeCart=%s)",
        cart.getId(), cart.getCartItems(), cart.getOrders(), cart.getClient(), cart.getActiveCart()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        Cart sameCart = new Cart();
        sameCart.setId(1L);
        
        List<CartItem> listCartItems = new ArrayList<>();
        
        Client client = new Client();
        client.setName("Luca");
        client.setCreditCards(new ArrayList<>());
        
        sameCart.setCartItems(listCartItems);
        sameCart.setClient(client);
        
        cart.setId(1L);
        cart.setCartItems(listCartItems);
        cart.setClient(client);

        assertEquals(sameCart, cart);
        assertEquals(sameCart.hashCode(), cart.hashCode());

        Client client2 = new Client();
        client2.setName("Giovanni");
        
        cart.setId(2L);
        sameCart.setClient(client2);
        assertNotEquals(sameCart, cart);
        assertNotEquals(sameCart.hashCode(), cart.hashCode());
    }
}
