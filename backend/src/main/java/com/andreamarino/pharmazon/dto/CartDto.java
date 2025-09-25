package com.andreamarino.pharmazon.dto;

import java.util.List;
import java.util.stream.Collectors;
import com.andreamarino.pharmazon.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    
    private List<CartItemDto> cartItemsDto;
    private ClientDto clientDto;

    public CartDto(Cart cart){
        this.cartItemsDto = cart.getCartItems().stream().map(entity -> new CartItemDto(entity)).collect(Collectors.toList());
        this.clientDto = new ClientDto(cart.getClient());
    }
}
