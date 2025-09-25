package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.dto.FeedbackDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.service.interfaceForClass.FeedbackService;

@ExtendWith(MockitoExtension.class)
public class FeedbackControllerTest {
    
    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    @Test
    public void insertFeedbackDto_WhenValidInput_ResponseCreated(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        //Mock
        when(feedbackService.insertFeedbackDto(any(FeedbackDto.class))).thenReturn(feedbackDto);

        //Test
        ResponseEntity<?> response = feedbackController.insertFeedbackDto(feedbackDto);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento del feedback avvenuto con successo.", responseBody.get("message"));
        assertEquals(feedbackDto, responseBody.get("feedbackDto"));
        verify(feedbackService, times(1)).insertFeedbackDto(feedbackDto);
    }

    @Test
    public void updateFeedbackDto_WhenValidInput_ResponseOk(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);

        //Mock
        when(feedbackService.updateFeedbackDto(any(FeedbackDto.class))).thenReturn(feedbackDto);

        //Test
        ResponseEntity<?> response = feedbackController.updateFeedbackDto(feedbackDto);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica del feedback avvenuta con successo.", responseBody.get("message"));
        assertEquals(feedbackDto, responseBody.get("feedbackDto"));
        verify(feedbackService, times(1)).updateFeedbackDto(feedbackDto);
    }

    @Test
    public void deleteFeedback_WhenValidInput_ResponseOk(){
         //Setup
         String code = "123";

         //Mock
         doNothing().when(feedbackService).deleteFeedback(anyString());

         //Test
         ResponseEntity<?> response = feedbackController.deleteFeedback(code);
         Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
         assertEquals(HttpStatus.OK, response.getStatusCode());
         assertEquals("Eliminazione del feedback avvenuta con successo.", responseBody.get("message"));
    }

    @Test
    public void listFeedbackUser_NoInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        List<FeedbackDto> listFeedbackDto = new ArrayList<>();
        listFeedbackDto.add(feedbackDto);
        
        //Mock
        when(feedbackService.getFeedbackListUser(anyString())).thenReturn(listFeedbackDto);

        //Test
        ResponseEntity<?> response = feedbackController.listFeedbackUser(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listFeedbackDto, response.getBody());
        verify(feedbackService, times(1)).getFeedbackListUser(username);
    }

    @Test
    public void listFeedback_NoInput_ResponseOk(){
        //Setup
        ClientDto clientDto = new ClientDto();
        clientDto.setUsername("andrysea");
        clientDto.setCreditCardsDto(new ArrayList<>());

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCode("123");
        
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setCode("123");
        feedbackDto.setDescription("Descrizione...");
        feedbackDto.setClientDto(clientDto);
        feedbackDto.setCartItemDto(cartItemDto);
        
        List<FeedbackDto> listFeedbackDto = new ArrayList<>();
        listFeedbackDto.add(feedbackDto);
        
        //Mock
        when(feedbackService.getFeedbackList()).thenReturn(listFeedbackDto);

        //Test
        ResponseEntity<?> response = feedbackController.listFeedback();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listFeedbackDto, response.getBody());
        verify(feedbackService, times(1)).getFeedbackList();
    }
}
