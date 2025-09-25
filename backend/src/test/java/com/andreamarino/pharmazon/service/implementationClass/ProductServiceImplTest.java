package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import com.andreamarino.pharmazon.repository.CategoryRepository;
import com.andreamarino.pharmazon.repository.OrderRepository;
import com.andreamarino.pharmazon.repository.ProductRepository;
import com.andreamarino.pharmazon.repository.StateRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    public void insertProductDto_WhenValidInputProductNotFound_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product product = new Product(productDto);
        product.setId(1L);
        product.setPharmacist(pharmacist);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(pharmacist));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productSaved = invocation.getArgument(0);
            return productSaved;
        });

        //Test
        ProductDto productDtoInsert = this.productServiceImpl.insertProductDto(productDto, username);
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(categoryRepository, times(1)).findByName(category.getName());
        verify(userRepository, times(1)).findByUsername(username);
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertEquals(productDto, productDtoInsert);
    }

    @Test
    public void insertProductDto_WhenValidInputPharmacistNotFound_NotFoundException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Non e' stato trovato nessun utente con questo username.", exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(categoryRepository, times(1)).findByName(category.getName());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void insertProductDto_WhenValidInputCategoryNotFound_NotFoundException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Non e' stata trovata nessuna categoria con questo nome.", exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(productDto.getCode());
        verify(categoryRepository, times(1)).findByName(category.getName());
    }

    @Test
    public void insertProductDto_WhenValidInputProductFound_DuplicateException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        Product product = new Product(productDto);

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nuovo codice relativo al prodotto, fa riferimento ad un altro prodotto.", exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(productDto.getCode());
    }

    @Test
    public void insertProductDto_WhenInvalidInputQuantityNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, null, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il valore dell'ammontare del prodotto inserito non puo' essere nullo o < 0.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputQuantityLessThan0_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il valore dell'ammontare del prodotto inserito non puo' essere nullo o < 0.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputCategoryNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = null;
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("La categoria del prodotto inserito non puo' essere nulla.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputPriceNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, null, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il prezzo del prodotto inserito non puo' essere nullo o <= 0.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputPriceLessThan0_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 0.0, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il prezzo del prodotto inserito non puo' essere nullo o <= 0.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputDescriptionNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", null, true, 100.0, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("La descrizione del prodotto inserito non puo' essere nullo o vuota.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputDescriptionBlank_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", " ", true, 100.0, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("La descrizione del prodotto inserito non puo' essere nullo o vuota.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputDescriptionLength_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo Nutriente: Arricchito con oli nutrienti e vitamine, questo shampoo deterge delicatamente i capelli mentre li nutre in profondità, lasciandoli morbidi, lucenti e visibilmente più sani. La sua formula delicata è adatta per un uso quotidiano.", true, 100.0, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("La descrizione del prodotto puo' avere massimo 200 caratteri.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputActiveProductNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", true, 100.0, imageJpg,
                "Gargnier", "Fosfato", null, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il valore relativo all'attivazione o meno del prodotto, non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputPrescriptionNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", null, 100.0, imageJpg,
                "Gargnier", "Fosfato", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il valore relativo alla richiesta o meno di una prescrizione, non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputActiveIngredientNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", null, 100.0, imageJpg,
                "Gargnier", null, true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome dell'ingrediente attivo, e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputActiveIngredientEmpty_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", null, 100.0, imageJpg,
                "Gargnier", "", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome dell'ingrediente attivo, e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputActiveIngredientLength_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", true, 100.0, imageJpg,
                "Gargnier", "Acido Poliglutammicoo", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome dell'ingrediente attivo puo' avere massimo 20 caratteri.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputProducerNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", null, 100.0, imageJpg,
                null, "Zolfo", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome del produttore e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputProducerLength_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", true, 100.0, imageJpg,
                "Tecnologie Cosmetiche Avanzate S.p.A.", "Zolfo", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome del produttore puo' avere massimo 30 caratteri.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputProducerEmpty_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", null, 100.0, imageJpg,
                "", "Zolfo", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome del produttore e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputNameNull_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto(null, "123", "Descrizione..", null, 100.0, imageJpg,
                "Garnier", "Zolfo", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome del prodotto e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputNameLength_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Crema Idratante Antirughe con Vitamina E e SPF 30", "123", "Descrizione..", true, 100.0, imageJpg,
                "Garnier", "Zolfo", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome del prodotto puo' avere massimo 40 caratteri.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputNameEmpty_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("", "123", "Descrizione..", null, 100.0, imageJpg,
                "Garnier", "Zolfo", true, -1, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il nome del prodotto e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputProductNull_NotFoundException(){
        //Setup
        String username = "andrysea";
        ProductDto productDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il prodotto inserito non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputCodeLength_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123-ABCNGFHLM-ABCFGH-LMN-123-ABCNGFHLM-ABCFGH-LMN-123-ABCNGFHLM-ABCFGH-LMN-123-ABCNGFHLM-ABCFGH-LMN", "Descrizione..", true, 100.0, imageJpg,
                "Gargnier", "Fosfato", true, 10, categoryDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Il codice del prodotto puo' avere massimo 40 caratteri.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputUsernameNull_NotFoundException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = null;

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", null, 100.0, imageJpg,
                "Gargnier", "Fosfato", true, 10, categoryDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertProductDto_WhenInvalidInputUsernameEmpty_NotFoundException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");      
        ProductDto productDto = new ProductDto("Shampoo", "123", "Descrizione..", null, 100.0, imageJpg,
                "Gargnier", "Fosfato", true, 10, categoryDto);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> this.productServiceImpl.insertProductDto(productDto, username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateProductDto_WhenValidInputNewQuantity0_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        CategoryDto categoryDtoOld = new CategoryDto("Anticoagulante", "456");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);

        Category categoryOld = new Category(categoryDtoOld);
        categoryNew.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "567", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 0, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product productNew = new Product(productDtoNew);
        productNew.setCategory(categoryNew);
        productNew.setId(1L);
        productNew.setPharmacist(pharmacist);

        Product productOld = new Product();
        productOld.setId(1L);
        productOld.setCode(oldCode);
        productOld.setDescription("Shampoo per capelli grassi.");
        productOld.setPrescription(false);
        productOld.setPrice(9.00);
        productOld.setImage(imageJpeg);
        productOld.setProducer("Gargnnier");
        productOld.setActiveIngredient("Fosfatina");
        productOld.setActiveProduct(false);
        productOld.setQuantity(2);
        productOld.setCategory(categoryOld);
        productOld.setPharmacist(pharmacist);

        Order order = new Order();
        order.setState(new StatePreparation());

        Client client = new Client();
        client.setEmail("user@user.com");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setOrders(new ArrayList<>());
        cart.getOrders().add(order);
        cart.setClient(client);

        CartItem cartItem = new CartItem();
        cartItem.setCode(oldCode);
        cartItem.setName(productOld.getName());
        cartItem.setCart(cart);
        cartItem.setFlag(false);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCode(oldCode);
        cartItem2.setName(productOld.getName());
        cartItem2.setCart(cart);
        cartItem.setFlag(true);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);
        listCartItem.add(cartItem2);

        productOld.setCartItems(listCartItem);
        productNew.setCartItems(listCartItem);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        List<Client> listClient = new ArrayList<>();
        listClient.add(client);

        productNew.addObserver(productNew.getPharmacist());
        productNew.addObserver(client);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.of(productOld));
        when(orderRepository.findAllClientWhoHaveBuyADeliveredProduct(anyString(), anyString(), anyString())).thenReturn(listClient);
        when(productRepository.findByCode(productNew.getCode())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(categoryNew));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productSaved = invocation.getArgument(0);
            return productSaved;
        });

        //Test
        ProductDto productDtoUpdated = this.productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist);
        verify(productRepository, times(1)).findByCode(productOld.getCode());
        verify(productRepository, times(1)).findByCode(productNew.getCode());
        verify(orderRepository, times(1)).findAllClientWhoHaveBuyADeliveredProduct(new StateDelivered().getState(), productOld.getCode(), productOld.getName());
        verify(categoryRepository, times(1)).findByName(categoryNew.getName());
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertEquals(productDtoNew, productDtoUpdated);
        assertEquals(productCaptor.getValue(), productNew);
    }

    @Test
    public void updateProductDto_WhenValidInputOrderStateWatingApproval_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        CategoryDto categoryDtoOld = new CategoryDto("Anticoagulante", "456");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);

        Category categoryOld = new Category(categoryDtoOld);
        categoryNew.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "567", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 0, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product productNew = new Product(productDtoNew);
        productNew.setCategory(categoryNew);
        productNew.setId(1L);
        productNew.setPharmacist(pharmacist);

        Product productOld = new Product();
        productOld.setId(1L);
        productOld.setCode(oldCode);
        productOld.setDescription("Shampoo per capelli grassi.");
        productOld.setPrescription(false);
        productOld.setPrice(9.00);
        productOld.setImage(imageJpeg);
        productOld.setProducer("Pantene");
        productOld.setActiveIngredient("Fosfatina");
        productOld.setActiveProduct(false);
        productOld.setQuantity(2);
        productOld.setCategory(categoryOld);
        productOld.setPharmacist(pharmacist);

        Order order = new Order();
        order.setState(new StateWaitingApproval());

        Order order2 = new Order();
        order2.setState(new StatePreparation());

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setOrders(new ArrayList<>());
        cart.getOrders().add(order2);
        cart.getOrders().add(order);

        CartItem cartItem = new CartItem();
        cartItem.setCode(oldCode);
        cartItem.setName(productOld.getName());
        cartItem.setCart(cart);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCode(oldCode);
        cartItem2.setName(productOld.getName());
        cartItem2.setCart(cart);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);
        listCartItem.add(cartItem2);

        productOld.setCartItems(listCartItem);

        Client client = new Client();
        client.setEmail("user@user.com");

        List<Client> listClient = new ArrayList<>();
        listClient.add(client);

        productNew.addObserver(productNew.getPharmacist());
        productNew.addObserver(client);

        //Mock
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.of(productOld));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist));
        assertEquals("Prodotto non modificabile.\nCi sono degli ordini che devono essere ancora approvati.", exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(productOld.getCode());
    }

    @Test
    public void updateProductDto_WhenValidInputNewQuantity2_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        CategoryDto categoryDtoOld = new CategoryDto("Anticoagulante", "456");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);

        Category categoryOld = new Category(categoryDtoOld);
        categoryNew.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "567", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product productNew = new Product(productDtoNew);
        productNew.setCategory(categoryNew);
        productNew.setId(1L);
        productNew.setPharmacist(pharmacist);

        Product productOld = new Product();
        productOld.setId(1L);
        productOld.setCode(oldCode);
        productOld.setDescription("Shampoo per capelli grassi.");
        productOld.setPrescription(false);
        productOld.setPrice(9.00);
        productOld.setImage(imageJpeg);
        productOld.setProducer("Gargnnier");
        productOld.setActiveIngredient("Fosfatina");
        productOld.setActiveProduct(false);
        productOld.setQuantity(0);
        productOld.setCategory(categoryOld);
        productOld.setPharmacist(pharmacist);

        Client client = new Client();
        client.setEmail("user@user.com");

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        List<Client> listClient = new ArrayList<>();
        listClient.add(client);

        productNew.addObserver(productNew.getPharmacist());
        productNew.addObserver(client);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.of(productOld));
        when(orderRepository.findAllClientWhoHaveBuyADeliveredProduct(anyString(), anyString(), anyString())).thenReturn(listClient);
        when(productRepository.findByCode(productNew.getCode())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(categoryNew));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productSaved = invocation.getArgument(0);
            return productSaved;
        });

        //Test
        ProductDto productDtoUpdated = this.productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist);
        verify(productRepository, times(1)).findByCode(productOld.getCode());
        verify(productRepository, times(1)).findByCode(productNew.getCode());
        verify(orderRepository, times(1)).findAllClientWhoHaveBuyADeliveredProduct(new StateDelivered().getState(), productOld.getCode(), productOld.getName());
        verify(categoryRepository, times(1)).findByName(categoryNew.getName());
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertEquals(productDtoNew, productDtoUpdated);
        assertEquals(productCaptor.getValue(), productNew);
    }

    @Test
    public void updateProductDto_WhenValidInputNewQuantity_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        CategoryDto categoryDtoOld = new CategoryDto("Anticoagulante", "456");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);

        Category categoryOld = new Category(categoryDtoOld);
        categoryNew.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "567", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 5, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product productNew = new Product(productDtoNew);
        productNew.setCategory(categoryNew);
        productNew.setId(1L);
        productNew.setPharmacist(pharmacist);

        Product productOld = new Product();
        productOld.setId(1L);
        productOld.setCode(oldCode);
        productOld.setDescription("Shampoo per capelli grassi.");
        productOld.setPrescription(false);
        productOld.setPrice(9.00);
        productOld.setImage(imageJpeg);
        productOld.setProducer("Gargnnier");
        productOld.setActiveIngredient("Fosfatina");
        productOld.setActiveProduct(false);
        productOld.setQuantity(0);
        productOld.setCategory(categoryOld);
        productOld.setPharmacist(pharmacist);

        Client client = new Client();
        client.setEmail("user@user.com");

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        List<Client> listClient = new ArrayList<>();
        listClient.add(client);

        productNew.addObserver(productNew.getPharmacist());
        productNew.addObserver(client);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.of(productOld));
        when(orderRepository.findAllClientWhoHaveBuyADeliveredProduct(anyString(), anyString(), anyString())).thenReturn(listClient);
        when(productRepository.findByCode(productNew.getCode())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(categoryNew));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productSaved = invocation.getArgument(0);
            return productSaved;
        });

        //Test
        ProductDto productDtoUpdated = this.productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist);
        verify(productRepository, times(1)).findByCode(productOld.getCode());
        verify(productRepository, times(1)).findByCode(productNew.getCode());
        verify(orderRepository, times(1)).findAllClientWhoHaveBuyADeliveredProduct(new StateDelivered().getState(), productOld.getCode(), productOld.getName());
        verify(categoryRepository, times(1)).findByName(categoryNew.getName());
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertEquals(productDtoNew, productDtoUpdated);
        assertEquals(productCaptor.getValue(), productNew);
    }
    
    @Test
    public void updateProductDto_WhenValidInputNoDifferents_ReturnObject(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", oldCode, "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product productNew = new Product(productDtoNew);
        productNew.setCategory(categoryNew);
        productNew.setId(1L);
        productNew.setPharmacist(pharmacist);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.of(productNew));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productSaved = invocation.getArgument(0);
            return productSaved;
        });

        //Test
        ProductDto productDtoUpdated = this.productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist);
        verify(productRepository, times(1)).findByCode(productNew.getCode());
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertEquals(productDtoNew, productDtoUpdated);
        assertEquals(productCaptor.getValue(), productNew);
    }

    @Test
    public void updateProductDto_WhenInvaliCategory_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoOld = new CategoryDto("Anticoagulante", "456");
        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);

        Category categoryOld = new Category(categoryDtoOld);
        categoryOld.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", oldCode, "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product productNew = new Product(productDtoNew);
        productNew.setCategory(categoryOld);
        productNew.setId(1L);
        productNew.setPharmacist(pharmacist);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.of(productNew));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist));
        assertEquals("Non e' stata trovata nessuna categoria con questo nome.", exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(productNew.getCode());
        verify(categoryRepository, times(1)).findByName(categoryNew.getName());
    }

    @Test
    public void updateProductDto_WhenInvaliNewCode_DuplicateException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        CategoryDto categoryDtoOld = new CategoryDto("Anticoagulante", "456");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);

        Category categoryOld = new Category(categoryDtoOld);
        categoryNew.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "567", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        Product productNew = new Product(productDtoNew);
        productNew.setCategory(categoryNew);
        productNew.setId(1L);
        productNew.setPharmacist(pharmacist);

        Product productOld = new Product();
        productOld.setId(1L);
        productOld.setCode(oldCode);
        productOld.setDescription("Shampoo per capelli grassi.");
        productOld.setPrescription(false);
        productOld.setPrice(9.00);
        productOld.setImage(imageJpeg);
        productOld.setProducer("Gargnnier");
        productOld.setActiveIngredient("Fosfatina");
        productOld.setActiveProduct(false);
        productOld.setQuantity(5);
        productOld.setCategory(categoryOld);
        productOld.setPharmacist(pharmacist);

        //Mock
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(pharmacist));
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.of(productOld));
        when(productRepository.findByCode(productNew.getCode())).thenReturn(Optional.of(productNew));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class,
        () -> productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist));
        assertEquals("Il nuovo codice relativo al prodotto, fa riferimento ad un altro prodotto.", exception.getMessage());

        verify(productRepository, times(1)).findByCode(productOld.getCode());
        verify(productRepository, times(1)).findByCode(productNew.getCode());
    }

    @Test
    public void updateProductDto_WhenInvalidProduct_IllegalArgumentException(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDtoNew = new CategoryDto("Antimicotico", "123");
        
        Category categoryNew = new Category(categoryDtoNew);
        categoryNew.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "567", "Shampoo per capelli.", true, 8.75, imageJpg,
                "Gargnier", "Fosfato", true, 2, categoryDtoNew);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        //Mock
        when(productRepository.findByCode(oldCode)).thenReturn(Optional.empty());

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> productServiceImpl.updateProductDto(productDtoNew, oldCode, pharmacist));
        assertEquals("Non e' stato trovato nessun prodotto con questo codice: " + oldCode, exception.getMessage());

        verify(productRepository, times(1)).findByCode(oldCode);
    }

    @Test
    public void getProductListDto_WhenValidInputAdmin_ReturnList(){
        //Setup
        String username = "andrysea";
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");
        pharmacist.setRole(Role.ADMIN);

        User user = new User(pharmacist);

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

        List<Product> listProduct = new ArrayList<>();
        listProduct.add(product);

        List<ProductDto> listProductDto = listProduct.stream().map(entity -> new ProductDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findAll()).thenReturn(listProduct);

        //Test
        List<ProductDto> listReturned = this.productServiceImpl.getProductListDto(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(productRepository, times(1)).findAll();

        assertEquals(listProductDto, listReturned);
    }

    @Test
    public void getProductListDto_WhenValidInputClient_ReturnList(){
        //Setup
        String username = "andrysea";
        byte[] imageJpeg = {(byte) 0xFF, (byte) 0xD8};

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(1L);
        pharmacist.setUsername("admin");
        pharmacist.setEmail("admin@admin.com");
        pharmacist.setRole(Role.ADMIN);

        Client client = new Client();
        client.setId(2L);
        client.setUsername(username);
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        User user = new User(client);
        
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

        List<Product> listProduct = new ArrayList<>();
        listProduct.add(product);

        List<ProductDto> listProductDto = listProduct.stream().map(entity -> new ProductDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findByActiveProductAndQuantity()).thenReturn(listProduct);

        //Test
        List<ProductDto> listReturned = this.productServiceImpl.getProductListDto(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(productRepository, times(1)).findByActiveProductAndQuantity();

        assertEquals(listProductDto, listReturned);
    }

    @Test
    public void getProductListDto_WhenValidInputAdmin_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        List<Product> listProduct = new ArrayList<>();

        User user = new User();
        user.setUsername(username);
        user.setId(1L);
        user.setRole(Role.ADMIN);
        user.setEmail("admin@admin.com");
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findAll()).thenReturn(listProduct);

        //Test
        assertThrows(NoSuchElementException.class,
            () -> this.productServiceImpl.getProductListDto(username));
        
        verify(userRepository, times(1)).findByUsername(username);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void getProductListDto_WhenValidInputClient_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        List<Product> listProduct = new ArrayList<>();

        User user = new User();
        user.setUsername(username);
        user.setId(1L);
        user.setRole(Role.CLIENT);
        user.setEmail("admin@admin.com");
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(productRepository.findByActiveProductAndQuantity()).thenReturn(listProduct);

        //Test
        assertThrows(NoSuchElementException.class,
            () -> this.productServiceImpl.getProductListDto(username));
        
        verify(userRepository, times(1)).findByUsername(username);
        verify(productRepository, times(1)).findByActiveProductAndQuantity();
    }

    @Test
    public void getProductListDto_WhenValidInput_NoutFoundException(){
        //Setup
        String username = "andrysea";
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductListDto(username));
        assertEquals("Non e' stato trovato nessun utente con questo username.", exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getProductListDto_WhenInvalidInputUsernameNull_NoutFoundException(){
        //Setup
        String username = null;
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductListDto(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getProductListDto_WhenInvalidInputUsernameEmpty_NoutFoundException(){
        //Setup
        String username = "";
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductListDto(username));
        assertEquals("Lo username e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getProductListDtoByName_WhenValidInput_ReturnList(){
        //Setup
        String name = "Shampoo";
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
        product.setActiveProduct(true);
        product.setQuantity(5);
        product.setCategory(category);
        product.setPharmacist(pharmacist);

        List<Product> listProduct = new ArrayList<>();
        listProduct.add(product);

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        List<ProductDto> listProductDto = listProduct.stream().map(entity -> new ProductDto(entity)).collect(Collectors.toList());

        //Mock
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));
        when(productRepository.findByName(anyString())).thenReturn(listProduct);

        //Test
        List<ProductDto> listReturned = this.productServiceImpl.getProductListDtoByName(name, client);
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(productRepository, times(1)).findByName(name);

        assertEquals(listProductDto, listReturned);
    }

    @Test
    public void getProductListDtoByNameClient_WhenValidInputQuantiy0_ReturnList(){
        //Setup
        String name = "Shampoo";
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
        product.setQuantity(0);
        product.setCategory(category);
        product.setPharmacist(pharmacist);

        List<Product> listProduct = new ArrayList<>();
        listProduct.add(product);

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));
        when(productRepository.findByName(anyString())).thenReturn(listProduct);

        //Test
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
        () -> this.productServiceImpl.getProductListDtoByName(name, client));
        verify(userRepository, times(1)).findByUsername(client.getUsername());
        verify(productRepository, times(1)).findByName(name);
    }


    @Test
    public void getProductListDtoByName_WhenValidInput_NoSuchElementException(){
        //Setup
        String name = "Shampoo";
        List<Product> listProduct = new ArrayList<>();

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");
        
        //Mock
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));
        when(productRepository.findByName(anyString())).thenReturn(listProduct);

        //Test
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
        () -> this.productServiceImpl.getProductListDtoByName(name, client));
        
        verify(productRepository, times(1)).findByName(name);
        verify(userRepository, times(1)).findByUsername(client.getUsername());
    }

    @Test
    public void getProductListDtoByName_WhenInvalidInputNameNull_NotFoundException(){
        //Setup
        String name = null;

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductListDtoByName(name, client));
        assertEquals("Il nome del prodotto e' nullo.", exception.getMessage());
    }

    @Test
    public void getProductDtoCode_WhenValidInput_ReturnObject(){
        //Setup
        String code = "123";
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
        product.setActiveProduct(true);
        product.setQuantity(5);
        product.setCategory(category);
        product.setPharmacist(pharmacist);

        ProductDto productDto = new ProductDto(product);

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));

        //Test
        ProductDto productDtoReturned = this.productServiceImpl.getProductDtoCode(code, client);
        verify(productRepository, times(1)).findByCode(code);
        verify(userRepository, times(1)).findByUsername(client.getUsername());

        assertEquals(productDto, productDtoReturned);
    }

    @Test
    public void getProductDtoCode_WhenValidInputActivateFalse_NotFoundException(){
        //Setup
        String code = "123";
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

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductDtoCode(code, client));
        assertEquals("Non e' stato trovato nessun prodotto disponibile con questo codice: " + code, exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(code);
        verify(userRepository, times(1)).findByUsername(client.getUsername());
    }

    @Test
    public void getProductDtoCode_WhenValidInputQuantity0_NotFoundException(){
        //Setup
        String code = "123";
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
        product.setActiveProduct(true);
        product.setQuantity(0);
        product.setCategory(category);
        product.setPharmacist(pharmacist);

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductDtoCode(code, client));
        assertEquals("Non e' stato trovato nessun prodotto disponibile con questo codice: " + code, exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(code);
        verify(userRepository, times(1)).findByUsername(client.getUsername());
    }

    @Test
    public void getProductDtoCode_WhenValidInputQuantity0_NotFoundExcpetion(){
        //Setup
        String code = "123";
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
        product.setQuantity(0);
        product.setCategory(category);
        product.setPharmacist(pharmacist);

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductDtoCode(code, client));
        assertEquals("Non e' stato trovato nessun prodotto disponibile con questo codice: " + code, exception.getMessage());
        verify(productRepository, times(1)).findByCode(code);
        verify(userRepository, times(1)).findByUsername(client.getUsername());

    }

    @Test
    public void getProductDtoCode_WhenValidInput_NotFoundException(){
        //Setup
        String code = "123";

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");
        
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductDtoCode(code, client));
        assertEquals("Non e' stato trovato nessun prodotto con questo codice: " + code, exception.getMessage());
        
        verify(productRepository, times(1)).findByCode(code);
        verify(userRepository, times(1)).findByUsername(client.getUsername());
    }

    @Test
    public void getProductDtoCode_WhenInvalidInputCodeNull_NotFoundException(){
        //Setup
        String code = null;

        Client client = new Client();
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductDtoCode(code, client));
        assertEquals("Il codice del prodotto e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getProductDtoCode_WhenInvalidInputCodeEmpty_NotFoundException(){
        //Setup
        String code = "";

        Client client = new Client();
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductDtoCode(code, client));
        assertEquals("Il codice del prodotto e' nullo o vuoto.", exception.getMessage());
    }


    @Test
    public void getProductListDtoByCategoryClient_WhenValidInput_ReturnList(){
        //Setup
        String name = "Antimicotico";
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

        List<Product> listProduct = new ArrayList<>();
        listProduct.add(product);

        List<ProductDto> listProductDto = listProduct.stream().map(entity -> new ProductDto(entity)).collect(Collectors.toList());

        Client client = new Client();
        client.setUsername("client");
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productRepository.findByCategoryCheckQuantity(anyString())).thenReturn(listProduct);
        when(userRepository.findByUsername(client.getUsername())).thenReturn(Optional.of(client));

        //Test
        List<ProductDto> listReturned = this.productServiceImpl.getProductListDtoByCategory(name, client);
        verify(productRepository, times(1)).findByCategoryCheckQuantity(name);
        verify(userRepository, times(1)).findByUsername(client.getUsername());

        assertEquals(listProductDto, listReturned);
    }

    @Test
    public void getProductListDtoByCategory_WhenValidInput_NoSuchElementException(){
        //Setup
        String name = "Antimicotico";
        List<Product> listProduct = new ArrayList<>();

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setRole(Role.ADMIN);
        pharmacist.setEmail("user@user.com");
        pharmacist.setUsername("pharmacist");
        
        //Mock
        when(productRepository.findByCategory(anyString())).thenReturn(listProduct);
        when(userRepository.findByUsername(pharmacist.getUsername())).thenReturn(Optional.of(pharmacist));

        //Test
        assertThrows(NoSuchElementException.class,
            () -> this.productServiceImpl.getProductListDtoByCategory(name, pharmacist));
        
        verify(productRepository, times(1)).findByCategory(name);
        verify(userRepository, times(1)).findByUsername(pharmacist.getUsername());
    }

    @Test
    public void getProductListDtoByCategory_WhenInvalidInputNameNull_NotFoundException(){
        //Setup
        String name = null;

        Client client = new Client();
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductListDtoByCategory(name, client));
        assertEquals("Il nome della categoria e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getProductListDtoByCategory_WhenInvalidInputNameEmpty_NotFoundException(){
        //Setup
        String name = "";

        Client client = new Client();
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> this.productServiceImpl.getProductListDtoByCategory(name, client));
        assertEquals("Il nome della categoria e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void activateProductDto_WhenValidInputActiveProductFalse_Success(){
        //Setup
        String code = "123";
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
        product.setActiveProduct(false);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productSaved = invocation.getArgument(0);
            return productSaved;
        });

        //Test
        productServiceImpl.activateProductDto(code);
        verify(productRepository, times(1)).findByCode(code);
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertEquals(product, productCaptor.getValue());
    }

    @Test
    public void activateProductDto_WhenValidInputActiveProductTrue_Success(){
        //Setup
        String code = "123";
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
        product.setActiveProduct(true);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setFlag(true);

        Client client = new Client();
        client.setEmail("user@user.com");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setClient(client);

        CartItem cartItemFalse = new CartItem();
        cartItemFalse.setProduct(product);
        cartItemFalse.setFlag(false);

        cartItemFalse.setCart(cart);

        product.getCartItems().add(cartItem);
        product.getCartItems().add(cartItemFalse);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.of(product));
        doNothing().when(cartItemRepository).delete(any(CartItem.class));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productSaved = invocation.getArgument(0);
            return productSaved;
        });

        //Test
        productServiceImpl.activateProductDto(code);
        verify(productRepository, times(1)).findByCode(code);
        verify(productRepository, times(1)).save(productCaptor.capture());

        assertEquals(product, productCaptor.getValue());
    }

    @Test
    public void activateProductDto_WhenInvalidProductCode_NotFoundException(){
        //Setup
        String code = "123";
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
        
        //Mock
        when(productRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> productServiceImpl.activateProductDto(code));
        assertEquals("Non e' stato trovato nessun prodotto con questo codice: " + code, exception.getMessage());

        verify(productRepository, times(1)).findByCode(code);
    }

    @Test
    public void activateProductDto_WhenInvalidCodeNull_NotFoundException(){
        //Setup
        String code = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> productServiceImpl.activateProductDto(code));
        assertEquals("Il codice del prodotto e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void activateProductDto_WhenInvalidCodeEmpty_NotFoundException(){
        //Setup
        String code = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class,
        () -> productServiceImpl.activateProductDto(code));
        assertEquals("Il codice del prodotto e' nullo o vuoto.", exception.getMessage());
    }
}
