package com.andreamarino.pharmazon.model.designPattern.observer.service;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

public interface ObserverService {
    void updateService(EmailService emailService, String nameService, String date);
}
