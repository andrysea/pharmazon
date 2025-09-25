package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.ProductDto;

public interface CartService {
    ProductDto insertProductDto(ProductDto productDto, String username); 
    List<CartItemDto> getCartItemsDtoCart(String username);
    List<CartItemDto> getCartItemListDeliveredClient(String username);
    void removeProduct(String username, String code); 
    void deleteCart(Long id);
}
