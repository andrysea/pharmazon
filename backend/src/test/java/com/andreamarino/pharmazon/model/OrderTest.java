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

import com.andreamarino.pharmazon.dto.AddressDto;
import com.andreamarino.pharmazon.dto.CartDto;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.dto.OrderDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;

@ExtendWith(MockitoExtension.class)
public class OrderTest {
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
    }

    @Test
    public void conversionDto(){
        String username = "andrysea";
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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        order = new Order(orderDto);
    }

    @Test
    public void allArgsConstructor(){
        Long id = 1L;
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        
        Cart cart = new Cart();
        cart.setId(1L);
        
        Timestamp dateTimeCreation = new Timestamp(0);

        AddressDto addressDto = new AddressDto();
        addressDto.setCode("ABC123");
        addressDto.setName("Mario");
        addressDto.setSurname("Rossi");
        addressDto.setNumber("123");
        addressDto.setAddress("Via Roma");
        addressDto.setCap("00100");
        addressDto.setCity("Roma");
        addressDto.setProvince("RM");
        Address address = new Address(addressDto);

        order = new Order(id, code, client, creditCard, total, cart, statePreparation, address, dateTimeCreation);

        assertEquals(id, order.getId());
        assertEquals(code, order.getCode());
        assertEquals(address, order.getAddress());
        assertEquals(client, order.getClient());
        assertEquals(creditCard, order.getCreditCard());
        assertEquals(total, order.getTotal());
        assertEquals(cart, order.getCart());
        assertEquals(statePreparation, order.getState());
        assertEquals(dateTimeCreation, order.getDateTimeCreation());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(order);
    }

    @Test
    public void getterAndSetter(){
        Long id = 1L;
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);


        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        
        Cart cart = new Cart();
        cart.setId(1L);
        
        Timestamp dateTimeCreation = new Timestamp(0);

        order.setId(id);
        order.setCode(code);
        order.setClient(client);
        order.setCreditCard(creditCard);
        order.setTotal(total);
        order.setCart(cart);
        order.setState(statePreparation);
        order.setDateTimeCreation(dateTimeCreation);

        assertEquals(id, order.getId());
        assertEquals(code, order.getCode());
        assertEquals(client, order.getClient());
        assertEquals(creditCard, order.getCreditCard());
        assertEquals(total, order.getTotal());
        assertEquals(cart, order.getCart());
        assertEquals(statePreparation, order.getState());
        assertEquals(dateTimeCreation, order.getDateTimeCreation());
    }

    @Test
    public void toStringMethod(){
        Long id = 1L;
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        
        Cart cart = new Cart();
        cart.setId(1L);
        
        Timestamp dateTimeCreation = new Timestamp(0);

        AddressDto addressDto = new AddressDto();
        addressDto.setCode("ABC123");
        addressDto.setName("Mario");
        addressDto.setSurname("Rossi");
        addressDto.setNumber("123");
        addressDto.setAddress("Via Roma");
        addressDto.setCap("00100");
        addressDto.setCity("Roma");
        addressDto.setProvince("RM");
        
        Address address = new Address(addressDto);

        order = new Order(id, code, client, creditCard, total, cart, statePreparation, address, dateTimeCreation);
        
        String toStringResult = order.toString();
        String expectedToString = String.format("Order(id=%d, code=%s, client=%s, creditCard=%s, total=%s, cart=%s, state=%s, address=%s, dateTimeCreation=%s)",
            id, code, client, creditCard, total, cart, statePreparation, address, dateTimeCreation);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        Long id = 1L;
        String username = "andrysea";
        String code = "123";
        Double total = 100.0;

        StatePreparation statePreparation = new StatePreparation();

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        
        Cart cart = new Cart();
        cart.setId(1L);
        
        Timestamp dateTimeCreation = new Timestamp(0);

        AddressDto addressDto = new AddressDto();
        addressDto.setCode("ABC123");
        addressDto.setName("Mario");
        addressDto.setSurname("Rossi");
        addressDto.setNumber("123");
        addressDto.setAddress("Via Roma");
        addressDto.setCap("00100");
        addressDto.setCity("Roma");
        addressDto.setProvince("RM");
        
        Address address = new Address(addressDto);

        order = new Order(id, code, client, creditCard, total, cart, statePreparation, address, dateTimeCreation);
        
        Order sameOrder = new Order();
        sameOrder.setId(id);
        sameOrder.setCode(code);
        sameOrder.setClient(client);
        sameOrder.setCreditCard(creditCard);
        sameOrder.setTotal(total);
        sameOrder.setCart(cart);
        sameOrder.setState(statePreparation);
        sameOrder.setDateTimeCreation(dateTimeCreation);
        sameOrder.setAddress(address);

        assertEquals(order, sameOrder);
        assertEquals(order.hashCode(), sameOrder.hashCode());

        sameOrder.setCode("456");

        assertNotEquals(order, sameOrder);
        assertNotEquals(order.hashCode(), sameOrder.hashCode());
    }
}
