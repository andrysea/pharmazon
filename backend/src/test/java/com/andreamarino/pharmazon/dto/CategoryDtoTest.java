package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Category;

@ExtendWith(MockitoExtension.class)
public class CategoryDtoTest {

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
    }

    @Test
    public void conversionDto(){
        Category category = new Category();
        category.setName("Antinfiammatorio");
        category.setCode("123");
        
        categoryDto = new CategoryDto(category);
    }

    @Test
    public void allArgsConstructor(){
        String name = "Antinfiammatorio";
        String code = "123";
        categoryDto = new CategoryDto("Antinfiammatorio", "123");

        assertEquals(name, categoryDto.getName());
        assertEquals(code, categoryDto.getCode());
    }

    @Test
    void noArgsConstructor() {
        assertNotNull(categoryDto);
    }

    @Test
    public void getterAndSetter(){
        String name = "Antimicotico";
        String code = "123";

        categoryDto.setName("Antimicotico");
        categoryDto.setCode("123");

        assertEquals(name, categoryDto.getName());
        assertEquals(code, categoryDto.getCode());
    }

    @Test
    public void toStringMethod(){
        categoryDto = new CategoryDto("Antinfiammatorio", "123");
        
        String toStringResult = categoryDto.toString();
        assertEquals(String.format("CategoryDto(name=%s, code=%s)",
        categoryDto.getName(), categoryDto.getCode()), toStringResult);
    }

    @Test
    void testEqualsAndHashCode() {
        categoryDto = new CategoryDto("Antimicotico", "123");

        CategoryDto sameCategoryDto= new CategoryDto();
        sameCategoryDto.setName("Antimicotico");
        sameCategoryDto.setCode("123");

        assertEquals(sameCategoryDto, categoryDto);
        assertEquals(sameCategoryDto.hashCode(), categoryDto.hashCode());

        categoryDto.setName("Antinfiammatorio");
        assertNotEquals(sameCategoryDto, categoryDto);
        assertNotEquals(sameCategoryDto.hashCode(), categoryDto.hashCode());
    }
}
