package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.Product;

@ExtendWith(MockitoExtension.class)
public class CartDtoTest {

    private CartDto cartDto;
    private Cart cart;
    
    @BeforeEach
    void setUp() {
        cartDto = new CartDto();
    }

    @Test
    public void conversionDto(){
        List<CartItem> cartItems = new ArrayList<>();
        List<Order> listOrders = new ArrayList<>();

        Client client = new Client();
        client.setUsername("andrysea");
        
        Category category = new Category();
        category.setName("Antinfiammatorio");
        
        Product product = new Product();
        product.setCategory(category);
        
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(new Cart());
        cartItem.setProduct(product);
        cartItem.setImagePrescription(null);
        cartItem.setQuantity(2);
        cartItems.add(cartItem);

        cart = new Cart(1L, cartItems, listOrders, new Client(), true);
        cartDto = new CartDto(cart);
    }

    @Test
    public void allArgsConstructor(){
        List<CartItemDto> cartItemsDto = new ArrayList<>();
        ClientDto clientDto = new ClientDto();

        cartDto = new CartDto(cartItemsDto, clientDto);
        assertEquals(cartItemsDto, cartDto.getCartItemsDto());
        assertEquals(clientDto, cartDto.getClientDto());
    }

    
    @Test
    void noArgsConstructor() {
        assertNotNull(cartDto);
    }

    @Test
    public void getterAndSetter(){
        List<CartItemDto> cartItemsDto = new ArrayList<>();
        ClientDto clientDto = new ClientDto();

        cartDto.setCartItemsDto(cartItemsDto);
        cartDto.setClientDto(clientDto);

        assertEquals(cartItemsDto, cartDto.getCartItemsDto());
        assertEquals(clientDto, cartDto.getClientDto());
    }

    @Test
    public void toStringMethod(){
        List<CartItemDto> cartItemsDto = new ArrayList<>();
        ClientDto clientDto = new ClientDto();
        cartDto = new CartDto(cartItemsDto, clientDto);
        
        String toStringResult = cartDto.toString();
        assertEquals(String.format("CartDto(cartItemsDto=%s, clientDto=%s)",
        cartDto.getCartItemsDto(), cartDto.getClientDto()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        CartDto sameCartDto = new CartDto();
        
        List<CartItemDto> cartItemsDto = new ArrayList<>();
        
        ClientDto clientDto = new ClientDto();
        clientDto.setName("Luca");
        clientDto.setCreditCardsDto(new ArrayList<>());
        
        sameCartDto.setCartItemsDto(cartItemsDto);
        sameCartDto.setClientDto(clientDto);
        
        cartDto.setCartItemsDto(cartItemsDto);
        cartDto.setClientDto(clientDto);

        assertEquals(sameCartDto, cartDto);
        assertEquals(sameCartDto.hashCode(), cartDto.hashCode());

        ClientDto clientDto2 = new ClientDto();
        clientDto2.setName("Giovanni");
        
        sameCartDto.setClientDto(clientDto2);
        assertNotEquals(sameCartDto, cartDto);
        assertNotEquals(sameCartDto.hashCode(), cartDto.hashCode());
    }
}
