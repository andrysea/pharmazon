package com.andreamarino.pharmazon.model.designPattern.observer.service;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

public interface Subject {
    void addObserver(ObserverService observer);
    void removeObserver(ObserverService observer);
    void notifyObservers(EmailService emailService, String nameService, String date);
}
