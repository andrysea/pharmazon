package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.Product;

@ExtendWith(MockitoExtension.class)
public class ProductDtoTest {
    
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
    }

    @Test
    public void conversionDto(){
        String username = "andrysea";
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

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
        product.setImage(image);
        product.setProducer("Gargnnier");
        product.setActiveIngredient("Fosfatina");
        product.setActiveProduct(false);
        product.setQuantity(5);
        product.setCategory(category);
        product.setPharmacist(pharmacist);

        productDto = new ProductDto(product);
    }

    @Test
    public void allArgsConstructor(){
        String name = "Shampoo";
        String code = "123";
        String description = "Descrizione...";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        int quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);

        productDto = new ProductDto(name, code, description, prescription, price, image, producer, activeIngredient, activeProduct, quantity, categoryDto);

        assertEquals(name, productDto.getName());
        assertEquals(code, productDto.getCode());
        assertEquals(description, productDto.getDescription());
        assertEquals(prescription, productDto.getPrescription());
        assertEquals(price, productDto.getPrice());
        assertEquals(image, productDto.getImage());
        assertEquals(producer, productDto.getProducer());
        assertEquals(activeIngredient, productDto.getActiveIngredient());
        assertEquals(activeProduct, productDto.getActiveProduct());
        assertEquals(quantity, productDto.getQuantity());
        assertEquals(categoryDto, productDto.getCategoryDto());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(productDto);
    }

    @Test
    public void getterAndSetter(){
        String name = "Shampoo";
        String code = "123";
        String description = "Descrizione...";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        int quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);

        productDto.setName(name);
        productDto.setCode(code);
        productDto.setDescription(description);
        productDto.setPrescription(prescription);
        productDto.setPrice(price);
        productDto.setImage(image);
        productDto.setProducer(producer);
        productDto.setActiveIngredient(activeIngredient);
        productDto.setActiveProduct(activeProduct);
        productDto.setQuantity(quantity);
        productDto.setCategoryDto(categoryDto);

        assertEquals(name, productDto.getName());
        assertEquals(code, productDto.getCode());
        assertEquals(description, productDto.getDescription());
        assertEquals(prescription, productDto.getPrescription());
        assertEquals(price, productDto.getPrice());
        assertEquals(image, productDto.getImage());
        assertEquals(producer, productDto.getProducer());
        assertEquals(activeIngredient, productDto.getActiveIngredient());
        assertEquals(activeProduct, productDto.getActiveProduct());
        assertEquals(quantity, productDto.getQuantity());
        assertEquals(categoryDto, productDto.getCategoryDto());
    }

    @Test
    public void toStringMethod(){
        String name = "Shampoo";
        String code = "123";
        String description = "Descrizione...";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        int quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);

        productDto = new ProductDto(name, code, description, prescription, price, image, producer, activeIngredient, activeProduct, quantity, categoryDto);
        
        String toStringResult = productDto.toString();
        String expectedToString = String.format("ProductDto(name=%s, code=%s, description=%s, prescription=%s, price=%s, image=%s, producer=%s, activeIngredient=%s, activeProduct=%s, quantity=%s, categoryDto=%s)",
        name, code, description, prescription, price, Arrays.toString(image), producer, activeIngredient, activeProduct, quantity, categoryDto);
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        String name = "Shampoo";
        String code = "123";
        String description = "Descrizione...";
        Boolean prescription = true;
        Double price = 10.0;
        String producer = "Gargnier";
        String activeIngredient = "Zolfo";
        Boolean activeProduct = true;
        int quantity = 10;
        byte[] image = {(byte) 0xFF, (byte) 0xD8};

        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        Category category = new Category(categoryDto);
        category.setId(1L);

        productDto = new ProductDto(name, code, description, prescription, price, image, producer, activeIngredient, activeProduct, quantity, categoryDto);
        
        ProductDto sameProductDto = new ProductDto();
        sameProductDto.setName(name);
        sameProductDto.setCode(code);
        sameProductDto.setDescription(description);
        sameProductDto.setPrescription(prescription);
        sameProductDto.setPrice(price);
        sameProductDto.setImage(image);
        sameProductDto.setProducer(producer);
        sameProductDto.setActiveIngredient(activeIngredient);
        sameProductDto.setActiveProduct(activeProduct);
        sameProductDto.setQuantity(quantity);
        sameProductDto.setCategoryDto(categoryDto);
       
        assertEquals(productDto, sameProductDto);
        assertEquals(productDto.hashCode(), sameProductDto.hashCode());

        sameProductDto.setCode("456");

        assertNotEquals(productDto, sameProductDto);
        assertNotEquals(productDto.hashCode(), sameProductDto.hashCode());
    }

}
