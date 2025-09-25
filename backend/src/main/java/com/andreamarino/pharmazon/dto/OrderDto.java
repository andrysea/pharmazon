package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private CartDto cartDto = new CartDto();
    private String code;
    private Double total;
    private StateDto stateDto;
    private AddressDto addressDto;
    private ClientDto clientDto;

    public OrderDto(Order order){
        this.cartDto = new CartDto(order.getCart());
        this.code = order.getCode();
        this.addressDto = new AddressDto(order.getAddress());
        this.total = order.getTotal();
        this.stateDto = new StateDto(order.getState());
        this.clientDto = new ClientDto(order.getClient());
    }    
}
