package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
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
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.service.interfaceForClass.ServiceService;

@ExtendWith(MockitoExtension.class)
public class ServiceControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    @Test
    public void insertServiceDto_WhenValidInput_ResponseCreated(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername(username);
        pharmacist.setEmail("user@user.com");

        //Mock
        when(serviceService.insertServiceDto(any(ServiceDto.class), anyString())).thenReturn(serviceDto);

        //Test
        ResponseEntity<?> response = serviceController.insertServiceDto(username, serviceDto, pharmacist);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inserimento del servizio avvenuto con successo.", responseBody.get("message"));
        assertEquals(serviceDto, responseBody.get("serviceDto"));
        verify(serviceService, times(1)).insertServiceDto(serviceDto, username);
    }

    @Test
    public void insertServiceDto_WhenValidInput_ResponseForbidden(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername("username");
        pharmacist.setEmail("user@user.com");

        //Test
        ResponseEntity<?> response = serviceController.insertServiceDto(username, serviceDto, pharmacist);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Username inserito come parametro, non e' conforme all'username legato al token.", responseBody.get("message"));
    }

    @Test
    public void updateServiceDto_WhenValidInput_ResponseOk(){
        //Setup
        String oldCode = "456";
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setUsername("username");
        pharmacist.setEmail("user@user.com");

        //Mock
        when(serviceService.updateServiceDto(any(ServiceDto.class), anyString(), any(Pharmacist.class))).thenReturn(serviceDto);

        //Test
        ResponseEntity<?> response = serviceController.updateServiceDto(serviceDto, oldCode, pharmacist);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Modifica del servizio avvenuta con successo.", responseBody.get("message"));
        assertEquals(serviceDto, responseBody.get("serviceDto"));
        verify(serviceService, times(1)).updateServiceDto(serviceDto, oldCode, pharmacist);
    }

    @Test
    public void getServiceDto_WhenValidInput_ResponseOk(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String username = "andrysea";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode("123");
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        List<ServiceDto> listServiceDto = new ArrayList<>();
        listServiceDto.add(serviceDto);
      
        //Mock
        when(serviceService.getServiceListDto(anyString())).thenReturn(listServiceDto);

        //Test
        ResponseEntity<?> response = serviceController.getServiceDto(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listServiceDto, response.getBody());
        verify(serviceService, times(1)).getServiceListDto(username);
    }

    @Test
    public void getServiceDtoByCode_WhenValidInput_ResponseOk(){
        //Setup
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String code = "123";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode(code);
        serviceDto.setName("Test glicemia");
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);
      
        //Mock
        when(serviceService.getServiceDto(anyString())).thenReturn(serviceDto);

        //Test
        ResponseEntity<?> response = serviceController.getServiceDtoByCode(code);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceDto, response.getBody());
        verify(serviceService, times(1)).getServiceDto(code);
    }

    @Test
    public void getServiceDtoByName_WhenValidInput_ResponseOk(){
        //Setup
        String username = "andrysea";
        String name = "Test glicemia";
        byte[] imageJpg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        String code = "123";
        
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1);

        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setCode(code);
        serviceDto.setName(name);
        serviceDto.setDescription("Questo test serve a misurare il livello di glucosio nel sangue.");
        serviceDto.setPrice(12.5);
        serviceDto.setAvailability(5);
        serviceDto.setImage(imageJpg);
        serviceDto.setDateChosen(localDateTime);

        List<ServiceDto> listServiceDto = new ArrayList<>();
        listServiceDto.add(serviceDto);
      
        //Mock
        when(serviceService.getServiceListDtoName(anyString(), anyString())).thenReturn(listServiceDto);

        //Test
        ResponseEntity<?> response = serviceController.getServiceDtoByName(username, name);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listServiceDto, response.getBody());
        verify(serviceService, times(1)).getServiceListDtoName(username, name);
    }

    @Test
    public void delete_WhenValidInput_ResponseOk(){
        //Setup
        String code = "123";
        
        //Mock
        doNothing().when(serviceService).deleteServiceDto(anyString());

        //Test
        ResponseEntity<?> response = serviceController.deleteServiceDto(code);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Eliminazione del servizio avvenuta con successo.", responseBody.get("message"));
    }
    
}
