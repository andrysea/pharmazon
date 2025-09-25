package com.andreamarino.pharmazon.service.implementationClass;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.repository.CategoryRepository;
import com.andreamarino.pharmazon.service.interfaceForClass.CategoryService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto insertCategoryDto(CategoryDto categoryDto) { 
        if(categoryDto == null){
            throw new NotFoundException("L'oggetto relativo alla categoria non può essere nullo.");
        }
        
        ValidationUtils.containsOnlyLetters(categoryDto.getName(), "nome categoria");
        Category category = categoryRepository.findByName(categoryDto.getName())
        .orElse(null);

        if(category != null){
            throw new DuplicateException("La categoria che vuoi inserire, e' gia' presente.");
        }
        
        categoryDto.setCode(this.generateCode());
        category = new Category(categoryDto);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    @Transactional
    public CategoryDto updateCategoryDto(CategoryDto categoryDto) {
        if(categoryDto == null){
            throw new NotFoundException("L'oggetto relativo alla categoria non può essere nullo.");
        }

        if(categoryDto.getCode() == null || categoryDto.getCode().isEmpty()){
            throw new NotFoundException("Il codice relativo alla categoria non può essere nullo o vuoto.");
        }

        Category category = categoryRepository.findByCode(categoryDto.getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stata trovata nessuna categoria con questo codice: " + categoryDto.getCode()));

        ValidationUtils.containsOnlyLetters(categoryDto.getName(), "nome categoria");
        
        if(categoryRepository.findByName(categoryDto.getName()).isPresent()){
            throw new IllegalArgumentException("E' stata trovata un'altra categoria con questo nome: " + categoryDto.getName());
        }
        
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> getCategoryDto() {
        List<Category> listCategory = categoryRepository.findAll();
        if (listCategory.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<CategoryDto> listCategoryDto = listCategory.stream().map(entity -> new CategoryDto(entity)).collect(Collectors.toList());
        return listCategoryDto;
    }

    private String generateCode() { 
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    } 
}
