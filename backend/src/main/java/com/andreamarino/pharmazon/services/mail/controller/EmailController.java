package com.andreamarino.pharmazon.services.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;


    /**
     * Invia un'email all'indirizzo specificato con informazioni sull'ordine.
     * 
     * @param email l'indirizzo email del destinatario
     * @param orderNumber il numero dell'ordine
     * @param message il messaggio da includere nell'email
     * @return una stringa che conferma l'avvenuto invio dell'email
     */
    @GetMapping("/send")
    public String sendEmail(@RequestParam String email, @RequestParam String orderNumber, @RequestParam String message) {
        emailService.sendSimpleEmail(email, "Order: " + orderNumber , message);
        return "Email inviata con successo.";
    }
}
