package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.OrderDto;

public interface OrderService {
     OrderDto insertOrderDto(OrderDto orderDto, String username);
     OrderDto updateOrderDto(OrderDto orderDto, boolean flag);
     void approvedOrNotOrder(String code, Boolean value);
     List<OrderDto> getListOrderWithoutSpecificState();
     List<OrderDto> getListOrderWaiting();
     List<OrderDto> getListOrderHistory();
     List<OrderDto> getListOrderUser(String username);
}
