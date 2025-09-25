package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    public void insertCategoryDto_WhenValidInput_ReturnedObjectInsert(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Antinfiammatorio");

        Category category = new Category(categoryDto);
        category.setId(1L);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        //Mock
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category categorySaved = invocation.getArgument(0);
            return categorySaved;
        });


        //Test
        CategoryDto categoryDtoInsert = categoryServiceImpl.insertCategoryDto(categoryDto);
        verify(categoryRepository, times(1)).findByName(category.getName());
        verify(categoryRepository, times(1)).save(categoryCaptor.capture());

        assertEquals(categoryDtoInsert, categoryDto);
    }

    @Test
    public void insertCategoryDto_WhenValidInputObjectPresent_DuplicateException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Antinfiammatorio");

        Category category = new Category(categoryDto);
        category.setId(1L);

        //Mock
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));

        //Test
        DuplicateException exception = assertThrows(DuplicateException.class, () ->
        categoryServiceImpl.insertCategoryDto(categoryDto));
        assertEquals("La categoria che vuoi inserire, e' gia' presente.", exception.getMessage());
        
        verify(categoryRepository, times(1)).findByName(category.getName());
    }

    @Test
    public void insertCategoryDto_WhenNameNull_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(null);

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.insertCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCategoryDto_WhenNameContainsSpaces_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Anti nfiammatorio");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.insertCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' contenere spazi.", exception.getMessage());
    }

    @Test
    public void insertCategoryDto_WhenNameEmpty_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.insertCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    
    @Test
    public void insertCategoryDto_WhenNameContainsOnlySpaces_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("  ");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.insertCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void insertCategoryDto_WhenNameContainsNumber_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Anti123");

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.insertCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito deve contenere solo lettere.", exception.getMessage());
    }
    
    @Test
    public void insertCategoryDto_WhenNameCategoryDtoNull_NotFoundException(){
        //Setup
        CategoryDto categoryDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        categoryServiceImpl.insertCategoryDto(categoryDto));
        assertEquals("L'oggetto relativo alla categoria non può essere nullo.", exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenValidInput_ReturnObjectUpdated(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Antimicotico");
        categoryDto.setCode("123");

        Category category = new Category(categoryDto);
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        
        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category categorySaved = invocation.getArgument(0);
            return categorySaved;
        });

        //Test
        CategoryDto categoryDtoUpdated = categoryServiceImpl.updateCategoryDto(categoryDto);
        verify(categoryRepository, times(1)).findByCode(category.getCode());
        verify(categoryRepository, times(1)).findByName(categoryDto.getName());
        verify(categoryRepository, times(1)).save(categoryCaptor.capture());

        assertEquals(categoryDtoUpdated, categoryDto);
    }

    @Test
    public void updateCategoryDto_WhenInvalidCategory_NotFoundException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Antimicotico");
        categoryDto.setCode("123");
        
        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.empty());

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Non e' stata trovata nessuna categoria con questo codice: " + categoryDto.getCode(), exception.getMessage());
        verify(categoryRepository, times(1)).findByCode(categoryDto.getCode());
    }

    
    @Test
    public void updateCategoryDto_WhenNameIsPresent_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode("123");
        categoryDto.setName("Antinfiammatorio");

        Category category = new Category(categoryDto);

        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("E' stata trovata un'altra categoria con questo nome: " + categoryDto.getName(), exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenCodeNull_NotFoundException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode(null);

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Il codice relativo alla categoria non può essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenCodeEmpty_NotFoundException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode("");

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Il codice relativo alla categoria non può essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenNameNull_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode("123");
        categoryDto.setName(null);

        Category category = new Category(categoryDto);

        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(category));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenNameContainsSpaces_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode("123");
        categoryDto.setName("Anti nfiammatorio");

        Category category = new Category(categoryDto);

        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(category));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' contenere spazi.", exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenNameEmpty_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode("123");
        categoryDto.setName("");

        Category category = new Category(categoryDto);

        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(category));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    
    @Test
    public void updateCategoryDto_WhenNameContainsOnlySpaces_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode("123");
        categoryDto.setName("  ");

        Category category = new Category(categoryDto);

        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(category));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito non puo' essere nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenNameContainsNumber_IllegalArgumentException(){
        //Setup
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode("123");
        categoryDto.setName("Anti123");

        Category category = new Category(categoryDto);

        //Mock
        when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(category));

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("Il valore di nome categoria inserito deve contenere solo lettere.", exception.getMessage());
    }

    @Test
    public void updateCategoryDto_WhenCategoryNull_NotFoundException(){
        //Setup
        CategoryDto categoryDto = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
        categoryServiceImpl.updateCategoryDto(categoryDto));
        assertEquals("L'oggetto relativo alla categoria non può essere nullo.", exception.getMessage());
    }

    @Test
    public void getCategoryDto_WhenValidInput_ReturnedList() {
        //Setup
        CategoryDto categoryDto = new CategoryDto("Antimicotico", "123");
        List<CategoryDto> listCategoryDto = new ArrayList<>();
        listCategoryDto.add(categoryDto);

        List<Category> listCategory = listCategoryDto.stream().map(entity -> new Category(entity)).collect(Collectors.toList());

        //Mock
        when(categoryRepository.findAll()).thenReturn(listCategory);

        //Test
        List<CategoryDto> listCategoryDtoReturned = categoryServiceImpl.getCategoryDto();
        verify(categoryRepository, times(1)).findAll();
        assertEquals(listCategoryDto, listCategoryDtoReturned);
    }

    @Test
    public void getCategoryDto_WhenEmptyList_NoSuchElementException() {
        //Setup
        List<Category> listCategory = new ArrayList<>();

        //Mock
        when(categoryRepository.findAll()).thenReturn(listCategory);

        //Test
        assertThrows(NoSuchElementException.class, () ->
            categoryServiceImpl.getCategoryDto());

        verify(categoryRepository, times(1)).findAll();
    }
}
