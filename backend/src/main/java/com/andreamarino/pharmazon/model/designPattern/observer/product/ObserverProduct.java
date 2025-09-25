package com.andreamarino.pharmazon.model.designPattern.observer.product;

import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

public interface ObserverProduct {
    void updateProduct(Product product, EmailService emailService);
}
