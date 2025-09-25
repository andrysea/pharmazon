package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.CategoryDto;

public interface CategoryService {
   CategoryDto insertCategoryDto(CategoryDto categoryDto);
   CategoryDto updateCategoryDto(CategoryDto categoryDto);
   List<CategoryDto> getCategoryDto();
}
