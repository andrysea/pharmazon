package com.andreamarino.pharmazon.services.email.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.services.mail.service.EmailServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;
    
    @InjectMocks
    private EmailServiceImpl emailServiceImpl;

    @Test
    public void sendSimpleEmail_WhenValidInput_Success(){
        //Setup
        String to = "user@user.com";
        String subject = "EMAIL DI CONFERMA";
        String text = "Gentile cliente, le confermo che...";

        //Test
        emailServiceImpl.sendSimpleEmail(to, subject, text);
    }

    @Test
    public void sendSimpleEmail_WhenInvalidInputToNull_NotFoundException(){
        //Setup
        String to = null;
        String subject = "EMAIL DI CONFERMA";
        String text = "Gentile cliente, le confermo che...";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            emailServiceImpl.sendSimpleEmail(to, subject, text));
        assertEquals("Il destinatario e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void sendSimpleEmail_WhenInvalidInputToEmpty_NotFoundException(){
        //Setup
        String to = "";
        String subject = "EMAIL DI CONFERMA";
        String text = "Gentile cliente, le confermo che...";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            emailServiceImpl.sendSimpleEmail(to, subject, text));
        assertEquals("Il destinatario e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void sendSimpleEmail_WhenInvalidInputSubjectNull_NotFoundException(){
        //Setup
        String to = "user@user.com";
        String subject = null;
        String text = "Gentile cliente, le confermo che...";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            emailServiceImpl.sendSimpleEmail(to, subject, text));
        assertEquals("Il soggetto e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void sendSimpleEmail_WhenInvalidInputSubjectEmpty_NotFoundException(){
        //Setup
        String to = "user@user.com";
        String subject = "";
        String text = "Gentile cliente, le confermo che...";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            emailServiceImpl.sendSimpleEmail(to, subject, text));
        assertEquals("Il soggetto e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void sendSimpleEmail_WhenInvalidInputTextNull_NotFoundException(){
        //Setup
        String to = "user@user.com";
        String subject = "EMAIL DI CONFERMA";
        String text = null;

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            emailServiceImpl.sendSimpleEmail(to, subject, text));
        assertEquals("Il testo e' nullo o vuoto.", exception.getMessage());
    }

    @Test
    public void sendSimpleEmail_WhenInvalidInputTextEmpty_NotFoundException(){
        //Setup
        String to = "user@user.com";
        String subject = "EMAIL DI CONFERMA";
        String text = "";

        //Test
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            emailServiceImpl.sendSimpleEmail(to, subject, text));
        assertEquals("Il testo e' nullo o vuoto.", exception.getMessage());
    }
}
