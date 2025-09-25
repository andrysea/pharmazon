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
import com.andreamarino.pharmazon.service.interfaceForClass.CategoryService;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    public void insertProductDto_WhenValidInput_ResponseCreated(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        //Mock
        when(categoryService.insertCategoryDto(any(CategoryDto.class))).thenReturn(categoryDto);

        //Test
        ResponseEntity<?> response = categoryController.insertCategoryDto(categoryDto);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento della categoria avvenuto con successo.", responseBody.get("message"));
        assertEquals(categoryDto, responseBody.get("categoryDto"));
        verify(categoryService, times(1)).insertCategoryDto(categoryDto);
    }

    @Test
    public void updateCategoryDto_WhenValidInput_ResponseOk(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");

        //Mock
        when(categoryService.updateCategoryDto(any(CategoryDto.class))).thenReturn(categoryDto);

        //Test
        ResponseEntity<?> response = categoryController.updateCategoryDto(categoryDto);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica della categoria avvenuto con successo.", responseBody.get("message"));
        assertEquals(categoryDto, responseBody.get("categoryDto"));
        verify(categoryService, times(1)).updateCategoryDto(categoryDto);
    }

    @Test
    public void getCategoryDto_NoInput_ResponseOk(){
        //Setup
        List<CategoryDto> listCategoryDto = new ArrayList<>();
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        listCategoryDto.add(categoryDto);

        //Mock
        when(categoryService.getCategoryDto()).thenReturn(listCategoryDto);

        //Test
        ResponseEntity<?> response = categoryController.getCategoryDto();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listCategoryDto, response.getBody());
        verify(categoryService, times(1)).getCategoryDto();
    }
    
}
