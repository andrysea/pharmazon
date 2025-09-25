package com.andreamarino.pharmazon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.andreamarino.pharmazon.dto.CategoryDto;

public class CategoryTest {
    
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    public void conversionDto(){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Antinfiammatorio");
        categoryDto.setCode("123");
        
        category = new Category(categoryDto);
    }

    @Test
    public void allArgsConstructor(){
        String name = "Antinfiammatorio";
        String code = "123";
        category = new Category(1L, "123", "Antinfiammatorio", new ArrayList<>());

        assertEquals(name, category.getName());
        assertEquals(code, category.getCode());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(category);
    }

    @Test
    public void getterAndSetter(){
        String name = "Antimicotico";
        String code = "123";
        Long id = 1L;

        Product product = new Product();
        product.setName("Crema idratante");

        List<Product> listProduct = new ArrayList<>();
        listProduct.add(product);

        category.setId(id);
        category.setName("Antimicotico");
        category.setCode("123");
        category.setProducts(listProduct);

        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(code, category.getCode());
        assertEquals(listProduct, category.getProducts());
    }

    @Test
    public void toStringMethod(){
        category = new Category(1L, "Antinfiammatorio", "123", new ArrayList<>());
        
        String toStringResult = category.toString();
        assertEquals(String.format("Category(id=%s, code=%s, name=%s, products=%s)",
        category.getId(), category.getCode(), category.getName(), category.getProducts()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        category = new Category(1L, "123", "Antimicotico", new ArrayList<>());

        Category sameCategory= new Category();
        sameCategory.setId(1L);
        sameCategory.setName("Antimicotico");
        sameCategory.setCode("123");
        sameCategory.setProducts(new ArrayList<>());

        assertEquals(sameCategory, category);
        assertEquals(sameCategory.hashCode(), category.hashCode());

        category.setName("Antinfiammatorio");
        assertNotEquals(sameCategory, category);
        assertNotEquals(sameCategory.hashCode(), category.hashCode());
    }
}
