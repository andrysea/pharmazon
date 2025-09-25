package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
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
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import com.andreamarino.pharmazon.repository.CartRepository;
import com.andreamarino.pharmazon.repository.ProductRepository;
import com.andreamarino.pharmazon.repository.StateRepository;
import com.andreamarino.pharmazon.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;

    @Test
    public void insertProductDto_WhenValidInputAndExistsProduct_ReturnedObjectInsert(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        Cart cart = new Cart();
        cart.setId(1L);

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
    
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.existCartItemInCart(anyLong(), anyLong())).thenReturn(Optional.of(cartItem));

        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem cartItemSaved = invocation.getArgument(0);
            return cartItemSaved;
        });

        //Test
        ProductDto productDtoInsert = cartServiceImpl.insertProductDto(productDto, username);
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartItemRepository, times(1)).save(cartItemCaptor.capture());

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        assertEquals(cartItem, cartItemCaptor.getValue());
        assertEquals(productDto, productDtoInsert);
    }

    @Test
    public void insertProductDto_WhenValidInputAndMaxQuantity_IllegalStateException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        Cart cart = new Cart();
        cart.setId(1L);
    
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.existCartItemInCart(anyLong(), anyLong())).thenReturn(Optional.of(cartItem));

        //Test
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il prodotto con codice: " + productDto.getCode() + " e' gia' nel carrello con la quantita' massima.", exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
    }

    @Test
    public void insertProductDto_WhenValidInputAndProductNoInCart_ReturnedObjectInsert(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
    
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.existCartItemInCart(anyLong(), anyLong())).thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cartSaved = invocation.getArgument(0);
            return cartSaved;
        });

        //Test
        ProductDto productDtoInsert = cartServiceImpl.insertProductDto(productDto, username);
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        cart.getCartItems().add(cartItem);
        assertEquals(cart, cartCaptor.getValue());
        assertEquals(productDto, productDtoInsert);
    }

    @Test
    public void insertProductDto_WhenValidInputAndCartNotExist_ReturnedObjectInsert(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        
        Cart cart = new Cart();
        cartItem.setCart(cart);

        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);
        cart.setClient(client);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
    
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cartSaved = invocation.getArgument(0);
            return cartSaved;
        });

        //Test
        ProductDto productDtoInsert = cartServiceImpl.insertProductDto(productDto, username);
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        assertEquals(cart.getCartItems().get(0).getProduct(), cartCaptor.getValue().getCartItems().get(0).getProduct());
        assertEquals(productDto, productDtoInsert);
    }

    @Test
    public void insertProductDto_WhenInvalidClient_NotFoundException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        
        Cart cart = new Cart();
        cartItem.setCart(cart);

        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);
        cart.setClient(client);
    
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(userRepository, times(1)).findByUsername(username);        
    }

    @Test
    public void insertProductDto_WhenInvalidProductQuantity_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 0, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        
        Cart cart = new Cart();
        cartItem.setCart(cart);

        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);
        cart.setClient(client);
    
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il prodotto con codice " + productDto.getCode() + " non e' disponibile.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());      
    }

    @Test
    public void insertProductDto_WhenInvalidProduct_NotFoundException(){
        //Setup
        String username = "andrysea";
        Client client = new Client();
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 0, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        
        Cart cart = new Cart();
        cartItem.setCart(cart);

        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);
        cart.setClient(client);
    
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Non e' stato trovato nessun prodotto con questo codice: " + productDto.getCode(), exception.getMessage());

        verify(productRepository, times(1)).findByCode(productDto.getCode());      
    }

    @Test
    public void insertProductDto_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 0, new CategoryDto());
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void insertProductDto_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 0, new CategoryDto());
    
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void insertProductDto_WhenInvalidCodeNull_NotFoundException(){
        //Setup
        String username = "andrysea";        
        ProductDto productDto = new ProductDto("Shampoo", null, "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 0, new CategoryDto());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il codice prodotto e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void insertProductDto_WhenInvalidCodeEmpty_NotFoundException(){
        //Setup
        String username = "andrysea";        
        ProductDto productDto = new ProductDto("Shampoo", "", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 0, new CategoryDto());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il codice prodotto e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void insertProductDto_WhenInvalidProductDtoNull_NotFoundException(){
        //Setup
        String username = "andrysea";        
        ProductDto productDto = null;
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.insertProductDto(productDto, username));
        assertEquals("L'oggetto relativo al prodotto non può essere nullo.", exception.getMessage());  
    }

    @Test
    public void getCartItemsDtoCart_WhenValidInput_ReturnedList(){
        //Setup
        String username = "andrysea";
        
        Client client = new Client();
        client.setId(1L);
        
        Cart cart = new Cart();
        cart.setId(1L);

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(product.getQuantity() + 1);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);
        List<CartItemDto> listCartItemDtos = listCartItem.stream().map(entity -> new CartItemDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItem);

        //Test
        List<CartItemDto> listReturned = cartServiceImpl.getCartItemsDtoCart(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());

        listCartItemDtos.get(0).setQuantity(listCartItemDtos.get(0).getQuantity() - 1);
        assertEquals(listCartItemDtos, listReturned);
    }

    @Test
    public void getCartItemsDtoCart_WhenValidInputCartNull_ReturnedList(){
        //Setup
        String username = "andrysea";
        
        Client client = new Client();
        client.setId(1L);
        
        List<CartItem> listCartItem = new ArrayList<>();
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.empty());
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItem);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cartSaved = invocation.getArgument(0);
            cartSaved.setId(1L);
            return cartSaved;
        });

        //Test
        assertThrows(NoSuchElementException.class, () ->
            cartServiceImpl.getCartItemsDtoCart(username));

        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartRepository, times(1)).save(cartCaptor.capture());
    }

    @Test
    public void getCartItemsDtoCart_WhenValidInputEqualQuantity_ReturnedList(){
        //Setup
        String username = "andrysea";
        
        Client client = new Client();
        client.setId(1L);
        
        Cart cart = new Cart();
        cart.setId(1L);

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());
        Product product = new Product(productDto);
        product.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(product.getQuantity());

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);
        List<CartItemDto> listCartItemDtos = listCartItem.stream().map(entity -> new CartItemDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItem);

        //Test
        List<CartItemDto> listReturned = cartServiceImpl.getCartItemsDtoCart(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());

        assertEquals(listCartItemDtos, listReturned);
    }

    @Test
    public void getCartItemsDtoCart_WhenValidInputEmptyList_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        
        Client client = new Client();
        client.setId(1L);
        
        Cart cart = new Cart();
        cart.setId(1L);

        List<CartItem> listCartItem = new ArrayList<>();

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemsInCart(anyLong(), anyLong())).thenReturn(listCartItem);

        //Test
        assertThrows(NoSuchElementException.class, () ->
            cartServiceImpl.getCartItemsDtoCart(username));

        verify(userRepository, times(1)).findByUsername(username);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartItemRepository, times(1)).findCartItemsInCart(cart.getId(), client.getId());
    }

    @Test
    public void getCartItemsDtoCart_WhenValidInputAndClientNotExist_NotFoundException(){
        //Setup
        String username = "andrysea";
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.getCartItemsDtoCart(username));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + username, exception.getMessage());  

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getCartItemsDtoCart_WhenInvaliInputUsenameNull_NotFoundException(){
        //Setup
        String username = null;
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.getCartItemsDtoCart(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void getCartItemsDtoCart_WhenInvaliInputUsenameEmpty_NotFoundException(){
        //Setup
        String username = "";
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.getCartItemsDtoCart(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());  
    } 

    @Test
    public void removeProduct_WhenValidInputQuantityGreaterThan1_Success(){
        //Setup
        String username = "andrysea";
        String code = "123";

        Product product = new Product();
        product.setId(1L);
        product.setCode(code);

        CartItem cartItem= new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(0);
        
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.existCartItemInCart(anyLong(), anyLong())).thenReturn(Optional.of(cartItem));

        //Test
        cartServiceImpl.removeProduct(username, code);
        verify(productRepository, times(1)).findByCode(code);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartItemRepository, times(1)).existCartItemInCart(product.getId(), cart.getId());
        verify(cartItemRepository, times(1)).deleteCartItemFromCart(cart.getId(), cartItem.getId());
    }

    @Test
    public void removeProduct_WhenProductNotInCart_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        String code = "123";

        Product product = new Product();
        product.setId(1L);
        product.setCode(code);
        
        Cart cart = new Cart();
        cart.setId(1L);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.existCartItemInCart(anyLong(), anyLong())).thenReturn(Optional.empty());

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        cartServiceImpl.removeProduct(username, code));
        assertEquals("Il prodotto non è presente nel carrello.", exception.getMessage());  
    }

    @Test
    public void removeProduct_WhenValidInputQuantityLessThan1_Success(){
        //Setup
        String username = "andrysea";
        String code = "123";

        Product product = new Product();
        product.setId(1L);
        product.setCode(code);

        CartItem cartItem= new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);
        
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.of(cart));
        when(cartItemRepository.existCartItemInCart(anyLong(), anyLong())).thenReturn(Optional.of(cartItem));

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cartItemSaved = invocation.getArgument(0);
            return cartItemSaved;
        });

        //Test
        cartServiceImpl.removeProduct(username, code);
        verify(productRepository, times(1)).findByCode(code);
        verify(cartRepository, times(1)).getCartOfClient(username);
        verify(cartItemRepository, times(1)).existCartItemInCart(product.getId(), cart.getId());
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        cartItem.setQuantity(cartItem.getQuantity() - 1);
        assertEquals(cart, cartCaptor.getValue());
    }

    @Test
    public void removeProduct_WhenValidInputCartNull_IllegalArgumentException(){
        //Setup
        String username = "andrysea";
        String code = "123";

        Product product = new Product();
        product.setId(1L);
        product.setCode(code);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(cartRepository.getCartOfClient(anyString())).thenReturn(Optional.empty());
       
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        cartServiceImpl.removeProduct(username, code));
        assertEquals("Non hai ancora inserito nessun prodotto nel tuo carrello.", exception.getMessage());  

        verify(productRepository, times(1)).findByCode(code);
        verify(cartRepository, times(1)).getCartOfClient(username);
    }

    @Test
    public void removeProduct_WhenValidInputProductNotExist_NotFoundException(){
        //Setup
        String username = "andrysea";
        String code = "123";

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.removeProduct(username, code));
        assertEquals("Non e' stato trovato nessun prodotto con questo codice: " + code, exception.getMessage());  

        verify(productRepository, times(1)).findByCode(code);
    }

    @Test
    public void removeProduct_WhenInvalidCodeNull_NotFoundException(){
        //Setup
        String username = "andrysea";
        String code = null;
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.removeProduct(username, code));
        assertEquals("Il codice e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void removeProduct_WhenInvalidCodeEmpty_NotFoundException(){
        //Setup
        String username = "andrysea";
        String code = "";
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.removeProduct(username, code));
        assertEquals("Il codice e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void removeProduct_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;
        String code = "123";
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.removeProduct(username, code));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void removeProduct_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";
        String code = "123";
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        cartServiceImpl.removeProduct(username, code));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());  
    }

    @Test
    public void deleteCart_WhenValidInput_Success(){
        //Setup
        Long id = 1L;
        Cart cart = new Cart();
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        //Mock
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        doAnswer(invocation -> {
            Cart cartDeleted = invocation.getArgument(0);
            return cartDeleted;
        }).when(cartRepository).delete(any(Cart.class));
       
        //Test
        cartServiceImpl.deleteCart(id);
        verify(cartRepository, times(1)).findById(id);
        verify(cartRepository, times(1)).delete(cartCaptor.capture());

        assertEquals(cart, cartCaptor.getValue());
    }

    @Test
    public void deleteCart_WhenValidInputCartNotExist_NotFoundException(){
        //Setup
        Long id = 1L;

        //Mock
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            cartServiceImpl.deleteCart(id));
        assertEquals("Non e' stato trovato nessun cart con questo id: " + id, exception.getMessage());  
        
        verify(cartRepository, times(1)).findById(id);
    }

    @Test
    public void deleteCart_WhenInvalidInputIdNull_NotFoundException(){
        //Setup
        Long id = null;
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            cartServiceImpl.deleteCart(id));
        assertEquals("L'id e' nullo o <= di 0.", exception.getMessage());  
    }

    
    @Test
    public void deleteCart_WhenInvalidInputIdLessThanZero_NotFoundException(){
        //Setup
        Long id = 0L;
       
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            cartServiceImpl.deleteCart(id));
        assertEquals("L'id e' nullo o <= di 0.", exception.getMessage());  
    }
    
    @Test
    public void getCartItemListDeliveredClient_WhenValidInput_ReturnList(){
        //Setup
        String username = "andrysea";
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Category category = new Category();
        category.setId(1L);
        category.setName("Antimicotico");

        Product product = new Product();
        product.setId(1L);
        product.setCode("123");
        product.setDescription("Shampoo per capelli grassi.");
        product.setPrescription(false);
        product.setPrice(9.00);
        product.setImage(imageJpeg);
        product.setProducer("Gargnnier");
        product.setActiveIngredient("Fosfatina");
        product.setActiveProduct(false);
        product.setQuantity(5);
        product.setCategory(category);
        product.setPharmacist(pharmacist);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);

        StateDelivered stateDelivered = new StateDelivered();

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);

        List<CartItemDto> listCartItemDto = listCartItem.stream().map(entity -> new CartItemDto(entity)).collect(Collectors.toList());

        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));
        when(cartItemRepository.findCartItemsInDeliveredOrderForClient(anyLong(), anyString())).thenReturn(listCartItem);

        //Test
        List<CartItemDto> listCartItemDtoReturned = this.cartServiceImpl.getCartItemListDeliveredClient(username);
        verify(stateRepository, times(1)).findByState(stateDelivered.getState());
        verify(cartItemRepository, times(1)).findCartItemsInDeliveredOrderForClient(stateDelivered.getId(), username);

        assertEquals(listCartItemDto, listCartItemDtoReturned);
    }

    @Test
    public void getCartItemListDeliveredClient_WhenValidInput_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        StateDelivered stateDelivered = new StateDelivered();
        List<CartItem> listCartItem = new ArrayList<>();

        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));
        when(cartItemRepository.findCartItemsInDeliveredOrderForClient(anyLong(), anyString())).thenReturn(listCartItem);

        //Test
        assertThrows(NoSuchElementException.class,
            () -> this.cartServiceImpl.getCartItemListDeliveredClient(username));
        
        verify(stateRepository, times(1)).findByState(stateDelivered.getState());
        verify(cartItemRepository, times(1)).findCartItemsInDeliveredOrderForClient(stateDelivered.getId(), username);
    }

    @Test
    public void getCartItemListDeliveredClient_WhenValidInput_InternalError(){
        //Setup
        String username = "andrysea";
        StateDelivered stateDelivered = new StateDelivered();

        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());

        //Test
        InternalError exception = assertThrows(InternalError.class,
        () -> this.cartServiceImpl.getCartItemListDeliveredClient(username));
        assertEquals("Errore nella ricerca dello stato.", exception.getMessage());

        verify(stateRepository, times(1)).findByState(stateDelivered.getState());
    }

    @Test
    public void getCartItemListDeliveredClient_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.cartServiceImpl.getCartItemListDeliveredClient(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getCartItemListDeliveredClient_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.cartServiceImpl.getCartItemListDeliveredClient(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }
}
