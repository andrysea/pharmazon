package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String name;
    private String code; 
    private String description;
    private Boolean prescription;
    private Double price;
    private byte[] image;
    private String producer;
    private String activeIngredient;
    private Boolean activeProduct;
    private Integer quantity;
    private CategoryDto categoryDto;

    public ProductDto(Product product){
        this.name = product.getName();
        this.code = product.getCode();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.image = product.getImage();
        this.producer = product.getProducer();
        this.activeIngredient = product.getActiveIngredient();
        this.activeProduct = product.getActiveProduct();
        this.quantity = product.getQuantity();
        this.prescription = product.getPrescription();
        this.categoryDto = new CategoryDto(product.getCategory());
    }

}
