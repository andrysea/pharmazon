package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.FeedbackDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Feedback;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import com.andreamarino.pharmazon.repository.CartRepository;
import com.andreamarino.pharmazon.repository.FeedbackRepository;
import com.andreamarino.pharmazon.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {
    
    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackServiceImpl;

    @Test
    public void insertFeedbackDto_WhenValidInput_ReturnObject(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());
        Client client = new Client(clientDto);

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        
        ProductDto productDto = new ProductDto();
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName("Shampoo");
        cartItemDto.setProductDto(productDto);

        CartItem cartItem = new CartItem(cartItemDto);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        ArgumentCaptor<Feedback> feedbackCaptor = ArgumentCaptor.forClass(Feedback.class);
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartItemRepository.findByCodeAndNameAndUsername(anyString(), anyString(), anyString())).thenReturn(listCartItem);
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback feedBackSaved = invocation.getArgument(0);
            return feedBackSaved;
        });

        //Test
        FeedbackDto feedbackDtoInsert = feedbackServiceImpl.insertFeedbackDto(feedbackDto);
        verify(userRepository, times(1)).findByUsername(clientDto.getUsername());
        verify(cartItemRepository, times(1)).findByCodeAndNameAndUsername(feedbackDto.getCartItemDto().getCode(), feedbackDto.getCartItemDto().getName(), client.getUsername());
        verify(feedbackRepository, times(1)).save(feedbackCaptor.capture());

        FeedbackDto feedbackDtoSaved = new FeedbackDto(feedbackCaptor.getValue());
        assertEquals(feedbackDtoInsert, feedbackDtoSaved);
    }

    @Test
    public void insertFeedbackDto_WhenValidInputFeedbackInsert_IllegalStateException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());
        Client client = new Client(clientDto);

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        
        ProductDto productDto = new ProductDto();
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName("Shampoo");
        cartItemDto.setProductDto(productDto);

        CartItem cartItem = new CartItem(cartItemDto);

        List<CartItem> listCartItem = new ArrayList<>();
        listCartItem.add(cartItem);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        Feedback feedback = new Feedback(feedbackDto);
        cartItem.getFeedbacks().add(feedback);
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartItemRepository.findByCodeAndNameAndUsername(anyString(), anyString(), anyString())).thenReturn(listCartItem);


        //Test
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
        feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("E' gia' stata inserita una recensione.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(clientDto.getUsername());
        verify(cartItemRepository, times(1)).findByCodeAndNameAndUsername(feedbackDto.getCartItemDto().getCode(), feedbackDto.getCartItemDto().getName(), client.getUsername());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidProduct_IllegalStateException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());
        Client client = new Client(clientDto);

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName("Shampoo");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(client));
        when(cartItemRepository.findByCodeAndNameAndUsername(anyString(), anyString(), anyString())).thenReturn(new ArrayList<>());

        //Test
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Il prodotto non e' stato acquistato in passato.", exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(clientDto.getUsername());
        verify(cartItemRepository, times(1)).findByCodeAndNameAndUsername(feedbackDto.getCartItemDto().getCode(), feedbackDto.getCartItemDto().getName(), client.getUsername());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidClient_NotFoundException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setName("Shampoo");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName("Shampoo");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Mock
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Non e' stato trovato nessun utente con questo username: " + feedbackDto.getClientDto().getUsername(), exception.getMessage());
        
        verify(userRepository, times(1)).findByUsername(clientDto.getUsername());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidUsernameNull_IllegalArgumentException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(null);
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setName("Shampoo");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName("Shampoo");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidUsernameEmpty_IllegalArgumentException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setName("Shampoo");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName("Shampoo");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Lo username non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidCartItemNameNull_IllegalArgumentException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername(null);
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setName("Shampoo");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName(null);
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Il nome del prodotto del carrello non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidCartItemNameEmpty_IllegalArgumentException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setName("Shampoo");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setName("");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Il nome del prodotto del carrello non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidProductCodeNull_IllegalArgumentException(){
        //Setup
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode(null);

        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Il codice del prodotto del carrello non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidProductCodeEmpty_IllegalArgumentException(){
        //Setup
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("");
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Il codice del prodotto del carrello non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidDescriptionNull_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription(null);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("La descrizione non puo' essere nulla o contenere solo spazi.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidDescriptionLength_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("La copertura dei test è un indicatore cruciale per valutare l'affidabilità del nostro codice sorgente.");
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("La descrizione deve essere al massimo 100 caratteri.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidDescriptionEmpty_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("");
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("La descrizione non puo' essere nulla o contenere solo spazi.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidDescriptionBlank_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription(" ");
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("La descrizione non puo' essere nulla o contenere solo spazi.", exception.getMessage());
    }

    @Test
    public void insertFeedbackDto_WhenInvalidFeedbackDto_NotFoundException(){
        //Setup
        FeedbackDto feedbackDto =  null;
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.insertFeedbackDto(feedbackDto));
        assertEquals("Il feedback non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void updateFeedbackDto_WhenValidInput_ReturnObject(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        Feedback feedback = new Feedback(feedbackDto);
        ArgumentCaptor<Feedback> feedbackCaptor = ArgumentCaptor.forClass(Feedback.class);
        
        //Mock
        when(feedbackRepository.findByCode(anyString())).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback feedBackSaved = invocation.getArgument(0);
            return feedBackSaved;
        });

        //Test
        FeedbackDto feedbackDtoInsert = feedbackServiceImpl.updateFeedbackDto(feedbackDto);
        verify(feedbackRepository, times(1)).findByCode(feedbackDto.getCode());
        verify(feedbackRepository, times(1)).save(feedbackCaptor.capture());

        FeedbackDto feedbackDtoUpdate = new FeedbackDto(feedbackCaptor.getValue());
        assertEquals(feedbackDtoInsert, feedbackDtoUpdate);
    }

    @Test
    public void updateFeedbackDto_WhenInvalidFeedback_NotFoundException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Mock
        when(feedbackRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("Non e' stato trovato nessun feedback con questo codice: " + feedbackDto.getCode(), exception.getMessage());
    
        verify(feedbackRepository, times(1)).findByCode(feedbackDto.getCode());
    }

    @Test
    public void updateFeedbackDto_WhenInvalidCodeNull_IllegalArgumentException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode(null);
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("Il valore di codice feedback inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateFeedbackDto_WhenInvalidCodeEmpty_IllegalArgumentException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("Il valore di codice feedback inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateFeedbackDto_WhenInvalidCodeContainsSpaces_IllegalArgumentException(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123 ");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("Il valore inserito di codice feedback non puo' contenere spazi.", exception.getMessage());
    }

    @Test
    public void updateFeedbackDto_WhenInvalidDescriptionNull_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription(null);
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("La descrizione non puo' essere nulla o contenere solo spazi.", exception.getMessage());
    }

    @Test
    public void updateFeedbackDto_WhenInvalidDescriptionEmpty_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);
        
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("");
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("La descrizione non puo' essere nulla o contenere solo spazi.", exception.getMessage());
    }

    @Test
    public void updateFeedbackDto_WhenInvalidDescriptionBlank_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);
        
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
    
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription(" ");
        feedbackDto.setCartItemDto(cartItemDto);
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("La descrizione non puo' essere nulla o contenere solo spazi.", exception.getMessage());
    }

    @Test
    public void updateFeedbackDto_WhenInvalidFeedbackDto_NotFoundException(){
        //Setup
        FeedbackDto feedbackDto =  null;
        
        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.updateFeedbackDto(feedbackDto));
        assertEquals("Il feedback non puo' essere nullo.", exception.getMessage());
    }

    @Test
    public void deleteFeedback_WhenInvalidCodeNull_NotFoundException(){
        //Setup
        String code = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.deleteFeedback(code));
            assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void deleteFeedback_WhenInvalidCodeEmpty_NotFoundException(){
        //Setup
        String code = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.deleteFeedback(code));
        assertEquals("Il codice inserito e' nullo o vuoto.", exception.getMessage());
    }


    @Test
    public void deleteFeedback_WhenValidInput_Success(){
        //Setup
        String code = "123";
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        Feedback feedback = new Feedback(feedbackDto);
        ArgumentCaptor<Feedback> feedbackCaptor = ArgumentCaptor.forClass(Feedback.class);
        
        //Mock
        when(feedbackRepository.findByCode(anyString())).thenReturn(Optional.of(feedback));
        doAnswer(invocation -> {
            Feedback feedbackDeleted = invocation.getArgument(0);
            return feedbackDeleted;
        }).when(feedbackRepository).delete(any(Feedback.class));

        //Test
        feedbackServiceImpl.deleteFeedback(code);
        verify(feedbackRepository, times(1)).findByCode(code);
        verify(feedbackRepository, times(1)).delete(feedbackCaptor.capture());

        FeedbackDto feedbackDtoDeleted = new FeedbackDto(feedbackCaptor.getValue());
        assertEquals(feedbackDto, feedbackDtoDeleted);
    }

    @Test
    public void deleteFeedback_WhenInvalidFeedback_NotFoundException(){
        //Setup
        String code = "123";
        
        //Mock
        when(feedbackRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.deleteFeedback(code));
        assertEquals("Non e' stato trovato nessun feedback con questo codice: " + code, exception.getMessage());

        verify(feedbackRepository, times(1)).findByCode(code);
    }

    @Test
    public void getFeedbackListUser_WhenValidInput_ReturnList(){
        //Setup
        String username = "andrysea";

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        Feedback feedback = new Feedback(feedbackDto);

        List<Feedback> listFeedback = new ArrayList<>();
        listFeedback.add(feedback);

        List<FeedbackDto> listFeedbackDto =  listFeedback.stream().map(entity -> new FeedbackDto(entity)).collect(Collectors.toList());
        
        //Mock
        when(feedbackRepository.findAllByClientUsername(anyString())).thenReturn(listFeedback);

        //Test
        List<FeedbackDto> listFeedbackReturned = feedbackServiceImpl.getFeedbackListUser(username);
        verify(feedbackRepository, times(1)).findAllByClientUsername(username);

        assertEquals(listFeedbackDto, listFeedbackReturned);
    }

    @Test
    public void getFeedbackListUser_WhenValidInput_NoSuchElementException(){
        //Setup
        String username = "andrysea";
        List<Feedback> listFeedback = new ArrayList<>();
    
        //Mock
        when(feedbackRepository.findAllByClientUsername(anyString())).thenReturn(listFeedback);

        //Test
        assertThrows(NoSuchElementException.class, () ->
            feedbackServiceImpl.getFeedbackListUser(username));

        verify(feedbackRepository, times(1)).findAllByClientUsername(username);
    }

    @Test
    public void getFeedbackListUser_WhenInvalidUsernameNull_NotFoundException(){
        //Setup
        String username = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.getFeedbackListUser(username));
        assertEquals("Lo username inserito è nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getFeedbackListUser_WhenInvalidUsernameEmpty_NotFoundException(){
        //Setup
        String username = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            feedbackServiceImpl.getFeedbackListUser(username));
        assertEquals("Lo username inserito è nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void getFeedbackList_WhenValidInput_ReturnList(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        ProductDto productDto = new ProductDto();
        productDto.setCode("123");
        productDto.setCategoryDto(categoryDto);
        
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        cartItemDto.setProductDto(productDto);
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        Feedback feedback = new Feedback(feedbackDto);

        List<Feedback> listFeedback = new ArrayList<>();
        listFeedback.add(feedback);

        List<FeedbackDto> listFeedbackDto =  listFeedback.stream().map(entity -> new FeedbackDto(entity)).collect(Collectors.toList());
        
        //Mock
        when(feedbackRepository.findAll()).thenReturn(listFeedback);

        //Test
        List<FeedbackDto> listFeedbackReturned = feedbackServiceImpl.getFeedbackList();
        verify(feedbackRepository, times(1)).findAll();

        assertEquals(listFeedbackDto, listFeedbackReturned);
    }

    @Test
    public void getFeedbackList_WhenValidInput_NoSuchElementException(){
        //Setup
        List<Feedback> listFeedback = new ArrayList<>();
    
        //Mock
        when(feedbackRepository.findAll()).thenReturn(listFeedback);

        //Test
        assertThrows(NoSuchElementException.class, () ->
            feedbackServiceImpl.getFeedbackList());
        
        verify(feedbackRepository, times(1)).findAll();
    }
}
