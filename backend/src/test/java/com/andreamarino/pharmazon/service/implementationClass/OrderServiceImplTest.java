package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreamarino.pharmazon.dto.AddressDto;
import com.andreamarino.pharmazon.dto.CartDto;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.dto.OrderDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.dto.StateDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Address;
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.CreditCard;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.designPattern.state.StateDeleted;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.model.designPattern.state.StateInTransit;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.AddressRepository;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import com.andreamarino.pharmazon.repository.CartRepository;
import com.andreamarino.pharmazon.repository.CreditCardRepository;
import com.andreamarino.pharmazon.repository.OrderRepository;
import com.andreamarino.pharmazon.repository.ProductRepository;
import com.andreamarino.pharmazon.repository.StateRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Test
    public void insertOrderDto_WhenValidInputWithPrescription_ReturnObject() {
        // Setup
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setId(1L);
        creditCard.setActive(true);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(stateWatingApproval));
        orderDto.setAddressDto(addressDto);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateWatingApproval));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        // Test
        OrderDto orderDtoInsert = orderServiceImpl.insertOrderDto(orderDto, username);
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
        verify(stateRepository, times(1)).findByState(stateWatingApproval.getState());
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        assertEquals(orderDtoInsert, orderDto);
    }

    @Test
    public void insertOrderDto_WhenInvalidStateWatingApproval_InternalError() {
        // Setup
        String username = "andrysea";
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(jpegHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        // Test
        InternalError exception = assertThrows(InternalError.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());
  
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
        verify(stateRepository, times(1)).findByState(stateWatingApproval.getState());
    }

    @Test
    public void insertOrderDto_WhenValidInputWithoutPrescription_ReturnObject() {
        // Setup
        String username = "andrysea";

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

        StatePreparation statePreparation = new StatePreparation();

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(statePreparation));
        orderDto.setAddressDto(addressDto);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        // Test
        OrderDto orderDtoInsert = orderServiceImpl.insertOrderDto(orderDto, username);
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
        verify(stateRepository, times(1)).findByState(statePreparation.getState());
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        assertEquals(orderDtoInsert, orderDto);
    }

    @Test
    public void insertOrderDto_WhenInvalidStatePreparation_InternalError() {
        // Setup
        String username = "andrysea";

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

        StatePreparation statePreparation = new StatePreparation();

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        // Test
        InternalError exception = assertThrows(InternalError.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
        verify(stateRepository, times(1)).findByState(statePreparation.getState());
    }

    @Test
    public void insertOrderDto_WhenInvalidBalance_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Il saldo della carta di credito, deve essere maggiore o uguale di " + orderDto.getTotal() + "€.",
                exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidTotal_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Il totale inserito non e' corretto.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidCartItemsSize_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);
        listCartItemsDb.add(new CartItem());

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals(
                "Il numero di elementi presentati, non corrisponde agli elementi effettivamente presenti nel carrello.",
                exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidCartItemInsert_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);
        listCartItemsDb.get(0).getProduct().setCode("456");

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Hai inserito qualche prodotto che non fa parte del carrello.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidImagePrescriptionNull_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, null, "Gargnier",
                "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("L'immagine della prescrizione, non puo' essere nulla o vuota.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidImagePrescriptionLength0_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(100.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[0],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("L'immagine della prescrizione, non puo' essere nulla o vuota.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidImagePrescription_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Immagine non valida.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidQuantity_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(0);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("La quantita' inserita per il prodotto con codice: "
                + listCartItemsDb.get(0).getProduct().getCode() + " non e' corretta.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidQuantityLessThan0Insert_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(0);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("La quantita' inserita per il prodotto con codice: "
                + listCartItemsDb.get(0).getProduct().getCode() + " non e' corretta.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidQuantityNull_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItemsDb);

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("La quantita' inserita per il prodotto con codice: "
                + listCartItemsDb.get(0).getProduct().getCode() + " non e' corretta.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardNumber_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        CreditCard creditCardInsert = new CreditCard();
        creditCardInsert.setNumber("1234567890123457");
        creditCardInsert.setCardSecurityCode("124");
        creditCardInsert.setExpirationDate("2031-03");
        creditCardInsert.setName("Franco");
        creditCardInsert.setSurname("Marinò");
        creditCardInsert.setBalance(1.0);
        creditCardInsert.setId(2L);
        creditCardInsert.setActive(false);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(new ArrayList<>());
        clientDto.getCreditCardsDto().add(new CreditCardDto(creditCardInsert));

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.empty());

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non e' stata trovata nessuna carta di credito.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardSecurityCode_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        CreditCard creditCardInsert = new CreditCard();
        creditCardInsert.setNumber("1234567890123456");
        creditCardInsert.setCardSecurityCode("124");
        creditCardInsert.setExpirationDate("2031-03");
        creditCardInsert.setName("Franco");
        creditCardInsert.setSurname("Marinò");
        creditCardInsert.setBalance(1.0);
        creditCardInsert.setId(2L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(new ArrayList<>());
        clientDto.getCreditCardsDto().add(new CreditCardDto(creditCardInsert));

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non e' stata trovata nessuna carta di credito.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(creditCardRepository, times(1)).findByNumber(creditCard.getNumber());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardInsert_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        CreditCard creditCardInsert = new CreditCard();
        creditCardInsert.setNumber("1234567890123456");
        creditCardInsert.setCardSecurityCode("123");
        creditCardInsert.setExpirationDate("2030-03");
        creditCardInsert.setName("Andrea");
        creditCardInsert.setSurname("Marino");
        creditCardInsert.setBalance(1.0);
        creditCardInsert.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(new ArrayList<>());
        clientDto.getCreditCardsDto().add(new CreditCardDto(creditCardInsert));

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("La carta di credito non appartiene a questo cliente.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(creditCardRepository, times(1)).findByNumber(creditCard.getNumber());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardInsertExpirationDate_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2000-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        CreditCard creditCardInsert = new CreditCard();
        creditCardInsert.setNumber("1234567890123456");
        creditCardInsert.setCardSecurityCode("123");
        creditCardInsert.setExpirationDate("2000-03");
        creditCardInsert.setName("Andrea");
        creditCardInsert.setSurname("Marino");
        creditCardInsert.setBalance(1.0);
        creditCardInsert.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(new ArrayList<>());
        clientDto.getCreditCardsDto().add(new CreditCardDto(creditCardInsert));

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("La carta di credito e' scaduta.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(creditCardRepository, times(1)).findByNumber(creditCard.getNumber());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardExpirationDate_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        CreditCard creditCardInsert = new CreditCard();
        creditCardInsert.setNumber("1234567890123456");
        creditCardInsert.setCardSecurityCode("123");
        creditCardInsert.setExpirationDate("2031-03");
        creditCardInsert.setName("Franco");
        creditCardInsert.setSurname("Marinò");
        creditCardInsert.setBalance(1.0);
        creditCardInsert.setId(2L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(new ArrayList<>());
        clientDto.getCreditCardsDto().add(new CreditCardDto(creditCardInsert));

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non e' stata trovata nessuna carta di credito.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(creditCardRepository, times(1)).findByNumber(creditCard.getNumber());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardName_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        CreditCard creditCardInsert = new CreditCard();
        creditCardInsert.setNumber("1234567890123456");
        creditCardInsert.setCardSecurityCode("123");
        creditCardInsert.setExpirationDate("2030-03");
        creditCardInsert.setName("Franco");
        creditCardInsert.setSurname("Marinò");
        creditCardInsert.setBalance(1.0);
        creditCardInsert.setId(2L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(new ArrayList<>());
        clientDto.getCreditCardsDto().add(new CreditCardDto(creditCardInsert));

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non e' stata trovata nessuna carta di credito.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(creditCardRepository, times(1)).findByNumber(creditCard.getNumber());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardSurname_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        CreditCard creditCardInsert = new CreditCard();
        creditCardInsert.setNumber("1234567890123456");
        creditCardInsert.setCardSecurityCode("123");
        creditCardInsert.setExpirationDate("2030-03");
        creditCardInsert.setName("Andrea");
        creditCardInsert.setSurname("Marinò");
        creditCardInsert.setBalance(1.0);
        creditCardInsert.setId(2L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(new ArrayList<>());
        clientDto.getCreditCardsDto().add(new CreditCardDto(creditCardInsert));

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(1.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(creditCardRepository.findByNumber(anyString())).thenReturn(Optional.of(creditCard));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non e' stata trovata nessuna carta di credito.", exception.getMessage());

        verify(addressRepository, times(1)).findByCode(address.getCode());
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(cartRepository, times(1)).getCartOfClient(client.getUsername());
        verify(creditCardRepository, times(1)).findByNumber(creditCard.getNumber());
    }

    @Test
    public void insertOrderDto_WhenInvalidUsernameNull_NotFoundException() {
        // Setup
        String username = null;

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        client.setCreditCards(new ArrayList<>());

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertOrderDto_WhenInvalidUsernameEmpty_NotFoundException() {
        // Setup
        String username = "";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        client.setCreditCards(new ArrayList<>());

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardSize_IllegalArgumentException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.getCreditCardsDto().add(new CreditCardDto());

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        // Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Ricordati di inserire una sola carta di credito.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditCardNull_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);
        clientDto.setCreditCardsDto(null);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Il valore della carta di credito inserita, non puo' essere nullo.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertOrderDto_WhenInvalidCreditClientDtoNull_NotFoundException() {
        // Setup
        String username = "andrysea";

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(1.0);
        creditCard.setId(1L);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = null;

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Le informazioni sull'utente non sono state inserite", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertOrderDto_WhenInvalidTotalNull_NotFoundException() {
        // Setup
        String username = "andrysea";
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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
        orderDto.setTotal(null);
        orderDto.setCartDto(cartDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Il totale inserito non puo' essere nullo.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertOrderDto_WhenValidInputAddressIsDeactivate_IllegalStateException() {
        // Setup
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        CreditCard creditCard = new CreditCard(creditCardDto);
        creditCard.setId(1L);
        creditCard.setActive(true);

        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(productDto.getQuantity() + 1);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        ClientDto clientDto = new ClientDto(client);

        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

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
        address.setActive(false);

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(stateWatingApproval));
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(addressRepository.findByCode(anyString())).thenReturn(Optional.of(address));

        // Test
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("L'indirizzo inserito non e' attivo.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(addressRepository, times(1)).findByCode(address.getCode());
    }

    @Test
    public void insertOrderDto_WhenInvalidProduct_NotFoundException() {
        // Setup
        String username = "andrysea";
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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
        orderDto.setCartDto(cartDto);
        orderDto.setAddressDto(addressDto);

        // Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Valori inseriti errati, per il prodotto nel carrello con codice: "
                + cartItemDto.getProductDto().getCode(), exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertOrderDto_WhenInvalidProductCodeNull_NotFoundException() {
        // Setup
        String username = "andrysea";
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", null, "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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
        orderDto.setCartDto(cartDto);
        orderDto.setAddressDto(addressDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non e' stato inserito il codice di un prodotto, presente nel carrello.", exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidProductCodeEmpty_NotFoundException() {
        // Setup
        String username = "andrysea";
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        byte[] byteArray = new byte[10];

        ProductDto productDto = new ProductDto("Shampoo", "", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);
        Product product = new Product(productDto);
        product.setId(1L);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(null);
        cartItemDto.setImagePrescription(byteArray);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        List<CartItem> listCartItemsDb = listCartItemDto.stream().map(entity -> new CartItem(entity))
                .collect(Collectors.toList());

        listCartItemsDb.get(0).getProduct().setQuantity(1);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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
        orderDto.setCartDto(cartDto);
        orderDto.setAddressDto(addressDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non e' stato inserito il codice di un prodotto, presente nel carrello.", exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidAddressDtoNull_NotFoundException() {
        // Setup
        String username = "andrysea";

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(null);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

        AddressDto addressDto = null;

        OrderDto orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);
        orderDto.setAddressDto(addressDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("L'indirizzo inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidCartItemDtoNull_NotFoundException() {
        // Setup
        String username = "andrysea";

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(null);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        Cart cart = new Cart();
        cart.setId(1L);

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
        orderDto.setCartDto(cartDto);
        orderDto.setAddressDto(addressDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non puoi inserire degli oggetti nel carrello nulli.", exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidProductDtoNull_NotFoundException() {
        // Setup
        String username = "andrysea";

        CartItemDto cartItemDto = new CartItemDto();

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        CartDto cartDto = new CartDto();
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
        orderDto.setCartDto(cartDto);
        orderDto.setAddressDto(addressDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Non puoi inserire degli oggetti nel carrello nulli.", exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidGetCartItemsDtoNull_NotFoundException() {
        // Setup
        String username = "andrysea";

        List<CartItemDto> listCartItemDto = null;

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        OrderDto orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("La lista dei prodotti, inseriti nel carrello, non puo' essere nulla o vuota.",
                exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidGetCartItemsDtoEmpty_NotFoundException() {
        // Setup
        String username = "andrysea";

        List<CartItemDto> listCartItemDto = new ArrayList<>();

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);

        OrderDto orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("La lista dei prodotti, inseriti nel carrello, non puo' essere nulla o vuota.",
                exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidCartDtoNull_NotFoundException() {
        // Setup
        String username = "andrysea";

        CartDto cartDto = null;
        OrderDto orderDto = new OrderDto();

        orderDto.setCartDto(cartDto);

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("Il carello non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertOrderDto_WhenInvalidOrderDtoNull_NotFoundException() {
        // Setup
        String username = "andrysea";
        OrderDto orderDto = null;

        // Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.insertOrderDto(orderDto, username));
        assertEquals("L'ordine inserito non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void getListOrderWithoutSpecificState_WhenValid_ReturnList(){
        //Setup
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
        StateInTransit stateInTransit = new StateInTransit();

        Order order = new Order();
        order.setCart(cart);
        order.setClient(client);
        order.setState(statePreparation);

        List<Order> listOrder = new ArrayList<>();
        listOrder.add(order);

        OrderDto orderDto = new OrderDto(order);

        //Mock
        when(orderRepository.findAllWithoutStates(anyLong(), anyLong())).thenReturn(listOrder);
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateInTransit));

        //Test
        List<OrderDto> listOrderDtoReturned = orderServiceImpl.getListOrderWithoutSpecificState();
        assertEquals(orderDto, listOrderDtoReturned.get(0));
    }

    @Test
    public void updateOrder_WhenValidInputFlagFalse_ReturnObject(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateInTransit stateInTransit = new StateInTransit();
        StatePreparation statePreparation = new StatePreparation();
        StateDto statePreparationDto = new StateDto(statePreparation);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(statePreparationDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        order.setState(stateInTransit);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        //Test
        OrderDto orderDtoUpdated = orderServiceImpl.updateOrderDto(orderDto, flag);
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());
        verify(orderRepository, times(1)).save(orderCaptor.capture());
       
        assertEquals(orderDto, orderDtoUpdated);
    }

    @Test
    public void updateOrder_WhenValidInputStateDeleted_ReturnObject(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setCode(productDto.getCode());
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateDeleted stateDeleted = new StateDeleted();
        StateDto stateDeletedDto = new StateDto(stateDeleted);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDeletedDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        order.setCreditCard(new CreditCard(creditCardDto));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDeleted));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        //Test
        OrderDto orderDtoUpdated = orderServiceImpl.updateOrderDto(orderDto, flag);
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());
        verify(orderRepository, times(1)).save(orderCaptor.capture());
       
        cartItemDto.getProductDto().setQuantity(cartItemDto.getProductDto().getQuantity() + cartItemDto.getQuantity());
        assertEquals(orderDto, orderDtoUpdated);
    }

    @Test
    public void updateOrder_WhenInvalidStatePreparation_IllegalArgumentException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StatePreparation statePreparation = new StatePreparation();
        StateDto statePreparationDto = new StateDto(statePreparation);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(statePreparationDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));


        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Non e' possibile ritornare allo stato precedente.", exception.getMessage());
       
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());       
    }

    @Test
    public void updateOrder_WhenValidInputFlagTrue_ReturnObject(){
        //Setup 
        boolean flag = true;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateInTransit stateInTransit = new StateInTransit();
        StatePreparation statePreparation = new StatePreparation();
        StateDto statePreparationDto = new StateDto(statePreparation);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(statePreparationDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        //Test
        OrderDto orderDtoUpdated = orderServiceImpl.updateOrderDto(orderDto, flag);
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());
        verify(orderRepository, times(1)).save(orderCaptor.capture());
       
        StateDto stateInTransitDto = new StateDto(stateInTransit);
        orderDto.setStateDto(stateInTransitDto);
        assertEquals(orderDto, orderDtoUpdated);
    }

    @Test
    public void updateOrder_WhenInvalidStateDelivered_IllegalArgumentException(){
        //Setup 
        boolean flag = true;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateDelivered stateDelivered = new StateDelivered();
        StateDto stateDeliveredDto = new StateDto(stateDelivered);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDeliveredDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        order.setState(stateDelivered);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Non e' possibile andare allo stato successivo.", exception.getMessage());
       
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());       
    }

    

    @Test
    public void updateOrder_WhenInvalidStateDeleted_IllegalArgumentException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateDeleted stateDeleted = new StateDeleted();
        StateDelivered stateDelivered = new StateDelivered();
        StateDto stateDeliveredDto = new StateDto(stateDelivered);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDeliveredDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        order.setState(stateDeleted);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("L'ordine non puo' essere modificato, perche' è stato eliminato.", exception.getMessage());
        
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());
    }

    @Test
    public void updateOrder_WhenInvalidStateWatingApproval_IllegalArgumentException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        StateDelivered stateDelivered = new StateDelivered();
        StateDto stateDeliveredDto = new StateDto(stateDelivered);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDeliveredDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        order.setState(stateWatingApproval);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("L'ordine non puo' essere modificato, perche' prima deve essere approvato o meno.", exception.getMessage());
        
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());
    }

    @Test
    public void updateOrder_WhenInvalidState_NotFoundException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        StateDelivered stateDelivered = new StateDelivered();
        StateDto stateDeliveredDto = new StateDto(stateDelivered);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDeliveredDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        order.setState(stateWatingApproval);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Non e' stato trovato nessuno stato con questo nome: " + (orderDto.getStateDto().getState()), exception.getMessage());
        
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(stateRepository, times(1)).findByState(orderDto.getStateDto().getState());
    }

    @Test
    public void updateOrder_WhenInvalidOrder_NotFoundException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        StateDelivered stateDelivered = new StateDelivered();
        StateDto stateDeliveredDto = new StateDto(stateDelivered);

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
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDeliveredDto);
        orderDto.setAddressDto(addressDto);

        Order order = new Order(orderDto);
        order.setState(stateWatingApproval);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Non e' stato trovato nessun ordine con questo codice: " + orderDto.getCode(), exception.getMessage());
        
        verify(orderRepository, times(1)).findByCode(order.getCode());
    }

    @Test
    public void updateOrder_WhenInvalidStateDtoNull_NotFoundException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        OrderDto orderDto = new OrderDto();
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(null);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Lo stato dell'ordine e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateOrder_WhenInvalidStateNull_NotFoundException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateDto stateDto = new StateDto();
        stateDto.setState(null);

        OrderDto orderDto = new OrderDto();
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Lo stato dell'ordine e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateOrder_WhenInvalidStateEmpty_NotFoundException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateDto stateDto = new StateDto();
        stateDto.setState("");

        OrderDto orderDto = new OrderDto();
        orderDto.setCode("123");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(stateDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Lo stato dell'ordine e' nullo o vuoto.", exception.getMessage());
    }


    @Test
    public void updateOrder_WhenInvalidCodeNull_NotFoundException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        OrderDto orderDto = new OrderDto();
        orderDto.setCode(null);
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Il codice dell'ordine e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateOrder_WhenInvalidCodeEmpty_NotFoundException(){
        //Setup 
        boolean flag = false;
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setNumber("1234567890123456");
        creditCardDto.setCardSecurityCode("123");
        creditCardDto.setExpirationDate("2030-03");
        creditCardDto.setName("Andrea");
        creditCardDto.setSurname("Marino");
        creditCardDto.setBalance(100.0);

        List<CreditCardDto> listCreditCardDto = new ArrayList<>();
        listCreditCardDto.add(creditCardDto);

        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setEmail("user@client.com");
        clientDto.setUsername(username);
        clientDto.setCreditCardsDto(listCreditCardDto);
 
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        OrderDto orderDto = new OrderDto();
        orderDto.setCode("");
        orderDto.setTotal(55.0);
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("Il codice dell'ordine e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateOrder_WhenInvalidOrderDto_NotFoundException(){
        //Setup 
        boolean flag = false;
        OrderDto orderDto = null;
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.updateOrderDto(orderDto, flag));
        assertEquals("L'ordine inserito non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void getListOrderWithoutSpecificState_WhenValid_NoSuchElementException(){
        //Setup
        StatePreparation statePreparation = new StatePreparation();
        StateInTransit stateInTransit = new StateInTransit();

        List<Order> listOrder = new ArrayList<>();

        //Mock
        when(orderRepository.findAllWithoutStates(anyLong(), anyLong())).thenReturn(listOrder);
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateInTransit));

        //Test
        assertThrows(NoSuchElementException.class,
                () -> orderServiceImpl.getListOrderWithoutSpecificState());
    }

    @Test
    public void getListOrderWithoutSpecificState_WhenInvalidStateTransit_InternalError(){
        //Setup
        StatePreparation statePreparation = new StatePreparation();
        
        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        //Test
        InternalError exception = assertThrows(InternalError.class,
                () -> orderServiceImpl.getListOrderWithoutSpecificState());
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());
    }

    @Test
    public void getListOrderWithoutSpecificState_WhenInvalidStatePreparation_InternalError(){        
        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        //Test
        InternalError exception = assertThrows(InternalError.class,
                () -> orderServiceImpl.getListOrderWithoutSpecificState());
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());
    }

    @Test
    public void getListOrderWaiting_WhenValid_ReturnList(){
        //Setup
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

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        
        Order order = new Order();
        order.setCart(cart);
        order.setClient(client);
        order.setState(stateWatingApproval);

        List<Order> listOrder = new ArrayList<>();
        listOrder.add(order);

        OrderDto orderDto = new OrderDto(order);

        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateWatingApproval));
        when(orderRepository.findWaitingOrders(anyLong())).thenReturn(listOrder);

        //Test
        List<OrderDto> listOrderDtoReturned = orderServiceImpl.getListOrderWaiting();
        assertEquals(orderDto, listOrderDtoReturned.get(0));
    }

    @Test
    public void getListOrderWaiting_WhenValid_NoSuchElementException(){
        //Setup
        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

        List<Order> listOrder = new ArrayList<>();

        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateWatingApproval));
        when(orderRepository.findWaitingOrders(anyLong())).thenReturn(listOrder);

        //Test
        assertThrows(NoSuchElementException.class,
                () -> orderServiceImpl.getListOrderWaiting());
    }

    @Test
    public void getListOrderWaiting_WhenInvalidStateWatingApproval_InternalError(){
        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        //Test
        InternalError exception = assertThrows(InternalError.class,
                () -> orderServiceImpl.getListOrderWaiting());
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());
    }

    @Test
    public void getListOrderHistory_WhenValid_ReturnList(){
        //Setup
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

        StateDeleted stateDeleted = new StateDeleted();
        StateDelivered stateDelivered = new StateDelivered();
        
        Order order = new Order();
        order.setCart(cart);
        order.setClient(client);
        order.setState(stateDeleted);

        List<Order> listOrder = new ArrayList<>();
        listOrder.add(order);

        OrderDto orderDto = new OrderDto(order);

        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDeleted));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));
        when(orderRepository.findAllWithoutStates(anyLong(), anyLong())).thenReturn(listOrder);

        //Test
        List<OrderDto> listOrderDtoReturned = orderServiceImpl.getListOrderHistory();
        assertEquals(orderDto, listOrderDtoReturned.get(0));
    }

    @Test
    public void getListOrderHistory_WhenValid_NoSuchElementException(){
        //Setup
        StateDeleted stateDeleted = new StateDeleted();
        StateDelivered stateDelivered = new StateDelivered();
        
        List<Order> listOrder = new ArrayList<>();

        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDeleted));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));
        when(orderRepository.findAllWithoutStates(anyLong(), anyLong())).thenReturn(listOrder);

        //Test
        assertThrows(NoSuchElementException.class,
                () -> orderServiceImpl.getListOrderHistory());
    }

    @Test
    public void getListOrderHistory_WhenInvalidStateDelivered_InternalError(){
        //Setup
        StateDeleted stateDeleted = new StateDeleted();
        
        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDeleted));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        //Test
        InternalError exception = assertThrows(InternalError.class,
                () -> orderServiceImpl.getListOrderHistory());
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());
    }

    @Test
    public void getListOrderHistory_WhenInvalidStateDeleted_InternalError(){
        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        //Test
        InternalError exception = assertThrows(InternalError.class,
                () -> orderServiceImpl.getListOrderHistory());
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());
    }

    @Test
    public void getListOrderUser_WhenValid_ReturnList(){
        //Setup
        String username = "andrysea";
        
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

        OrderDto orderDto = new OrderDto(order);

        //Mock
        when(orderRepository.findAllByClientUsername(anyString())).thenReturn(listOrder);

        //Test
        List<OrderDto> listOrderDtoReturned = orderServiceImpl.getListOrderUser(username);
        assertEquals(orderDto, listOrderDtoReturned.get(0));
    }

    @Test
    public void getListOrderUser_WhenValid_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        List<Order> listOrder = new ArrayList<>();

        //Mock
        when(orderRepository.findAllByClientUsername(anyString())).thenReturn(listOrder);

        //Test
        assertThrows(NoSuchElementException.class,
                () -> orderServiceImpl.getListOrderUser(username));
    }

    @Test
    public void getListOrderUser_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.getListOrderUser(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getListOrderUser_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.getListOrderUser(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void approvedOrNotOrder_WhenValidInputTrue_Success(){
        //Setup 
        Boolean value = true;
        String code = "123";
        String username = "andrysea";

        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCard creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(100.0);

        List<Product> listProduct = new ArrayList<>();
        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        Category category = new Category(1L, "Shampoo", "123", listProduct);
        
        Product product = new Product();
        product.setId(1L);
        product.setCode("123");
        product.setName("Shampoo Antiforfora");
        product.setQuantity(2);
        product.setPrice(14.5);
        product.setActiveProduct(true);
        product.setPrescription(true);

        category.getProducts().add(product);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setName(product.getName());
        cartItem.setCode(product.getCode());
        cartItem.setQuantity(1);
        cartItem.setImagePrescription(pngHeader);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        Client client = new Client();
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        Cart cart = new Cart();
        cart.setClient(client);
        cart.setCartItems(listCartItem);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

        Order order = new Order();
        order.setCode("123");
        order.setTotal(55.0);
        order.setCart(cart);
        order.setClient(client);
        order.setState(stateWatingApproval);
        order.setCreditCard(client.getCreditCards().get(0));
        order.setState(stateWatingApproval);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        //Test
        orderServiceImpl.approvedOrNotOrder(code, value);
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        order.getState().next(order);
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    public void approvedOrNotOrder_WhenInvalidBalance_IllegalStateException(){
        //Setup 
        Boolean value = true;
        String code = "123";
        String username = "andrysea";
        Double total = 55.0;

        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCard creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(40.0);

        List<Product> listProduct = new ArrayList<>();
        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        Category category = new Category(1L, "Shampoo", "123", listProduct);
        
        Product product = new Product();
        product.setId(1L);
        product.setCode("123");
        product.setName("Shampoo Antiforfora");
        product.setQuantity(2);
        product.setPrice(14.5);
        product.setActiveProduct(true);
        product.setPrescription(true);

        category.getProducts().add(product);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setName(product.getName());
        cartItem.setCode(product.getCode());
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setImagePrescription(pngHeader);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        Client client = new Client();
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        Cart cart = new Cart();
        cart.setClient(client);
        cart.setCartItems(listCartItem);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

        Order order = new Order();
        order.setCode("123");
        order.setTotal(total);
        order.setCart(cart);
        order.setClient(client);
        order.setState(stateWatingApproval);
        order.setCreditCard(client.getCreditCards().get(0));
        order.setState(stateWatingApproval);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));

        //Test
        IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> orderServiceImpl.approvedOrNotOrder(code, value));
        assertEquals("Il bilancio della carta di credito, deve essere >= di " + total + "!", exception.getMessage());
        
        verify(orderRepository, times(1)).findByCode(order.getCode());
    }   

    @Test
    public void approvedOrNotOrder_WhenValidInputFalse_Success(){
        //Setup 
        Boolean value = false;
        String code = "123";
        String username = "andrysea";

        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCard creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(100.0);

        List<Product> listProduct = new ArrayList<>();
        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setEmail("pharmacist@pharmacist.com");

        Category category = new Category(1L, "Shampoo", "123", listProduct);
        
        Product product = new Product();
        product.setId(1L);
        product.setCode("123");
        product.setName("Shampoo Antiforfora");
        product.setQuantity(2);
        product.setPrice(14.5);
        product.setActiveProduct(true);
        product.setPrescription(true);
        product.setPharmacist(pharmacist);

        category.getProducts().add(product);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setCode(product.getCode());
        cartItem.setName(product.getName());
        cartItem.setImagePrescription(pngHeader);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        Client client = new Client();
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        List<Client> listClient = new ArrayList<>();
        listClient.add(client);

        Cart cart = new Cart();
        cart.setClient(client);
        cart.setCartItems(listCartItem);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

        Order order = new Order();
        order.setCode("123");
        order.setTotal(55.0);
        order.setCart(cart);
        order.setClient(client);
        order.setState(stateWatingApproval);
        order.setCreditCard(client.getCreditCards().get(0));
        order.setState(stateWatingApproval);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(orderRepository.findAllClientWhoHaveBuyADeliveredProduct(anyString(), anyString(), anyString())).thenReturn(listClient);
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        //Test
        orderServiceImpl.approvedOrNotOrder(code, value);
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        order.getState().delete(order);
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    public void approvedOrNotOrder_WhenValidInputFalseNoQuantityChange_Success(){
        //Setup 
        Boolean value = false;
        String code = "123";
        String username = "andrysea";

        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        CreditCard creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setNumber("1234567890123456");
        creditCard.setCardSecurityCode("123");
        creditCard.setExpirationDate("2030-03");
        creditCard.setName("Andrea");
        creditCard.setSurname("Marino");
        creditCard.setBalance(100.0);

        List<Product> listProduct = new ArrayList<>();
        List<CreditCard> listCreditCard = new ArrayList<>();
        listCreditCard.add(creditCard);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setEmail("pharmacist@pharmacist.com");

        Category category = new Category(1L, "Shampoo", "123", listProduct);
        
        Product product = new Product();
        product.setId(1L);
        product.setCode("123");
        product.setName("Shampoo Antiforfora");
        product.setQuantity(2);
        product.setPrice(14.5);
        product.setActiveProduct(true);
        product.setPrescription(true);
        product.setPharmacist(pharmacist);

        category.getProducts().add(product);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setCode("11");
        cartItem.setName("Shampoo");
        cartItem.setImagePrescription(pngHeader);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        Client client = new Client();
        client.setEmail("user@client.com");
        client.setUsername(username);
        client.setCreditCards(listCreditCard);

        List<Client> listClient = new ArrayList<>();
        listClient.add(client);

        Cart cart = new Cart();
        cart.setClient(client);
        cart.setCartItems(listCartItem);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

        Order order = new Order();
        order.setCode("123");
        order.setTotal(55.0);
        order.setCart(cart);
        order.setClient(client);
        order.setState(stateWatingApproval);
        order.setCreditCard(client.getCreditCards().get(0));
        order.setState(stateWatingApproval);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        //Mock
        when(orderRepository.findByCode(anyString())).thenReturn(Optional.of(order));
        when(orderRepository.findAllClientWhoHaveBuyADeliveredProduct(anyString(), anyString(), anyString())).thenReturn(listClient);
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderSaved = invocation.getArgument(0);
            return orderSaved;
        });

        //Test
        orderServiceImpl.approvedOrNotOrder(code, value);
        verify(orderRepository, times(1)).findByCode(order.getCode());
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        order.getState().delete(order);
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    public void approvedOrNotOrder_WhenInvalidInputCodeNull_NotFoundException(){
        //Setup 
        Boolean value = false;
        String code = null;
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.approvedOrNotOrder(code, value));
        assertEquals("Il codice dell'ordine e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void approvedOrNotOrder_WhenInvalidInputCodeEmpty_NotFoundException(){
        //Setup 
        Boolean value = false;
        String code = "";
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderServiceImpl.approvedOrNotOrder(code, value));
        assertEquals("Il codice dell'ordine e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void approvedOrNotOrder_WhenInvalidInputValueNull_IllegalArgumentException(){
        //Setup 
        Boolean value = null;
        String code = "123";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderServiceImpl.approvedOrNotOrder(code, value));
        assertEquals("Il valore booleano, non puo' essere nullo.", exception.getMessage());
    }    
}
