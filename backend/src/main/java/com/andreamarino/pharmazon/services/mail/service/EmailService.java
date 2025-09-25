package com.andreamarino.pharmazon.services.mail.service;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}

