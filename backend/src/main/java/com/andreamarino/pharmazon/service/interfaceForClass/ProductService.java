package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import com.andreamarino.pharmazon.dto.ProductDto;

public interface ProductService {
    ProductDto insertProductDto(ProductDto productDto, String username);
    ProductDto updateProductDto(ProductDto productDtoNew, String codeProduct, UserDetails userDetails);
    ProductDto getProductDtoCode(String code, UserDetails userDetails);
    List<ProductDto> getProductListDto(String username);
    List<ProductDto> getProductListDtoByName(String name, UserDetails userDetails);
    List<ProductDto> getProductListDtoByCategory(String name, UserDetails userDetails);
    void activateProductDto(String code);
}
