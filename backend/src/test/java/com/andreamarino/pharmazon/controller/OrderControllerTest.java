package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.andreamarino.pharmazon.dto.CartDto;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.FeedbackDto;
import com.andreamarino.pharmazon.dto.OrderDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.dto.StateDto;
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.designPattern.state.StateInTransit;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.service.interfaceForClass.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;


    @Test
    public void insertOrderDto_WhenValidInput_ResponseCreated(){
        //Setup
        String username = "andrysea";
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(stateWatingApproval));

        //Mock
        when(orderService.insertOrderDto(any(OrderDto.class), anyString())).thenReturn(orderDto);

        //Test
        ResponseEntity<?> response = orderController.insertOrderDto(orderDto, username);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento dell'ordine avvenuto con successo.", responseBody.get("message"));
        assertEquals(orderDto, responseBody.get("orderDto"));
        verify(orderService, times(1)).insertOrderDto(orderDto, username);
    }

    @Test
    public void updateOrderDto_WhenValidInput_ResponseOk(){
        //Setup
        boolean flag = false;
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);
        cartItemDto.setImagePrescription(pngHeader);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        CartDto cartDto = new CartDto();
        cartDto.setClientDto(clientDto);
        cartDto.setCartItemsDto(listCartItemDto);

        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();

        OrderDto orderDto = new OrderDto();
        orderDto.setTotal(productDto.getPrice());
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(stateWatingApproval));

        //Mock
        when(orderService.updateOrderDto(any(OrderDto.class), anyBoolean())).thenReturn(orderDto);

        //Test
        ResponseEntity<?> response = orderController.updateOrderDto(orderDto, flag);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica dell'ordine avvenuta con successo.", responseBody.get("message"));
        assertEquals(orderDto, responseBody.get("orderDto"));
        verify(orderService, times(1)).updateOrderDto(orderDto, flag);
    }

    @Test
    public void getListOrderWithoutSpecificState_WhenValidInput_ResponseList(){
        //Setup
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");

        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setRole(Role.CLIENT);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);
        cartDto.setClientDto(clientDto);

        StatePreparation statePreparation = new StatePreparation();

        OrderDto orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(statePreparation));

        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto.add(orderDto);

        //Mock
        when(orderService.getListOrderWithoutSpecificState()).thenReturn(listOrderDto);

        //Test
        ResponseEntity<?> response = orderController.getListOrderWithoutSpecificState();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listOrderDto, response.getBody());
        verify(orderService, times(1)).getListOrderWithoutSpecificState();
    }

    @Test
    public void getListOrderHistory_WhenValidInput_ResponseList(){
         
        //Setup
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setRole(Role.CLIENT);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);
        cartDto.setClientDto(clientDto);

        StateWaitingApproval stateWaitingApproval = new StateWaitingApproval();
    
        OrderDto orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(stateWaitingApproval));

        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto.add(orderDto);

        //Mock
        when(orderService.getListOrderHistory()).thenReturn(listOrderDto);

        //Test
        ResponseEntity<?> response = orderController.getListOrderHistory();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listOrderDto, response.getBody());
        verify(orderService, times(1)).getListOrderHistory();
    }

    @Test
    public void getListOrderWaiting_WhenValidInput_ResponseList(){
         
        //Setup
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setRole(Role.CLIENT);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);
        cartDto.setClientDto(clientDto);

        StateWaitingApproval stateWaitingApproval = new StateWaitingApproval();
        
        StateInTransit stateInTransit = new StateInTransit();

        OrderDto orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(stateWaitingApproval));

        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto.add(orderDto);

        //Mock
        when(orderService.getListOrderWaiting()).thenReturn(listOrderDto);

        //Test
        ResponseEntity<?> response = orderController.getListOrderWaiting();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listOrderDto, response.getBody());
        verify(orderService, times(1)).getListOrderWaiting();
    }

    @Test
    public void getListOrderUser_WhenValidInput_ResponseList(){
         
        //Setup
        String username = "andrysea";
        CategoryDto categoryDto = new CategoryDto("Shampoo", "123");
        ProductDto productDto = new ProductDto("Shampoo", "123", "Shampoo per capelli.", true, 8.75, new byte[10],
                "Gargnier", "Fosfato", true, 2, categoryDto);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductDto(productDto);
        cartItemDto.setQuantity(1);

        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto.add(cartItemDto);

        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setRole(Role.CLIENT);

        CartDto cartDto = new CartDto();
        cartDto.setCartItemsDto(listCartItemDto);
        cartDto.setClientDto(clientDto);

        StateWaitingApproval stateWaitingApproval = new StateWaitingApproval();
        
        OrderDto orderDto = new OrderDto();
        orderDto.setCartDto(cartDto);
        orderDto.setClientDto(clientDto);
        orderDto.setStateDto(new StateDto(stateWaitingApproval));

        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto.add(orderDto);

        //Mock
        when(orderService.getListOrderUser(anyString())).thenReturn(listOrderDto);

        //Test
        ResponseEntity<?> response = orderController.getListOrderUser(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listOrderDto, response.getBody());
        verify(orderService, times(1)).getListOrderUser(username);
    }

    @Test
    public void approvedOrNotOrder_WhenValidInput_void(){
         
        //Setup
        Boolean value = true;
        String code = "123";
        
        //Mock
        doNothing().when(orderService).approvedOrNotOrder(anyString(), anyBoolean());

        //Test
        ResponseEntity<?> response = orderController.approvedOrNotOrder(code, value);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica dell'ordine avvenuta con successo.", responseBody.get("message"));
        verify(orderService, times(1)).approvedOrNotOrder(code, value);
    }

}
