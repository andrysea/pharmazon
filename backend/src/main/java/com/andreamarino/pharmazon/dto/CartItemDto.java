package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private ProductDto productDto;
    private Integer quantity;
    private byte[] imagePrescription;
    private String code;
    private String name;
    private Double price;

    public CartItemDto(CartItem cartItem){
        this.productDto = new ProductDto(cartItem.getProduct());
        this.quantity = cartItem.getQuantity();
        this.code = cartItem.getCode();
        this.name = cartItem.getName();
        this.price = cartItem.getPrice();
        this.imagePrescription = cartItem.getImagePrescription();
    }
}
