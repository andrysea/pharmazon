package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    private String name;
    private String code;

    public CategoryDto(Category category){
        this.name = category.getName();
        this.code = category.getCode();
    }
}
