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
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.CreditCard;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;

@ExtendWith(MockitoExtension.class)
public class OrderDtoTest {

    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
    }

    @Test
    public void conversionDto(){
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setImagePrescription(pngHeader);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);
        product.setCartItems(listCartItem);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setClient(client);
        cart.setCartItems(listCartItem);

        StatePreparation statePreparation = new StatePreparation();

        Order order = new Order();
        order.setTotal(1.0);
        order.setCart(cart);
        order.setClient(client);
        order.setState(statePreparation);

        orderDto = new OrderDto(order);
    }

    @Test
    public void allArgsConstructor(){
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();
        StateDto stateDto = new StateDto(statePreparation);

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(1.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        AddressDto addressDto = new AddressDto();
        addressDto.setCode("ABC123");
        addressDto.setName("Mario");
        addressDto.setSurname("Rossi");
        addressDto.setNumber("123");
        addressDto.setAddress("Via Roma");
        addressDto.setCap("00100");
        addressDto.setCity("Roma");
        addressDto.setProvince("RM");

        orderDto = new OrderDto(cartDto, code, total, stateDto, addressDto, clientDto);
        
        assertEquals(cartDto, orderDto.getCartDto());
        assertEquals(addressDto, orderDto.getAddressDto());
        assertEquals(code, orderDto.getCode());
        assertEquals(clientDto, orderDto.getClientDto());
        assertEquals(stateDto, orderDto.getStateDto());
        assertEquals(total, orderDto.getTotal());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(orderDto);
    }

    @Test
    public void getterAndSetter(){
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();
        StateDto stateDto = new StateDto(statePreparation);

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(1.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);
        orderDto.setCode(code);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDto);
        orderDto.setTotal(total);
        
        assertEquals(cartDto, orderDto.getCartDto());
        assertEquals(code, orderDto.getCode());
        assertEquals(clientDto, orderDto.getClientDto());
        assertEquals(stateDto, orderDto.getStateDto());
        assertEquals(total, orderDto.getTotal());
    }

    @Test
    public void toStringMethod(){
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();
        StateDto stateDto = new StateDto(statePreparation);

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(1.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        AddressDto addressDto = new AddressDto();
        addressDto.setCode("ABC123");
        addressDto.setName("Mario");
        addressDto.setSurname("Rossi");
        addressDto.setNumber("123");
        addressDto.setAddress("Via Roma");
        addressDto.setCap("00100");
        addressDto.setCity("Roma");
        addressDto.setProvince("RM");

        orderDto = new OrderDto(cartDto, code, total, stateDto, addressDto, clientDto);
        
        String toStringResult = orderDto.toString();
        String expectedToString = String.format("OrderDto(cartDto=%s, code=%s, total=%s, stateDto=%s, addressDto=%s, clientDto=%s)",
        cartDto, code, total, stateDto, addressDto, clientDto);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();
        StateDto stateDto = new StateDto(statePreparation);

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(1.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        AddressDto addressDto = new AddressDto();
        addressDto.setCode("ABC123");
        addressDto.setName("Mario");
        addressDto.setSurname("Rossi");
        addressDto.setNumber("123");
        addressDto.setAddress("Via Roma");
        addressDto.setCap("00100");
        addressDto.setCity("Roma");
        addressDto.setProvince("RM");

        orderDto = new OrderDto(cartDto, code, total, stateDto, addressDto, clientDto);
        
        OrderDto sameOrderDto = new OrderDto();
        sameOrderDto.setCartDto(cartDto);
        sameOrderDto.setCode(code);
        sameOrderDto.setClientDto(clientDto);
        sameOrderDto.setStateDto(stateDto);
        sameOrderDto.setAddressDto(addressDto);
        sameOrderDto.setTotal(total);
       
        assertEquals(orderDto, sameOrderDto);
        assertEquals(orderDto.hashCode(), sameOrderDto.hashCode());

        sameOrderDto.setCode("456");

        assertNotEquals(orderDto, sameOrderDto);
        assertNotEquals(orderDto.hashCode(), sameOrderDto.hashCode());
    }       
}
