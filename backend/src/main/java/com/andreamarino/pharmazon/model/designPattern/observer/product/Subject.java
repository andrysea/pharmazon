package com.andreamarino.pharmazon.model.designPattern.observer.product;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

public interface Subject {
    void addObserver(ObserverProduct observer);
    void removeObserver(ObserverProduct observer);
    void notifyObservers(EmailService emailService);
}
