package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.service.interfaceForClass.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    public void insertProductDto_WhenValidInput_ResponseCreated(){
        //Setup
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

        //Mock
        when(productService.insertProductDto(any(ProductDto.class), anyString())).thenReturn(productDto);

        //Test
        ResponseEntity<?> response = productController.insertProductDto(productDto, username, pharmacist);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento del prodotto avvenuto con successo.", responseBody.get("message"));
        assertEquals(productDto, responseBody.get("productDto"));
        verify(productService, times(1)).insertProductDto(productDto, username);
    }

    @Test
    public void insertProductDto_WhenValidInput_ResponseForbidden(){
        //Setup
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, jpgHeader,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername("ciao01");
        pharmacist.setEmail("user@user.com");

        //Test
        ResponseEntity<?> response = productController.insertProductDto(productDto, username, pharmacist);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Username inserito come parametro, non e' conforme all'username legato al token.", responseBody.get("message"));
    }

    @Test
    public void updateProductDto_WhenValidInput_ResponseOk(){
        //Setup
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String oldCode = "123";
        String username = "andrysea";

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "456", "Shampoo per capelli.", true, 8.75, jpgHeader,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        //Mock
        when(productService.updateProductDto(any(ProductDto.class), anyString(), any(Pharmacist.class))).thenReturn(productDtoNew);

        //Test
        ResponseEntity<?> response = productController.updateProductDto(productDtoNew, oldCode, pharmacist);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica del prodotto avvenuto con successo.", responseBody.get("message"));
        assertEquals(productDtoNew, responseBody.get("productDto"));
        verify(productService, times(1)).updateProductDto(productDtoNew, oldCode, pharmacist);
    }

    @Test
    public void getProductDto_WhenValid_ResponseOk(){
        //Setup
        String username = "andrysea";
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", "456", "Shampoo per capelli.", true, 8.75, jpgHeader,
                "Gargnier", "Fosfato", true, 2, categoryDto);
        
        List<ProductDto> listProductDto = new ArrayList<>();
        listProductDto.add(productDtoNew);

        //Mock
        when(productService.getProductListDto(username)).thenReturn(listProductDto);

        //Test
        ResponseEntity<?> response = productController.getProductDto(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listProductDto, response.getBody());
        verify(productService, times(1)).getProductListDto(username);
    }

    @Test
    public void getProductDtoName_WhenValid_ResponseOk(){
        //Setup
        String name = "Shampoo";
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDtoNew = new ProductDto(name, "456", "Shampoo per capelli.", true, 8.75, jpgHeader,
                "Gargnier", "Fosfato", true, 2, categoryDto);
        
        List<ProductDto> listProductDto = new ArrayList<>();
        listProductDto.add(productDtoNew);

        Client client = new Client();
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productService.getProductListDtoByName(anyString(), any(Client.class))).thenReturn(listProductDto);

        //Test
        ResponseEntity<?> response = productController.getProductDtoName(name, client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listProductDto, response.getBody());
        verify(productService, times(1)).getProductListDtoByName(name, client);
    }

    @Test
    public void getProductDtoCode_WhenValid_ResponseOk(){
        //Setup
        String code = "123";
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDtoNew = new ProductDto("Shampoo", code, "Shampoo per capelli.", true, 8.75, jpgHeader,
                "Gargnier", "Fosfato", true, 2, categoryDto);

        Client client = new Client();
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productService.getProductDtoCode(anyString(), any(Client.class))).thenReturn(productDtoNew);

        //Test
        ResponseEntity<?> response = productController.getProductDtoCode(code, client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDtoNew, response.getBody());
        verify(productService, times(1)).getProductDtoCode(code, client);
    }

    @Test
    public void getProductDtoCategory_WhenValid_ResponseOk(){
        //Setup
        String name = "Antimicotico";
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        CategoryDto categoryDto = new CategoryDto(name, "123");
        Category category = new Category(categoryDto);
        category.setId(1L);
        
        ProductDto productDtoNew = new ProductDto(name, "456", "Shampoo per capelli.", true, 8.75, jpgHeader,
                "Gargnier", "Fosfato", true, 2, categoryDto);
        
        List<ProductDto> listProductDto = new ArrayList<>();
        listProductDto.add(productDtoNew);

        Client client = new Client();
        client.setRole(Role.CLIENT);
        client.setEmail("user@user.com");

        //Mock
        when(productService.getProductListDtoByCategory(anyString(), any(Client.class))).thenReturn(listProductDto);

        //Test
        ResponseEntity<?> response = productController.getProductDtoCategory(name, client);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listProductDto, response.getBody());
        verify(productService, times(1)).getProductListDtoByCategory(name, client);
    }

    @Test
    public void deleteProductDto_WhenValidInput_ResponseOk(){
         //Setup
         String code = "123";

         //Mock
         doNothing().when(productService).activateProductDto(anyString());

         //Test
         ResponseEntity<?> response = productController.activateProductDto(code);
         Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
         assertEquals(HttpStatus.OK, response.getStatusCode());
         assertEquals("Cambiamento di stato del prodotto avvenuto con successo.", responseBody.get("message"));
    }
}
