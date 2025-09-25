package com.andreamarino.pharmazon.services.email.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreamarino.pharmazon.services.mail.controller.EmailController;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    public void sendEmail_WhenValidInput_ReturnObject(){
        //Setup
        String email = "user@user.com";
        String orderNumber = "1234";
        String message = "Ordine spedito!";

        //Mock
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        //Test
        String returnedString = emailController.sendEmail(email, orderNumber, message);
        assertEquals("Email inviata con successo.", returnedString);
    }
    
}
