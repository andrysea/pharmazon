package com.andreamarino.pharmazon.services.mail.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;

    @Async
    public void sendSimpleEmail(String to, String subject, String text) throws MailException{
        if(to == null || to.isEmpty()){
            throw new NotFoundException("Il destinatario e' nullo o vuoto.");
        }

        if(subject == null || subject.isEmpty()){
            throw new NotFoundException("Il soggetto e' nullo o vuoto.");
        }
        
        if(text == null || text.isEmpty()){
            throw new NotFoundException("Il testo e' nullo o vuoto.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}