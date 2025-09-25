package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.model.designPattern.observer.product.ObserverProduct;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class ProductTest {

    private Product product;

    @Mock
    private Client client;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    public void conversionDto(){
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, jpgHeader,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        product = new Product(productDto);
        product.setId(1L);
        product.setPharmacist(pharmacist);
    }

    @Test
    public void allArgsConstructor(){
        Long id = 1L;
        String code = "123";
        String name = "Shampoo";
        String description = "Prodotto molto utile per la forfora.";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        Integer quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        Category category = new Category(1L, "123", "Antimicotico", new ArrayList<>());
        Timestamp dateTimeCreation = new Timestamp(0);

        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        client.setId(1L);
        client.setEmail("user@client.com");

        Cart cart = new Cart();
        cart.setId(1L);

        List<Cart> listCart = new ArrayList<>();
        listCart.add(cart);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("pharmacist@doctor.com");

        List<CartItem> listCartItem = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setQuantity(2);

        List<ObserverProduct> observersProduct = new ArrayList<>();
        observersProduct.add(client);
        
        product = new Product(id, code, name, description, prescription, price, producer, activeIngredient, activeProduct, quantity, image, category, listCartItem, pharmacist, dateTimeCreation, listCart, observersProduct);

        assertEquals(id, product.getId());
        assertEquals(code, product.getCode());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(prescription, product.getPrescription());
        assertEquals(price, product.getPrice());
        assertEquals(producer, product.getProducer());
        assertEquals(activeIngredient, product.getActiveIngredient());
        assertEquals(activeProduct, product.getActiveProduct());
        assertEquals(quantity, product.getQuantity());
        assertEquals(image, product.getImage());
        assertEquals(category, product.getCategory());
        assertEquals(listCartItem, product.getCartItems());
        assertEquals(pharmacist, product.getPharmacist());
        assertEquals(dateTimeCreation, product.getDateTimeCreation());
        assertEquals(listCart, product.getCart());
        assertEquals(observersProduct, product.getObserversProduct());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(product);
    }

    @Test
    public void getterAndSetter(){
        Long id = 1L;
        String code = "123";
        String name = "Shampoo";
        String description = "Prodotto molto utile per la forfora.";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        Integer quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        Category category = new Category(1L, "123", "Antimicotico", new ArrayList<>());
        Timestamp dateTimeCreation = new Timestamp(0);

        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        client.setId(1L);
        client.setEmail("user@client.com");

        Cart cart = new Cart();
        cart.setId(1L);

        List<Cart> listCart = new ArrayList<>();
        listCart.add(cart);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("pharmacist@doctor.com");

        List<CartItem> listCartItem = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setQuantity(2);

        List<ObserverProduct> observersProduct = new ArrayList<>();
       
        product.setId(id);
        product.setCode(code);
        product.setName(name);
        product.setDescription(description);
        product.setPrescription(prescription);
        product.setPrice(price);
        product.setProducer(producer);
        product.setActiveIngredient(activeIngredient);
        product.setActiveProduct(activeProduct);
        product.setQuantity(quantity);
        product.setImage(image);
        product.setCategory(category);
        product.setCartItems(listCartItem);
        product.setPharmacist(pharmacist);
        product.setDateTimeCreation(dateTimeCreation);
        product.setCart(listCart);
        product.setObserversProduct(observersProduct);

        product.addObserver(client);
        product.removeObserver(client);
    }

    @Test
    public void toStringMethod(){
        Long id = 1L;
        String code = "123";
        String name = "Shampoo";
        String description = "Prodotto molto utile per la forfora.";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        Integer quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        Category category = new Category(1L, "123", "Antimicotico", new ArrayList<>());
        Timestamp dateTimeCreation = new Timestamp(0);

        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        client.setId(1L);
        client.setEmail("user@client.com");

        Cart cart = new Cart();
        cart.setId(1L);

        List<Cart> listCart = new ArrayList<>();
        listCart.add(cart);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("pharmacist@doctor.com");

        List<CartItem> listCartItem = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setQuantity(2);

        List<ObserverProduct> observersProduct = new ArrayList<>();
        observersProduct.add(client);
        
        product = new Product(id, code, name, description, prescription, price, producer, activeIngredient, activeProduct, quantity, image, category, listCartItem, pharmacist, dateTimeCreation, listCart, observersProduct);

        String toStringResult = product.toString();
        String expectedToString = String.format("Product(id=%d, code=%s, name=%s, description=%s, prescription=%s, price=%s, producer=%s, activeIngredient=%s, activeProduct=%s, quantity=%s, image=%s, category=%s, cartItems=%s, pharmacist=%s, dateTimeCreation=%s, cart=%s, observersProduct=%s)",
            id, code, name, description, prescription, price, producer, activeIngredient, activeProduct, quantity, Arrays.toString(image), category, listCartItem, pharmacist, dateTimeCreation, listCart, observersProduct);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        Long id = 1L;
        String code = "123";
        String name = "Shampoo";
        String description = "Prodotto molto utile per la forfora.";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        Integer quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        Category category = new Category(1L, "123", "Antimicotico", new ArrayList<>());
        Timestamp dateTimeCreation = new Timestamp(0);

        String username = "andrysea";
        Client client = new Client();
        client.setUsername(username);
        client.setId(1L);
        client.setEmail("user@client.com");

        Cart cart = new Cart();
        cart.setId(1L);

        List<Cart> listCart = new ArrayList<>();
        listCart.add(cart);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("pharmacist@doctor.com");

        List<CartItem> listCartItem = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setQuantity(2);

        List<ObserverProduct> observersProduct = new ArrayList<>();
        
        pharmacist.setId(1L);
        pharmacist.setEmail(pharmacist.getEmail());
        
        observersProduct.add(pharmacist);
        observersProduct.add(client);
        
        product = new Product(id, code, name, description, prescription, price, producer, activeIngredient, activeProduct, quantity, image, category, listCartItem, pharmacist, dateTimeCreation, listCart, observersProduct);

        Product sameProduct = new Product();
        sameProduct.setId(id);
        sameProduct.setCode(code);
        sameProduct.setName(name);
        sameProduct.setDescription(description);
        sameProduct.setPrescription(prescription);
        sameProduct.setPrice(price);
        sameProduct.setProducer(producer);
        sameProduct.setActiveIngredient(activeIngredient);
        sameProduct.setActiveProduct(activeProduct);
        sameProduct.setQuantity(quantity);
        sameProduct.setImage(image);
        sameProduct.setCategory(category);
        sameProduct.setCartItems(listCartItem);
        sameProduct.setPharmacist(pharmacist);
        sameProduct.setDateTimeCreation(dateTimeCreation);
        sameProduct.setCart(listCart);
        sameProduct.setObserversProduct(observersProduct);

        assertEquals(product, sameProduct);
        assertEquals(product.hashCode(), sameProduct.hashCode());

        sameProduct.setCode("456");

        assertNotEquals(product, sameProduct);
        assertNotEquals(product.hashCode(), sameProduct.hashCode());
    }


    @Test
    public void notifyObservers_WhenValidInput_Success(){
        //Setup
        client.setId(1L);
        client.setEmail("user@client.com");

        List<ObserverProduct> observersProduct = new ArrayList<>();
        observersProduct.add(client);
        product.setObserversProduct(observersProduct);

        //Mock
        doNothing().when(client).updateProduct(any(Product.class), any(EmailService.class));

        //Test
        product.notifyObservers(emailService);

    }
    
}
