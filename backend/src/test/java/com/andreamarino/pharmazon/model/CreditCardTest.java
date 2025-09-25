package com.andreamarino.pharmazon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.model.designPattern.state.StateInTransit;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class CreditCardTest {
    private CreditCard creditCard;

    @BeforeEach
    void setUp() {
        creditCard = new CreditCard();
    }

    @Test
    public void conversionDto(){
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setBalance(100.0);
        
        creditCard = new CreditCard(creditCardDto);
    }

    @Test
    public void allArgsConstructor(){
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;
        boolean active = true;
        Timestamp dateTimeCreation = new Timestamp(0);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        Client client = new Client();
        client.setUsername("andrysea");
        client.setRole(Role.CLIENT);

        Cart cart = new Cart();
        cart.setCartItems(listCartItem);
        cart.setClient(client);

        StatePreparation statePreparation = new StatePreparation();
    
        Order order = new Order();
        order.setCart(cart);
        order.setClient(client);
        order.setState(statePreparation);

        List<Order> listOrder = new ArrayList<>();
        listOrder.add(order);

        creditCard = new CreditCard(id, name, surname, number, cardSecurityCode, expirationDate, balance, active, client, listOrder, dateTimeCreation);

        assertEquals(id, creditCard.getId());
        assertEquals(name, creditCard.getName());
        assertEquals(surname, creditCard.getSurname());
        assertEquals(number, creditCard.getNumber());
        assertEquals(cardSecurityCode, creditCard.getCardSecurityCode());
        assertEquals(expirationDate, creditCard.getExpirationDate());
        assertEquals(balance, creditCard.getBalance());
        assertEquals(client, creditCard.getClient());
        assertEquals(dateTimeCreation, creditCard.getDateTimeCreation());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(creditCard);
    }

    @Test
    public void getterAndSetter(){
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;
        Client client = new Client();
        List<Order> listOrder = new ArrayList<>();
        Timestamp dateTimeCreation = new Timestamp(0);

        creditCard.setId(id);
        creditCard.setName(name);
        creditCard.setSurname(surname);
        creditCard.setNumber(number);
        creditCard.setCardSecurityCode(cardSecurityCode);
        creditCard.setExpirationDate(expirationDate);
        creditCard.setBalance(balance);
        creditCard.setClient(client);
        creditCard.setDateTimeCreation(dateTimeCreation);

        assertEquals(id, creditCard.getId());
        assertEquals(name, creditCard.getName());
        assertEquals(surname, creditCard.getSurname());
        assertEquals(number, creditCard.getNumber());
        assertEquals(cardSecurityCode, creditCard.getCardSecurityCode());
        assertEquals(expirationDate, creditCard.getExpirationDate());
        assertEquals(balance, creditCard.getBalance());
        assertEquals(client, creditCard.getClient());
        assertEquals(dateTimeCreation, creditCard.getDateTimeCreation());
    }

    @Test
    public void toStringMethod(){
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;
        boolean active = true;
        Timestamp dateTimeCreation = new Timestamp(0);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        Client client = new Client();
        client.setUsername("andrysea");
        client.setRole(Role.CLIENT);

        Cart cart = new Cart();
        cart.setCartItems(listCartItem);
        cart.setClient(client);

        StatePreparation statePreparation = new StatePreparation();

        Order order = new Order();
        order.setCart(cart);
        order.setClient(client);
        order.setState(statePreparation);

        List<Order> listOrder = new ArrayList<>();
        listOrder.add(order);

        creditCard = new CreditCard(id, name, surname, number, cardSecurityCode, expirationDate, balance, active, client, listOrder, dateTimeCreation);
        
        String toStringResult = creditCard.toString();
        assertEquals(String.format("CreditCard(id=%s, name=%s, surname=%s, number=%s, cardSecurityCode=%s, expirationDate=%s, balance=%s, active=%s, client=%s, orders=%s, dateTimeCreation=%s)",
        creditCard.getId(), creditCard.getName(), creditCard.getSurname(), creditCard.getNumber(), creditCard.getCardSecurityCode(), creditCard.getExpirationDate(), creditCard.getBalance(), creditCard.isActive(), creditCard.getClient(), creditCard.getOrders(), creditCard.getDateTimeCreation()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        Long id = 1L;
        String name = "Andrea";
        String surname = "Marino";
        String number = "1234567890123456";
        String cardSecurityCode = "123";
        String expirationDate = "2030-03";
        Double balance = 100.0;
        boolean active = true;
        Timestamp dateTimeCreation = new Timestamp(0);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        Client client = new Client();
        client.setUsername("andrysea");
        client.setRole(Role.CLIENT);

        Cart cart = new Cart();
        cart.setCartItems(listCartItem);
        cart.setClient(client);

        StatePreparation statePreparation = new StatePreparation();        

        Order order = new Order();
        order.setCart(cart);
        order.setClient(client);
        order.setState(statePreparation);

        List<Order> listOrder = new ArrayList<>();
        listOrder.add(order);

        creditCard = new CreditCard(id, name, surname, number, cardSecurityCode, expirationDate, balance, active, client, listOrder, dateTimeCreation);

        CreditCard sameCreditCard= new CreditCard();
        
        sameCreditCard.setId(id);
        sameCreditCard.setName(name);
        sameCreditCard.setSurname(surname);
        sameCreditCard.setNumber(number);
        sameCreditCard.setCardSecurityCode(cardSecurityCode);
        sameCreditCard.setExpirationDate(expirationDate);
        sameCreditCard.setBalance(balance);
        sameCreditCard.setClient(client);
        sameCreditCard.setDateTimeCreation(dateTimeCreation);
        sameCreditCard.setOrders(listOrder);

        assertEquals(creditCard, sameCreditCard);
        assertEquals(creditCard.hashCode(), sameCreditCard.hashCode());

        sameCreditCard.setName("Luigi");
        assertNotEquals(sameCreditCard, creditCard);
        assertNotEquals(sameCreditCard.hashCode(), creditCard.hashCode());
    }
}
