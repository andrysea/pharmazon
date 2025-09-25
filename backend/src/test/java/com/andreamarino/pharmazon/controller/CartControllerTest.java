package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.service.interfaceForClass.CartService;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {
    
    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    public void insertProductDto_WhenValidInput_ResponseCreated(){
        //Setup
        String username = "andrysea";
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());

        //Mock
        when(cartService.insertProductDto(any(ProductDto.class), anyString())).thenReturn(productDto);

        //Test
        ResponseEntity<?> response = cartController.insertProductDto(productDto, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento del prodotto nel carrello, avvenuto con successo.", responseBody.get("message"));
        assertEquals(productDto, responseBody.get("productDto"));
        verify(cartService, times(1)).insertProductDto(productDto, username);
    }

    @Test
    public void removeProduct_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        String code = "123";

        //Mock
        doNothing().when(cartService).removeProduct(anyString(), anyString());

        //Test
        ResponseEntity<?> response = cartController.removeProduct(username, code);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rimozione del prodotto nel carrello, avvenuto con successo.", responseBody.get("message"));
        verify(cartService, times(1)).removeProduct(username, code);
    }

    @Test
    public void getProductDtoCart_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());

        CartItemDto cartItem = new CartItemDto();
        cartItem.setQuantity(1);
        cartItem.setProductDto(productDto);
        
        List<CartItemDto> listCartItemsDto = new ArrayList<>();
        listCartItemsDto.add(cartItem);

        //Mock
        when(cartService.getCartItemsDtoCart(anyString())).thenReturn(listCartItemsDto);

        //Test
        ResponseEntity<?> response = cartController.getCartItemsDtoCart(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listCartItemsDto, response.getBody());
        verify(cartService, times(1)).getCartItemsDtoCart(username);
    }

    @Test
    public void getProductListDeliveredClient_WhenValid_ResponseOk(){
        //Setup
        String username = "andrysea";
        
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", false, 8.75, new byte[10], "Gargnier", "Fosfato", true, 2, new CategoryDto());

        CartItemDto cartItem = new CartItemDto();
        cartItem.setQuantity(1);
        cartItem.setProductDto(productDto);
        
        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItem);

        //Mock
        when(cartService.getCartItemListDeliveredClient(anyString())).thenReturn(listCartItemDto);

        //Test
        ResponseEntity<?> response = cartController.getCartItemListDeliveredClient(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listCartItemDto, response.getBody());
        verify(cartService, times(1)).getCartItemListDeliveredClient(username);
    }

    @Test
    public void deleteCart_WhenValidInput_ResponseOk(){
        //Setup
        Long id = 1L;

        //Mock
        doNothing().when(cartService).deleteCart(anyLong());

        //Test
        ResponseEntity<?> response = cartController.deleteCart(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Il carrello è stato eliminato.", responseBody.get("message"));
        verify(cartService, times(1)).deleteCart(id);
    }
}
